package com.filip.versu.security;


import com.filip.versu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer // tells Spring to activate the authorization server
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {


    private static final int THIRTY_DAYS = 60 * 60 * 24 * 30;
    public static final String REALM="FILIP_REALM";

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    // configures where the identifiers that our authentication server is supplying will be stored
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private UserApprovalHandler userApprovalHandler;

    @Autowired
    private UserService userService;

    @Value("${app.client.id}")
    private String clientId;
    @Value("${app.client.secret}")
    private String clientSecret;


    @Override
    public void configure (ClientDetailsServiceConfigurer clients) throws Exception {

        clients.jdbc(dataSource).withClient (clientId)
                .secret (passwordEncoder. encode (clientSecret))
                .authorizedGrantTypes ("password", "authorization_code", "refresh_token", "implicit")
                .authorities ("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "USER")
                .scopes ("read", "write")
                .accessTokenValiditySeconds(THIRTY_DAYS)
                .refreshTokenValiditySeconds(THIRTY_DAYS);

    }

    @Override
    public void configure (AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.
                tokenStore(tokenStore).
                tokenEnhancer(jwtAccessTokenConverter).
                userApprovalHandler(userApprovalHandler).
                authenticationManager(authenticationManager).
                userDetailsService(userService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.realm(REALM);
    }


}
