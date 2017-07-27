package common.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.jpa.JpaSystemException;

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
    public void testGetUsers() throws Exception {
        List<User> userList = dao.getAllOrderByUsername();

        assertEquals(2, userList.size());
    }

    @Test
    public void testGetUserPassword() throws Exception {
        User user = dao.get(-1L);
        String password = dao.getPasswordById(user.getId());

        assertNotNull(password);

        log.debug("password: " + password);
    }

    @Test(expected = JpaSystemException.class)
    public void testUpdateUser() throws Exception {
        User user = dao.get(-1L);

        dao.saveUser(user);

        user = dao.get(-1L);

        User newUser = new User();
        newUser.setConfirmPassword(user.getPassword());
        newUser.setEmail(user.getEmail());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setPassword(user.getPassword());
        newUser.setRoles(user.getRoles());
        newUser.setUsername(user.getUsername());

        dao.saveUser(newUser);
    }

    @Test
    public void testAddUserRole() throws Exception {
        User user = dao.get(-1L);

        assertEquals(1, user.getRoles().size());

        Role role = rdao.getByNameEquals(Constants.USER_ROLE);
        user.addRole(role);
        user.setConfirmPassword(user.getPassword());
        dao.saveUser(user);
        user = dao.get(-1L);

        assertEquals(2, user.getRoles().size());

        user.addRole(role);
        dao.saveUser(user);
        user = dao.get(-1L);

        assertEquals("more than 2 roles", 2, user.getRoles().size());

        user.getRoles().remove(role);
        dao.saveUser(user);
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

        Role role = rdao.getByNameEquals(Constants.USER_ROLE);
        assertNotNull(role.getId());
        user.addRole(role);
        user = dao.saveUser(user);

        assertNotNull(user.getId());

        user = dao.get(user.getId());

        assertEquals("testpass", user.getPassword());

        dao.remove(user);
        dao.get(user.getId());
    }

    @Test(expected = DataAccessException.class)
    public void testAddAndRemoveUserId() throws Exception {
        User user = new User("testuser");
        user.setConfirmPassword("testpass");
        user.setPassword("testpass");
        user.setFirstName("Test");
        user.setLastName("Last");
        user.setEmail("testuser@appfuse.org");

        Role role = rdao.getByNameEquals(Constants.USER_ROLE);

        assertNotNull(role.getId());

        user.addRole(role);
        user = dao.saveUser(user);

        assertNotNull(user.getId());

        user = dao.get(user.getId());

        assertEquals("testpass", user.getPassword());

        dao.remove(user.getId());
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

        List<User> userList = dao.search("admin");

        assertEquals(1, userList.size());
        assertEquals("admin", userList.get(0).getFirstName());

        User user = dao.get(-2L);
        user.setConfirmPassword(user.getPassword());
        user.setFirstName("MattX");
        dao.saveUser(user);
        dao.reindex();

        userList = dao.search("MattX");

        assertEquals(1, userList.size());
        assertEquals("MattX", userList.get(0).getFirstName());
    }
}
