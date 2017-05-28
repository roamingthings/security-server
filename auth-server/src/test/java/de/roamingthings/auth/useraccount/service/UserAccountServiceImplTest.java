package de.roamingthings.auth.useraccount.service;

import de.roamingthings.auth.useraccount.domain.Role;
import de.roamingthings.auth.useraccount.domain.UserAccount;
import de.roamingthings.auth.useraccount.repository.UserAccountRepository;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/07
 */
public class UserAccountServiceImplTest {
    @Test
    public void should_add_user_if_not_existing_in_repository() throws Exception {
        final UserAccountRepository userRepositoryMock = mock(UserAccountRepository.class);
        when(userRepositoryMock.existsByUsername("test")).thenReturn(false);
        final PasswordEncoder passwordEncoderMock = mock(PasswordEncoder.class);
        when(passwordEncoderMock.encode(any())).thenReturn("encoded");

        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock, passwordEncoderMock);

        userService.addEnabledUserWithRolesIfNotExists("test", "password", new Role("ADMIN"), new Role("USER"));

        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepositoryMock).existsByUsername(usernameCaptor.capture());
        assertThat(usernameCaptor.getValue(), is("test"));

        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(passwordEncoderMock).encode(passwordCaptor.capture());
        assertThat(passwordCaptor.getValue(), is("password"));

        ArgumentCaptor<UserAccount> userEntityCaptor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userRepositoryMock, times(1)).save(userEntityCaptor.capture());
        final UserAccount createdUserAccount = userEntityCaptor.getValue();
        assertNotNull(createdUserAccount);
        assertThat(createdUserAccount.getUsername(), is("test"));
        assertThat(createdUserAccount.getPasswordDigest(), is("encoded"));
        assertThat(createdUserAccount.getRoles(), containsInAnyOrder(
                hasProperty("role", is("ADMIN")),
                hasProperty("role", is("USER"))
        ));
    }

    @Test
    public void should_not_add_user_if_existing_in_repository() throws Exception {
        final UserAccountRepository userRepositoryMock = mock(UserAccountRepository.class);
        when(userRepositoryMock.existsByUsername("test")).thenReturn(true);
        final PasswordEncoder passwordEncoderMock = mock(PasswordEncoder.class);

        UserServiceImpl userService = new UserServiceImpl(userRepositoryMock, passwordEncoderMock);

        userService.addEnabledUserWithRolesIfNotExists("test", "password", new Role("ADMIN"), new Role("USER"));

        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepositoryMock).existsByUsername(usernameCaptor.capture());
        assertThat(usernameCaptor.getValue(), is("test"));

        verify(userRepositoryMock, never()).save(any(UserAccount.class));
    }

}