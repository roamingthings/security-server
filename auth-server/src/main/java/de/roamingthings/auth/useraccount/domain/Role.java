package de.roamingthings.auth.useraccount.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/07
 */
@Entity
@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Role {
    @Id @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private UUID uuid;

    @NotEmpty
    @Column(nullable = false, unique = true)
    private String role;

    public Role(String role) {
        this.role = role;
    }
}
