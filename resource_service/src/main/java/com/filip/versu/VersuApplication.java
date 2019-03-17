package com.filip.versu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableSwagger2
@EnableAsync
@EnableScheduling
//@EnableResourceServer ignoring spring security, using own implemented authorization against facebook. TODO use spring sec.
public class VersuApplication extends SpringBootServletInitializer {


    public static void main(String[] args) {
        SpringApplication.run(VersuApplication.class, args);
    }



    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public Docket swaggerSpringMvcPlugin() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("test")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build().apiInfo(new ApiInfoBuilder()
                        .title("Versu")
                        .description("Swagger UI for Versu backend")
                        .version("0.1.3")
                        .build())
                .securitySchemes(securitySchemes())
                .securityContexts(securityContext());

    }

    class PasswordTokenRequestEndpoint extends TokenRequestEndpoint {

        private final String _username;
        private final String _password;

        public PasswordTokenRequestEndpoint(String url, String clientIdName,
                                            String clientSecretName, String username, String password) {

            super(url, clientIdName, clientSecretName);
            this._username = username;
            this._password = password;
        }

        @SuppressWarnings("unused")
        public String getUsername() {
            return _username;
        }

        @SuppressWarnings("unused")
        public String getPassword() {
            return _password;
        }
    }

    /**
     * Class used for Oauth2 password grant type
     */
    class OAuth2PasswordCredentialsGrantType extends GrantType {

        @SuppressWarnings("unused")
        private final PasswordTokenRequestEndpoint _tokenRequestEndpoint;

        public OAuth2PasswordCredentialsGrantType(
                PasswordTokenRequestEndpoint tokenRequestEndpoint) {
            super("password");
            this._tokenRequestEndpoint = tokenRequestEndpoint;
        }
    }

    private List<SecurityContext> securityContext() {
        return Arrays.asList(SecurityContext.builder()
                .securityReferences(SecurityReferences())
                .build());
    }

    private List<SecurityScheme> securitySchemes() {

        ArrayList<SecurityScheme> authorizationTypes = new ArrayList<>();

			/* not sure why scopes are used for both authorization and authorization
             * type
			 * API is not using the scopes for now
			 */
        List<AuthorizationScope> authorizationScopeList = new ArrayList<>();
        authorizationScopeList.add(new AuthorizationScope("read", "read only"));
        authorizationScopeList.add(new AuthorizationScope("write", "read and write"));

        List<GrantType> grantTypes = new ArrayList<>();

        TokenRequestEndpoint tokenRequestEndpoint = new TokenRequestEndpoint("/oauth/token", "client_Id ", "client_secret");
        TokenEndpoint tokenEndpoint = new TokenEndpoint("/oauth/token", "theToken");

        PasswordTokenRequestEndpoint passwordTokenRequestEndpoint = new PasswordTokenRequestEndpoint(
                "/oauth/token", "client_Id ", "client_secret", "user", "password");
        grantTypes.add(new OAuth2PasswordCredentialsGrantType(
                passwordTokenRequestEndpoint));

        grantTypes.add(new AuthorizationCodeGrant(tokenRequestEndpoint, tokenEndpoint));

			/* OAuth authorization type with client credentials and password grant
			 * types.
			 */
        authorizationTypes.add(new OAuth("oauth2", authorizationScopeList,
                grantTypes));

        //authorizationTypes.add(oAuth);

        return authorizationTypes;
    }

    private List<SecurityReference> SecurityReferences() {

        List<SecurityReference> authorizations = new ArrayList<>();

        /* use same scopes as above */
        AuthorizationScope[] authorizationScopes = {
                new AuthorizationScope("read", "read only"),
                new AuthorizationScope("write", "read and write")};

        /* Currently we have 2 roles - user and client */
        authorizations.add(new SecurityReference("USER", authorizationScopes));
        authorizations
                .add(new SecurityReference("CLIENT", authorizationScopes));

        return authorizations;

    }


    @Bean
    SecurityConfiguration security() {
        return new SecurityConfiguration("acme", "test-app-realm", "test", "apiKey");
    }


}
