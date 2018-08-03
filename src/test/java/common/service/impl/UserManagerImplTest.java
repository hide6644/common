package common.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import common.Constants;
import common.dao.UserDao;
import common.dto.UserDetailsForm;
import common.exception.DatabaseException;
import common.model.Role;
import common.model.User;
import common.service.PasswordTokenManager;
import common.service.RoleManager;

public class UserManagerImplTest extends BaseManagerMockTestCase {

    @Mock
    private UserDao userDao;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordTokenManager passwordTokenManager;

    @Mock
    private RoleManager roleManager;

    @InjectMocks
    private UserManagerImpl userManager = new UserManagerImpl(userDao, userDetailsService, roleManager);

    @Test
    public void testGetUser() {
        User testData = new User("1");
        testData.getRoles().add(new Role("user"));

        given(userDao.getOne(1L)).willReturn(testData);

        User user = userManager.getUser("1");

        assertTrue(user != null);
        assert user != null;
        assertTrue(user.getRoles().size() == 1);
    }

    @Test
    public void testSaveUser() {
        User testData = new User("1");
        testData.getRoles().add(new Role("user"));

        given(userDao.getOne(1L)).willReturn(testData);

        User user = userManager.getUser("1");
        user.setLastName("smith");

        given(userDao.saveAndFlush(user)).willReturn(user);
        given(roleManager.getRoles(testData.getRoles())).willReturn(testData.getRoles());

        UserDetailsForm userDetailsForm = new UserDetailsForm();
        BeanUtils.copyProperties(user, userDetailsForm);
        User returned = userManager.saveUserDetails(userDetailsForm);

        assertTrue(returned.getLastName().equals("smith"));
        assertTrue(returned.getRoles().size() == 1);
    }

    @Test
    public void testAddAndRemoveUser() {
        User user = new User();

        user = (User) populate(user);

        Role role = new Role(Constants.USER_ROLE);
        user.addRole(role);

        given(userDao.saveAndFlush(user)).willReturn(user);
        given(roleManager.getRoles(user.getRoles())).willReturn(user.getRoles());

        UserDetailsForm userDetailsForm = new UserDetailsForm();
        BeanUtils.copyProperties(user, userDetailsForm);
        user = userManager.saveUserDetails(userDetailsForm);

        assertTrue(user.getUsername().equals("john_elway"));
        assertTrue(user.getRoles().size() == 1);

        willDoNothing().given(userDao).deleteById(5L);
        userManager.removeUser("5");

        given(userDao.getOne(5L)).willReturn(null);

        user = userManager.getUser("5");

        //then
        assertNull(user);
        verify(userDao).deleteById(5L);
    }

    @Test
    public void testUserExistsException() {
        User user = new User("admin");
        user.setEmail("matt@raibledesigns.com");

        willThrow(new DataIntegrityViolationException("")).given(userDao).saveAndFlush(user);

        try {
            UserDetailsForm userDetailsForm = new UserDetailsForm();
            BeanUtils.copyProperties(user, userDetailsForm);
            userManager.saveUserDetails(userDetailsForm);
            fail("Expected UserExistsException not thrown");
        } catch (DatabaseException e) {
            log.debug("expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}
