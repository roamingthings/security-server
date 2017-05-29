package de.roamingthings.usermanagement.userprofile.service;

import de.roamingthings.usermanagement.userprofile.domain.UserProfile;
import de.roamingthings.usermanagement.userprofile.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/29
 */
@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public Optional<UserProfile> getUserProfileByUserAccountUuid(String userAccountUuid) {
        return userProfileRepository.findByUserAccountUuid(userAccountUuid);
    }
}
