package common.service.impl;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import common.Constants;
import common.dao.UserDao;
import common.exception.DatabaseException;
import common.model.Role;
import common.model.User;
import common.service.PasswordTokenManager;
import common.service.RoleManager;

public class UserManagerImplTest extends BaseManagerMockTestCase {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordTokenManager passwordTokenManager;

    @Mock
    private RoleManager roleManager;

    @InjectMocks
    private UserManagerImpl userManager = new UserManagerImpl();

    @Test
    public void testGetUser() throws Exception {
        User testData = new User("1");
        testData.getRoles().add(new Role("user"));

        given(userDao.get(1L)).willReturn(testData);

        User user = userManager.getUser("1");

        assertTrue(user != null);
        assert user != null;
        assertTrue(user.getRoles().size() == 1);
    }

    @Test
    public void testSaveUser() throws Exception {
        User testData = new User("1");
        testData.getRoles().add(new Role("user"));

        given(userDao.get(1L)).willReturn(testData);

        final User user = userManager.getUser("1");
        user.setLastName("smith");

        given(userDao.saveUser(user)).willReturn(user);
        given(roleManager.getRoles(testData.getRoles())).willReturn(testData.getRoles());

        User returned = userManager.saveUser(user);

        assertTrue(returned.getLastName().equals("smith"));
        assertTrue(returned.getRoles().size() == 1);
    }

    @Test
    public void testAddAndRemoveUser() throws Exception {
        User user = new User();

        user = (User) populate(user);

        Role role = new Role(Constants.USER_ROLE);
        user.addRole(role);

        given(userDao.saveUser(user)).willReturn(user);
        given(roleManager.getRoles(user.getRoles())).willReturn(user.getRoles());

        user = userManager.saveUser(user);

        assertTrue(user.getUsername().equals("john_elway"));
        assertTrue(user.getRoles().size() == 1);

        willDoNothing().given(userDao).remove(5L);
        userManager.removeUser("5");

        given(userDao.get(5L)).willReturn(null);

        user = userManager.getUser("5");

        //then
        assertNull(user);
        verify(userDao).remove(5L);
    }

    @Test
    public void testUserExistsException() {
        final User user = new User("admin");
        user.setEmail("matt@raibledesigns.com");

        willThrow(new DataIntegrityViolationException("")).given(userDao).saveUser(user);

        try {
            userManager.saveUser(user);
            fail("Expected UserExistsException not thrown");
        } catch (DatabaseException e) {
            log.debug("expected exception: " + e.getMessage());
            assertNotNull(e);
        }
    }
}
