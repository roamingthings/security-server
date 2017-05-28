package de.roamingthings.auth.useraccount.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/03
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "user_account")
public class UserAccount {
    @Id @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private UUID uuid;

    @NotEmpty
    @Size(max = 50)
    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @NotEmpty
    @Column(name = "password_digest", length = 64, nullable = false)
    private String passwordDigest;

    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    // ~ defaults to @JoinTable(name = "USER_ROLES", joinColumns = @JoinColumn(name = "user_id"),
    //     inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JoinTable(name = "USER_ACCOUNT_ROLE", joinColumns = @JoinColumn(name = "user_account_uuid"),
            inverseJoinColumns = @JoinColumn(name = "role_uuid"))
    private Set<Role> roles;

    public UserAccount(String username, String passwordDigest, boolean enabled, Set<Role> roles) {
        this.username = username;
        this.passwordDigest = passwordDigest;
        this.enabled = enabled;
        this.roles = roles;
    }

}
