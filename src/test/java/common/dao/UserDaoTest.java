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
import common.dao.jpa.RoleDao;
import common.dao.jpa.UserDao;
import common.entity.Role;
import common.entity.User;

class UserDaoTest extends BaseDaoTestCase {

    @Autowired
    private UserDao dao;

    @Autowired
    private RoleDao rdao;

    @Test
    void testGetUserInvalid() throws Exception {
        Assertions.assertThrows(DataAccessException.class, () -> {
            dao.getReferenceById(1000L);
        });
    }

    @Test
    void testGetUser() throws Exception {
        User user = dao.getReferenceById(-1L);

        assertNotNull(user);
        assertEquals(1, user.getRoles().size());
        assertTrue(user.isEnabled());
    }

    @Test
    void testGetUsers() {
        List<User> userList = dao.findAll(Sort.by("username"));

        assertEquals(2, userList.size());
    }

    @Test
    void testGetUserPassword() {
        User user = dao.getReferenceById(-1L);
        String password = dao.findPasswordById(user.getId());

        assertNotNull(password);

        log.debug("password:{}", password);
    }

    @Test
    void testUpdateUser() {
        User user = dao.getReferenceById(-1L);

        dao.save(user);

        user = dao.getReferenceById(-1L);

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
    void testAddUserRole() {
        User user = dao.getReferenceById(-1L);

        assertEquals(1, user.getRoles().size());

        Role role = rdao.getReferenceById(Constants.USER_ROLE);
        user.addRole(role);
        user.setConfirmPassword(user.getPassword());
        dao.save(user);
        user = dao.getReferenceById(-1L);

        assertEquals(2, user.getRoles().size());

        user.addRole(role);
        dao.save(user);
        user = dao.getReferenceById(-1L);

        assertEquals(2, user.getRoles().size());

        user.removeRole(role);
        dao.save(user);
        user = dao.getReferenceById(-1L);

        assertEquals(1, user.getRoles().size());
    }

    @Test
    void testAddAndRemoveUser() {
        User user = new User("testuser");
        user.setConfirmPassword("testpass");
        user.setPassword("testpass");
        user.setFirstName("Test");
        user.setLastName("Last");
        user.setEmail("testuser@appfuse.org");

        Role role = rdao.getReferenceById(Constants.USER_ROLE);

        assertNotNull(role);

        user.addRole(role);
        user = dao.save(user);

        assertNotNull(user.getId());

        final User delUser = dao.getReferenceById(user.getId());

        assertEquals("testpass", delUser.getPassword());

        dao.delete(delUser);
        Assertions.assertThrows(DataAccessException.class, () -> {
            dao.getReferenceById(delUser.getId());
        });
    }

    @Test
    void testAddAndRemoveUserId() {
        User user = new User("testuser");
        user.setConfirmPassword("testpass");
        user.setPassword("testpass");
        user.setFirstName("Test");
        user.setLastName("Last");
        user.setEmail("testuser@appfuse.org");

        Role role = rdao.getReferenceById(Constants.USER_ROLE);

        assertNotNull(role);

        user.addRole(role);
        user = dao.save(user);

        assertNotNull(user.getId());

        final User delUser = dao.getReferenceById(user.getId());

        assertEquals("testpass", delUser.getPassword());

        dao.deleteById(delUser.getId());
        Assertions.assertThrows(DataAccessException.class, () -> {
            dao.getReferenceById(delUser.getId());
        });
    }

    @Test
    void testUserExists() {
        boolean b = dao.existsById(-1L);

        assertTrue(b);
    }

    @Test
    void testUserNotExists() {
        boolean b = dao.existsById(111L);

        assertFalse(b);
    }

    @Test
    void testPaged() {
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
