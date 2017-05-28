package de.roamingthings.auth.useraccount.service;


import de.roamingthings.auth.useraccount.domain.Role;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/07
 */
public interface RoleService {
    void addRoleIfNotExists(String role);

    Role findByRole(String role);
}
