package common.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import common.dto.PaginatedList;
import common.dto.UploadForm;
import common.dto.UserSearchCriteria;
import common.dto.UserSearchResults;
import common.entity.User;
import common.webapp.converter.FileType;

class UsersManagerTest extends BaseManagerTestCase {

    @Autowired
    private UsersManager usersManager;

    @Test
    void testGetUsers() {
        List<User> userList = usersManager.getUsers();

        assertNotNull(userList);
        assertEquals(2, userList.size());
    }

    @Test
    void testCreatePaginatedList() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/service/users.csv");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData", input);

        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(FileType.CSV.getValue());
        uploadForm.setFileData(mockMultipartFile);
        usersManager.uploadUsers(uploadForm);

        // 検索結果が1件の場合
        UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
        userSearchCriteria.setUsername("normaluser");
        PaginatedList<UserSearchResults> paginatedList = usersManager.createPaginatedList(userSearchCriteria, 1);

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
        UserSearchResults userSearchResults = paginatedList.getCurrentPage().get(0);
        assertEquals(Long.valueOf(-2), userSearchResults.getId());
        assertEquals("normaluser", userSearchResults.getUsername());
        assertEquals("user@foo.bar", userSearchResults.getEmail());
        assertTrue(userSearchResults.isEnabled());

        // 検索結果が12件の場合
        userSearchCriteria.setUsername(null);
        paginatedList = usersManager.createPaginatedList(userSearchCriteria, 1);

        assertNotNull(paginatedList);
        assertEquals(12, paginatedList.getAllRecordCount());
        assertFalse(paginatedList.isExistPrePage());
        assertTrue(paginatedList.isExistNextPage());
        assertEquals(3, paginatedList.getPageNumberList().size());
        assertEquals(Integer.valueOf(1), paginatedList.getPageNumberList().get(0));
        assertEquals(Integer.valueOf(2), paginatedList.getPageNumberList().get(1));
        assertEquals(5, paginatedList.getCurrentPage().size());

        paginatedList = usersManager.createPaginatedList(userSearchCriteria, 3);

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

    @Test
    void testCreatePaginatedListByFullText() {
        usersManager.reindex();
        UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
        PaginatedList<UserSearchResults> paginatedList = usersManager.createPaginatedListByFullText(userSearchCriteria, 1);

        assertNotNull(paginatedList);
        assertEquals(2, paginatedList.getAllRecordCount());

        userSearchCriteria.setUsername("normaluser");
        paginatedList = usersManager.createPaginatedListByFullText(userSearchCriteria, 1);

        assertNotNull(paginatedList);
        assertEquals(1, paginatedList.getAllRecordCount());

        userSearchCriteria.setUsername("normaluser");
        userSearchCriteria.setEmail("user");
        paginatedList = usersManager.createPaginatedListByFullText(userSearchCriteria, 1);

        assertNotNull(paginatedList);
        assertEquals(1, paginatedList.getAllRecordCount());

        userSearchCriteria.setUsername("test");
        userSearchCriteria.setEmail("test");
        paginatedList = usersManager.createPaginatedListByFullText(userSearchCriteria, 1);

        assertNotNull(paginatedList);
        assertEquals(0, paginatedList.getAllRecordCount());
    }
}
