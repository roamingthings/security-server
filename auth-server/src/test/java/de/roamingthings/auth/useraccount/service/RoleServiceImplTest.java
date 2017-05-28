package de.roamingthings.auth.useraccount.service;

import de.roamingthings.auth.useraccount.domain.Role;
import de.roamingthings.auth.useraccount.repository.RoleRepository;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Alexander Sparkowsky [info@roamingthings.de]
 * @version 2017/05/07
 */
public class RoleServiceImplTest {
    @Test
    public void should_add_role_if_not_existing_in_repository() throws Exception {
        final RoleRepository roleRepositoryMock = mock(RoleRepository.class);
        when(roleRepositoryMock.existsByRole("TEST")).thenReturn(false);

        RoleServiceImpl roleService = new RoleServiceImpl(roleRepositoryMock);

        roleService.addRoleIfNotExists("TEST");

        ArgumentCaptor<String> roleCaptore = ArgumentCaptor.forClass(String.class);
        verify(roleRepositoryMock).existsByRole(roleCaptore.capture());
        assertThat(roleCaptore.getValue(), is("TEST"));

        ArgumentCaptor<Role> roleEntityCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepositoryMock, times(1)).save(roleEntityCaptor.capture());
        final Role createdRoleEntity = roleEntityCaptor.getValue();
        assertNotNull(createdRoleEntity);
        assertThat(createdRoleEntity.getRole(), is("TEST"));
    }

    @Test
    public void should_not_add_role_if_existing_in_repository() throws Exception {
        final RoleRepository roleRepositoryMock = mock(RoleRepository.class);
        when(roleRepositoryMock.existsByRole("TEST")).thenReturn(true);

        RoleServiceImpl roleService = new RoleServiceImpl(roleRepositoryMock);

        roleService.addRoleIfNotExists("TEST");

        ArgumentCaptor<String> roleCaptore = ArgumentCaptor.forClass(String.class);
        verify(roleRepositoryMock).existsByRole(roleCaptore.capture());
        assertThat(roleCaptore.getValue(), is("TEST"));

        ArgumentCaptor<Role> roleEntityCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepositoryMock, never()).save(any(Role.class));
    }


}