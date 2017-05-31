package de.roamingthings.auth.useraccount.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/31
 */
@RestController
public class UserAccountController {

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @RequestMapping("/me")
    public Principal me(Principal user) {
        return user;
    }
}
