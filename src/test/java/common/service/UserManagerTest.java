package common.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import common.Constants;
import common.model.PaginatedList;
import common.model.User;

public class UserManagerTest extends BaseManagerTestCase {

    @Autowired
    private UserManager mgr;

    @Autowired
    private RoleManager roleManager;

    @Test
    public void testGetUser() {
        User user = mgr.getUserByUsername("normaluser");

        assertNotNull(user);

        log.debug(user);

        assertEquals(1, user.getRoles().size());
    }

    @Test
    public void testGetUsers() {
        List<User> userList = mgr.getUsers();

        assertNotNull(userList);
        assertEquals(2, userList.size());
    }

    @Test
    public void testSaveUser() {
        User user = mgr.getUserByUsername("normaluser");
        user.setConfirmPassword(user.getPassword());
        user.setLastName("smith");

        log.debug("saving user with updated last name: " + user);

        user = mgr.saveUser(user);

        assertEquals("smith", user.getLastName());
        assertEquals(1, user.getRoles().size());
    }

    @Test
    public void testAddAndRemoveUser() throws Exception {
        User user = new User();
        user = (User) populate(user);
        user.addRole(roleManager.getRole(Constants.USER_ROLE));
        user = mgr.saveUser(user);

        log.debug("removing user...");

        mgr.removeUser(user);

        try {
            user = mgr.getUserByUsername("john");
            fail("Expected 'Exception' not thrown");
        } catch (Exception e) {
            log.debug(e);
            assertNotNull(e);
        }
    }

    @Test
    public void testAddAndRemoveUserByPK() throws Exception {
        User user = new User();
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
    public void testCreatePaginatedList() {
        // 検索結果が1件の場合
        User user = new User("normaluser");
        user.setEnabled(true);
        user.setAccountLocked(false);
        PaginatedList<User> paginatedList = mgr.createPaginatedList(user, 1);

        assertNotNull(paginatedList);
        assertEquals(2, paginatedList.getPageRangeSize());
        assertEquals(1, paginatedList.getAllRecordCount());
        assertFalse(paginatedList.isExistPrePage());
        assertFalse(paginatedList.isExistNextPage());
        assertEquals(1, paginatedList.getPageNumberList().size());
        assertEquals(1, paginatedList.getCurrentPage().size());

        // 検索結果が12件の場合
        user.setUsername(null);
        paginatedList = mgr.createPaginatedList(user, 1);

        assertNotNull(paginatedList);
        assertEquals(12, paginatedList.getAllRecordCount());
        assertFalse(paginatedList.isExistPrePage());
        assertTrue(paginatedList.isExistNextPage());
        assertEquals(2, paginatedList.getPageNumberList().size());
        assertEquals(Integer.valueOf(1), paginatedList.getPageNumberList().get(0));
        assertEquals(Integer.valueOf(2), paginatedList.getPageNumberList().get(1));
        assertEquals(10, paginatedList.getCurrentPage().size());

        paginatedList = mgr.createPaginatedList(user, 2);

        assertTrue(paginatedList.isExistPrePage());
        assertFalse(paginatedList.isExistNextPage());
        assertEquals(2, paginatedList.getPageNumberList().size());
        assertEquals(10, paginatedList.getCurrentPage().size());
    }
}
