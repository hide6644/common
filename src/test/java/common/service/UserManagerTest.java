package common.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import common.Constants;
import common.model.User;

public class UserManagerTest extends BaseManagerTestCase {

    @Autowired
    private UserManager mgr;

    @Autowired
    private RoleManager roleManager;

    private User user;

    @Test
    public void testGetUser() throws Exception {
        user = mgr.getUserByUsername("normaluser");
        assertNotNull(user);

        log.debug(user);
        assertEquals(1, user.getRoles().size());
    }

    @Test
    public void testSaveUser() throws Exception {
        user = mgr.getUserByUsername("normaluser");
        user.setConfirmPassword(user.getPassword());
        user.setLastName("smith");

        log.debug("saving user with updated last name: " + user);

        user = mgr.saveUser(user);
        assertEquals("smith", user.getLastName());
        assertEquals(1, user.getRoles().size());
    }

    @Test
    public void testAddAndRemoveUser() throws Exception {
        user = new User();
        user = (User) populate(user);

        user.addRole(roleManager.getRole(Constants.USER_ROLE));

        user = mgr.saveUser(user);
        assertEquals("john_elway", user.getUsername());
        assertEquals(1, user.getRoles().size());

        log.debug("removing user...");

        mgr.removeUser(user.getId().toString());

        try {
            user = mgr.getUserByUsername("john");
            fail("Expected 'Exception' not thrown");
        } catch (Exception e) {
            log.debug(e);
            assertNotNull(e);
        }
    }

    @Test
    public void testGetAll() throws Exception {
        List<User> found = mgr.getAll();
        log.debug("Users found: " + found);
        assertEquals(2, found.size());
    }
}
