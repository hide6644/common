package common.dao;

import static common.dao.jpa.UserSpecifications.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.jpa.domain.Specification.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.JpaSystemException;

import common.Constants;
import common.model.Role;
import common.model.User;

public class UserDaoTest extends BaseDaoTestCase {

    @Autowired
    private UserDao dao;

    @Autowired
    private RoleDao rdao;

    @Test
    public void testGetUserInvalid() throws Exception {
        Assertions.assertThrows(DataAccessException.class, () -> {
            dao.getOne(1000L);
        });
    }

    @Test
    public void testGetUser() throws Exception {
        User user = dao.getOne(-1L);

        assertNotNull(user);
        assertEquals(1, user.getRoles().size());
        assertTrue(user.isEnabled());
    }

    @Test
    public void testGetUsers() {
        List<User> userList = dao.findAll(Sort.by("username"));

        assertEquals(2, userList.size());
    }

    @Test
    public void testGetUserPassword() {
        User user = dao.getOne(-1L);
        String password = dao.findPasswordById(user.getId());

        assertNotNull(password);

        log.debug("password: " + password);
    }

    @Test
    public void testUpdateUser() {
        User user = dao.getOne(-1L);

        dao.save(user);

        user = dao.getOne(-1L);

        User newUser = new User();
        newUser.setConfirmPassword(user.getPassword());
        newUser.setEmail(user.getEmail());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setPassword(user.getPassword());
        newUser.setRoles(user.getRoles());
        newUser.setUsername(user.getUsername());

        Assertions.assertThrows(JpaSystemException.class, () -> {
            dao.save(newUser);
        });
    }

    @Test
    public void testAddUserRole() {
        User user = dao.getOne(-1L);

        assertEquals(1, user.getRoles().size());

        Role role = rdao.findByName(Constants.USER_ROLE);
        user.addRole(role);
        user.setConfirmPassword(user.getPassword());
        dao.save(user);
        user = dao.getOne(-1L);

        assertEquals(2, user.getRoles().size());

        user.addRole(role);
        dao.save(user);
        user = dao.getOne(-1L);

        assertEquals(2, user.getRoles().size());

        user.removeRole(role);
        dao.save(user);
        user = dao.getOne(-1L);

        assertEquals(1, user.getRoles().size());
    }

    @Test
    public void testAddAndRemoveUser() {
        User user = new User("testuser");
        user.setConfirmPassword("testpass");
        user.setPassword("testpass");
        user.setFirstName("Test");
        user.setLastName("Last");
        user.setEmail("testuser@appfuse.org");

        Role role = rdao.findByName(Constants.USER_ROLE);
        assertNotNull(role.getId());
        user.addRole(role);
        user = dao.save(user);

        assertNotNull(user.getId());

        final User delUser = dao.getOne(user.getId());

        assertEquals("testpass", delUser.getPassword());

        dao.delete(delUser);
        Assertions.assertThrows(DataAccessException.class, () -> {
            dao.getOne(delUser.getId());
        });
    }

    @Test
    public void testAddAndRemoveUserId() {
        User user = new User("testuser");
        user.setConfirmPassword("testpass");
        user.setPassword("testpass");
        user.setFirstName("Test");
        user.setLastName("Last");
        user.setEmail("testuser@appfuse.org");

        Role role = rdao.findByName(Constants.USER_ROLE);

        assertNotNull(role.getId());

        user.addRole(role);
        user = dao.save(user);

        assertNotNull(user.getId());

        final User delUser = dao.getOne(user.getId());

        assertEquals("testpass", delUser.getPassword());

        dao.deleteById(delUser.getId());
        Assertions.assertThrows(DataAccessException.class, () -> {
            dao.getOne(delUser.getId());
        });
    }

    @Test
    public void testUserExists() {
        boolean b = dao.existsById(-1L);

        assertTrue(b);
    }

    @Test
    public void testUserNotExists() {
        boolean b = dao.existsById(111L);

        assertFalse(b);
    }

    @Test
    public void testPaged() {
        String username = "";
        String email = "";

        Page<User> pagedUser = dao.findAll(where(usernameContains(username)).and(emailContains(email)), PageRequest.of(0, 10, Sort.by("username")));

        assertEquals(1, pagedUser.getTotalPages());
        assertEquals(2, pagedUser.getTotalElements());
        assertEquals(2, pagedUser.getContent().size());

        pagedUser = dao.findAll(where(usernameContains(username)).and(emailContains(email)), PageRequest.of(1, 10, Sort.by("username")));

        assertEquals(1, pagedUser.getTotalPages());
        assertEquals(2, pagedUser.getTotalElements());

        username = "baduser";
        email = "bademail";

        pagedUser = dao.findAll(where(usernameContains(username)).and(emailContains(email)), PageRequest.of(0, 10, Sort.by("username")));

        assertEquals(0, pagedUser.getTotalPages());
        assertEquals(0, pagedUser.getTotalElements());
    }
}
