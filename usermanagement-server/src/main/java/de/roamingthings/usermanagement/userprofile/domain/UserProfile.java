package de.roamingthings.usermanagement.userprofile.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/29
 */
@Entity
@Table(name = "user_profile")
@Data
@NoArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue
    private Long id;

    @Email
    @Column(name = "primary_email")
    private String primaryEmail;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_account_uuid")
    private String userAccountUuid;
}
