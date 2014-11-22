package common.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import common.Constants;
import common.model.Role;
import common.model.User;

public class UserDaoTest extends BaseDaoTestCase {

    @Autowired
    private UserDao dao;

    @Autowired
    private RoleDao rdao;

    @Test(expected = DataAccessException.class)
    public void testGetUserInvalid() throws Exception {
        dao.get(1000L);
    }

    @Test
    public void testGetUser() throws Exception {
        User user = dao.get(-1L);

        assertNotNull(user);
        assertEquals(1, user.getRoles().size());
        assertTrue(user.isEnabled());
    }

    @Test
    public void testGetUserPassword() throws Exception {
        User user = dao.get(-1L);
        String password = dao.getUserPassword(user.getId());
        assertNotNull(password);
        log.debug("password: " + password);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testUpdateUser() throws Exception {
        User user = dao.get(-1L);

        dao.saveUser(user);
        flush();

        user = dao.get(-1L);

        User user2 = new User();
        user2.setConfirmPassword(user.getPassword());
        user2.setEmail(user.getEmail());
        user2.setFirstName(user.getFirstName());
        user2.setLastName(user.getLastName());
        user2.setPassword(user.getPassword());
        user2.setRoles(user.getRoles());
        user2.setUsername(user.getUsername());

        dao.saveUser(user2);
    }

    @Test
    public void testAddUserRole() throws Exception {
        User user = dao.get(-1L);
        assertEquals(1, user.getRoles().size());

        Role role = rdao.getRoleByName(Constants.USER_ROLE);
        user.addRole(role);
        user.setConfirmPassword(user.getPassword());
        dao.saveUser(user);
        flush();

        user = dao.get(-1L);
        assertEquals(2, user.getRoles().size());

        user.addRole(role);
        dao.saveUser(user);
        flush();

        user = dao.get(-1L);
        assertEquals("more than 2 roles", 2, user.getRoles().size());

        user.getRoles().remove(role);
        dao.saveUser(user);
        flush();

        user = dao.get(-1L);
        assertEquals(1, user.getRoles().size());
    }

    @Test(expected = DataAccessException.class)
    public void testAddAndRemoveUser() throws Exception {
        User user = new User("testuser");
        user.setConfirmPassword("testpass");
        user.setPassword("testpass");
        user.setFirstName("Test");
        user.setLastName("Last");
        user.setEmail("testuser@appfuse.org");

        Role role = rdao.getRoleByName(Constants.USER_ROLE);
        assertNotNull(role.getId());
        user.addRole(role);

        user = dao.saveUser(user);
        flush();

        assertNotNull(user.getId());
        user = dao.get(user.getId());
        assertEquals("testpass", user.getPassword());

        dao.remove(user);
        flush();

        dao.get(user.getId());
    }

    @Test
    public void testUserExists() throws Exception {
        boolean b = dao.exists(-1L);
        assertTrue(b);
    }

    @Test
    public void testUserNotExists() throws Exception {
        boolean b = dao.exists(111L);
        assertFalse(b);
    }

    @Test
    public void testUserSearch() throws Exception {
        dao.reindex();

        List<User> found = dao.search("admin");
        assertEquals(1, found.size());
        User user = found.get(0);
        assertEquals("admin", user.getFirstName());

        user = dao.get(-2L);
        user.setConfirmPassword(user.getPassword());
        user.setFirstName("MattX");
        dao.saveUser(user);
        flush();
        flushSearchIndexes();

        found = dao.search("MattX");
        assertEquals(1, found.size());
        user = found.get(0);
        assertEquals("MattX", user.getFirstName());
    }
}
