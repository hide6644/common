package common.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import common.Constants;
import common.dto.UploadForm;
import common.dto.UserSearchCriteria;
import common.dto.UserSearchResults;
import common.model.PaginatedList;
import common.model.User;
import common.webapp.converter.FileType;

public class UserManagerTest extends BaseManagerTestCase {

    @Autowired
    private UserManager userManager;

    @Autowired
    private RoleManager roleManager;

    @Test
    public void testGetUser() {
        User user = userManager.getUserByUsername("normaluser");

        assertNotNull(user);
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isCredentialsNonExpired());

        log.debug(user);

        assertEquals(1, user.getRoles().size());
    }

    @Test
    public void testGetUsers() {
        List<User> userList = userManager.getUsers();

        assertNotNull(userList);
        assertEquals(2, userList.size());
    }

    @Test
    public void testSaveUser() {
        User user = userManager.getUserByUsername("normaluser");
        user.setConfirmPassword(user.getPassword());
        user.setLastName("smith");

        log.debug("saving user with updated last name: " + user);

        user = userManager.saveUser(user);

        assertEquals("smith", user.getLastName());
        assertEquals(1, user.getRoles().size());
    }

    @Test
    public void testAddAndRemoveUser() throws Exception {
        User user = new User();
        user = (User) populate(user);
        user.addRole(roleManager.getRole(Constants.USER_ROLE));
        user = userManager.saveUser(user);

        log.debug("removing user...");

        userManager.removeUser(user);

        try {
            user = userManager.getUserByUsername("john");
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
        user = userManager.saveUser(user);

        assertEquals("john_elway", user.getUsername());
        assertEquals(1, user.getRoles().size());

        log.debug("removing user...");

        userManager.removeUser(user.getId().toString());

        try {
            user = userManager.getUserByUsername("john");
            fail("Expected 'Exception' not thrown");
        } catch (Exception e) {
            log.debug(e);
            assertNotNull(e);
        }
    }

    @Test
    public void testCreatePaginatedList() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/service/users.csv");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData", input);

        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(FileType.CSV.getValue());
        uploadForm.setFileData(mockMultipartFile);
        userManager.uploadUsers(uploadForm);

        // 検索結果が1件の場合
        UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
        userSearchCriteria.setUsername("normaluser");
        PaginatedList<UserSearchResults> paginatedList = userManager.createPaginatedList(userSearchCriteria, 1);

        assertNotNull(paginatedList);
        assertEquals(2, paginatedList.getPageRangeSize());
        assertEquals(1, paginatedList.getCurrentStartRecordNumber());
        assertEquals(5, paginatedList.getCurrentEndRecordNumber());
        assertEquals(1, paginatedList.getAllRecordCount());
        assertFalse(paginatedList.isExistPrePage());
        assertFalse(paginatedList.isExistNextPage());
        assertEquals(0, paginatedList.getPrePageNumber());
        assertEquals(2, paginatedList.getNextPageNumber());
        assertEquals(1, paginatedList.getPageNumberList().size());
        assertEquals(1, paginatedList.getCurrentPage().size());

        // 検索結果が12件の場合
        userSearchCriteria.setUsername(null);
        paginatedList = userManager.createPaginatedList(userSearchCriteria, 1);

        assertNotNull(paginatedList);
        assertEquals(12, paginatedList.getAllRecordCount());
        assertFalse(paginatedList.isExistPrePage());
        assertTrue(paginatedList.isExistNextPage());
        assertEquals(3, paginatedList.getPageNumberList().size());
        assertEquals(Integer.valueOf(1), paginatedList.getPageNumberList().get(0));
        assertEquals(Integer.valueOf(2), paginatedList.getPageNumberList().get(1));
        assertEquals(5, paginatedList.getCurrentPage().size());

        paginatedList = userManager.createPaginatedList(userSearchCriteria, 3);

        assertEquals(11, paginatedList.getCurrentStartRecordNumber());
        assertEquals(15, paginatedList.getCurrentEndRecordNumber());
        assertTrue(paginatedList.isExistPrePage());
        assertFalse(paginatedList.isExistNextPage());
        assertEquals(2, paginatedList.getPrePageNumber());
        assertEquals(4, paginatedList.getNextPageNumber());
        assertEquals(3, paginatedList.getPageNumberList().size());
        assertEquals(2, paginatedList.getCurrentPage().size());
        assertEquals(5, paginatedList.getPageSize());
    }
}
