package de.roamingthings.auth.useraccount.service;


import de.roamingthings.auth.useraccount.domain.Role;
import de.roamingthings.auth.useraccount.domain.UserAccount;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/03
 */
public interface UserService {
    UserAccount findByUsername(String username);

    void addEnabledUserWithRolesIfNotExists(String username, String password, Role... roles);
}
