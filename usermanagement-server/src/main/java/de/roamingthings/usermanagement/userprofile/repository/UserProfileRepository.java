package de.roamingthings.usermanagement.userprofile.repository;

import de.roamingthings.usermanagement.userprofile.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/29
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserAccountUuid(String userAccountUuid);
}
