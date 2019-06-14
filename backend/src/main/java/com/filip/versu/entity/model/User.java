package com.filip.versu.entity.model;

import com.filip.versu.entity.dto.UserDTO;
import com.filip.versu.entity.model.abs.AbsBaseEntity;
import com.filip.versu.repository.DBHelper;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = DBHelper.TablesNames.USER)
@Where(clause = "is_deleted=0")
public class User extends AbsBaseEntity<Long> implements UserDetails {

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
    private String password;

    @Getter
    @Setter
    private String quote;

    @Getter
    @Setter
    private boolean isDeleted;

    @Getter
    @Setter
    private String profilePhotoURL;


    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    @Getter
    @Setter
    private List<UserRole> roles;

    /**
     * This is the last known location of this user.
     */
    @Getter
    @Setter
    @ManyToOne
    private GoogleLocation location;

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
            location = new GoogleLocation(other.location);
        }

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (UserRole role : roles) {
            String name = role.getName().toUpperCase();
            if (!name.startsWith("ROLE_")) {
                name = "ROLE_" + name;
            }
            authorities.add(new SimpleGrantedAuthority(name));
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "registrationTime=" + registrationTime +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", quote='" + quote + '\'' +
                ", isDeleted=" + isDeleted +
                ", profilePhotoURL='" + profilePhotoURL + '\'' +
                ", roles=" + roles +
                ", location=" + location +
                ", lastNotificationRefreshTimestamp=" + lastNotificationRefreshTimestamp +
                ", devices=" + devices +
                '}';
    }
}
