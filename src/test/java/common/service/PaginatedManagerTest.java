package common.service;

import static org.junit.Assert.*;

import java.io.InputStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import common.dao.jpa.HibernateSearchJpa;
import common.model.PaginatedList;
import common.model.User;
import common.service.impl.PaginatedManagerImpl;
import common.webapp.converter.FileType;
import common.webapp.form.UploadForm;

public class PaginatedManagerTest extends BaseManagerTestCase {

    public static final String PERSISTENCE_UNIT_NAME = "ApplicationEntityManager";

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    protected EntityManager entityManager;

    @Autowired
    private UserManager userManager;

    PaginatedManager<User, Long> paginatedManager;

    @Before
    public void setUp() throws Exception {
        paginatedManager = new PaginatedManagerImpl<User, Long>(new HibernateSearchJpa<User, Long>(User.class, entityManager));
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/service/users.csv");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData", input);

        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(FileType.CSV.getValue());
        uploadForm.setFileData(mockMultipartFile);
        userManager.uploadUsers(uploadForm);
    }

    @Test
    public void testCreatePaginatedList() {
        // 検索結果が1件の場合
        User user = new User("normaluser");
        user.setEnabled(true);
        user.setAccountLocked(false);
        PaginatedList<User> paginatedList = paginatedManager.createPaginatedList(user, 1);

        assertNotNull(paginatedList);
        assertEquals(2, paginatedList.getPageRangeSize());
        assertEquals(10, paginatedList.getCurrentEndRecordNumber());
        assertEquals(1, paginatedList.getAllRecordCount());
        assertFalse(paginatedList.isExistPrePage());
        assertFalse(paginatedList.isExistNextPage());
        assertEquals(0, paginatedList.getPrePageNumber());
        assertEquals(2, paginatedList.getNextPageNumber());
        assertEquals(1, paginatedList.getPageNumberList().size());
        assertEquals(1, paginatedList.getCurrentPage().size());

        // 検索結果が12件の場合
        user.setUsername(null);
        paginatedList = paginatedManager.createPaginatedList(user, 1);

        assertNotNull(paginatedList);
        assertEquals(12, paginatedList.getAllRecordCount());
        assertFalse(paginatedList.isExistPrePage());
        assertTrue(paginatedList.isExistNextPage());
        assertEquals(2, paginatedList.getPageNumberList().size());
        assertEquals(Integer.valueOf(1), paginatedList.getPageNumberList().get(0));
        assertEquals(Integer.valueOf(2), paginatedList.getPageNumberList().get(1));
        assertEquals(10, paginatedList.getCurrentPage().size());

        paginatedList.setCurrentPageNumber(2);

        assertEquals(20, paginatedList.getCurrentEndRecordNumber());
        assertTrue(paginatedList.isExistPrePage());
        assertFalse(paginatedList.isExistNextPage());
        assertEquals(1, paginatedList.getPrePageNumber());
        assertEquals(3, paginatedList.getNextPageNumber());
        assertEquals(2, paginatedList.getPageNumberList().size());
        assertEquals(10, paginatedList.getCurrentPage().size());
    }
}
