package com.filip.versu.entity.model;

import com.filip.versu.entity.dto.UserDTO;
import com.filip.versu.entity.model.abs.AbsBaseEntity;
import com.filip.versu.repository.DBHelper;
import com.filip.versu.security.ExternalUserDTO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = DBHelper.TablesNames.USER)
@Where(clause = "is_deleted=0")
public class User extends AbsBaseEntity<Long> {

    public enum UserRole {
        APP_USER, USER_WITH_LINK
    }

    /**
     * The UTC time, when this user was registered
     */
    @Getter
    @Setter
    private long registrationTime;

    @Getter
    @Setter
    @Column(unique = true)
    @NotNull
    @Size(min = 3, max = 255)
    private String username;

    @Getter
    @Setter
    @Column(unique = true)
    @NotNull
    @Size(min = 6, max = 255)
    private String email;

    @Getter
    @Setter
    @Size(min = 6, max = 20)
    private String password;

    @Getter
    @Setter
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "appUser")
    private List<ExternalAccount> externalAccounts = new ArrayList<>();

    @Getter
    @Setter
    private String quote;

    @Getter
    @Setter
    private boolean isDeleted;

    @Getter
    @Setter
    private String profilePhotoURL;

    /**
     * This is the last known location of this user.
     */
    @Getter
    @Setter
    @ManyToOne
    private UserLocation location;

    /**
     * The timestamp, when the notification for this user were synchronized last time.
     */
    @Getter
    @Setter
    private long lastNotificationRefreshTimestamp;

    @Getter
    @Setter
    @OneToMany(fetch = FetchType.LAZY, cascade = {}, mappedBy = "owner")
    private List<DeviceInfo> devices = new ArrayList<>();

    @Transient
    @Getter
    @Setter
    private transient UserRole userRole;

    /**
     * This is an authorization token, for the users, who are not registered in the app.
     */
    @Transient
    @Getter
    @Setter
    private transient String secretUrl;

    public User() {
        super();
    }

    public User(UserDTO other) {
        super(other);
        this.registrationTime = other.registrationTime;
        this.username = other.username;
        this.email = other.email;
        this.password = other.password;
        this.profilePhotoURL = other.profilePhotoURL;
        this.quote = other.quote;

        if(other.location != null) {
            location = new UserLocation(other.location);
        }

    }

    public User(ExternalUserDTO externalUser) {
        this.email = externalUser.email;
        this.username = externalUser.username;
        this.profilePhotoURL = externalUser.photoURL;

        ExternalAccount externalAccount = new ExternalAccount(externalUser);
        externalAccount.setAppUser(this);
        this.externalAccounts.add(externalAccount);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        if (isDeleted != user.isDeleted) return false;
        if (username != null ? !username.equals(user.username) : user.username != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        return profilePhotoURL != null ? profilePhotoURL.equals(user.profilePhotoURL) : user.profilePhotoURL == null;

    }
}
