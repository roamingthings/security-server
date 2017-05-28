package de.roamingthings.auth.main.security;

import de.roamingthings.auth.useraccount.domain.UserAccount;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/07
 */
@Component
public class UserToUserDetails implements Converter<UserAccount, UserDetails> {
    @Override
    public UserDetails convert(UserAccount userAccount) {
        final List<SimpleGrantedAuthority> authorities = userAccount.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(toList());

        UserDetailsImpl userDetails = new UserDetailsImpl(
                userAccount.getUsername(),
                userAccount.getPasswordDigest(),
                userAccount.isEnabled(),
                authorities);

        return userDetails;
    }
}
