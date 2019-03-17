package com.filip.versu.entity.model;


import com.filip.versu.entity.model.abs.AbsBaseEntity;
import com.filip.versu.repository.DBHelper;
import com.filip.versu.security.ExternalUserDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * This class represents an account of user by an external provider (fb, google ...), which is linked to User of this app.
 */
@Entity
@Table(name = DBHelper.TablesNames.EXTERNAL_ACCOUNT)
public class ExternalAccount extends AbsBaseEntity<Long> {

    public enum ExternalAccountProvider {
        FACEBOOK, GOOGLE, ANONYM_NAME
    }


    @Getter
    @Setter
    @Column(unique = true)
    private String externalUserId;//this ID will remain the same for people who have already logged into your app

    @Getter
    @Setter
    private String username;

    @ManyToOne
    @Getter
    @Setter
    private User appUser;

    @Getter
    @Setter
    private ExternalAccountProvider provider;


    public ExternalAccount() {
        super();
    }

    public ExternalAccount(ExternalUserDTO externalUserDTO) {
        this.externalUserId = externalUserDTO.id;
        this.username = externalUserDTO.username;
        this.provider = externalUserDTO.accountProvider;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ExternalAccount that = (ExternalAccount) o;

        if (externalUserId != null ? !externalUserId.equals(that.externalUserId) : that.externalUserId != null)
            return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        return provider == that.provider;

    }

}
