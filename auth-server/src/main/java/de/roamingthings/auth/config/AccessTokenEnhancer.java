package de.roamingthings.auth.config;

import de.roamingthings.auth.useraccount.domain.UserAccount;
import de.roamingthings.auth.useraccount.repository.UserAccountRepository;
import de.roamingthings.auth.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/22
 */
@Component
public class AccessTokenEnhancer implements TokenEnhancer {
    private final UserAccountRepository userAccountRepository;

    public AccessTokenEnhancer(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String username;
        if (authentication.getPrincipal() instanceof String) {
            username = authentication.getPrincipal().toString();
        } else {
            final UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
            username = user.getUsername();
        }

        UserAccount userAccount = userAccountRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        final Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("user_uuid", userAccount.getUuid());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

        return accessToken;
    }
}