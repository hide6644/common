package common.service;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;

import common.dao.jpa.PaginatedDaoJpa;
import common.model.PaginatedList;
import common.model.User;
import common.service.impl.PaginatedManagerImpl;

public class PaginatedManagerTest extends BaseManagerTestCase {

    public static final String PERSISTENCE_UNIT_NAME = "ApplicationEntityManager";

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    protected EntityManager entityManager;

    PaginatedManager<User, Long> paginatedManager;

    @Before
    public void setUp() {
        paginatedManager = new PaginatedManagerImpl<User, Long>(new PaginatedDaoJpa<User, Long>(User.class, entityManager));
    }

    @Test
    public void testCreatePaginatedList() throws Exception {
        User user = new User("normaluser");
        user.setEnabled(true);
        user.setAccountLocked(false);
        PaginatedList<User> paginatedList = paginatedManager.createPaginatedList(user, 1);

        assertNotNull(paginatedList);
        assertEquals(1, paginatedList.getAllRecordCount());
        assertFalse(paginatedList.isExistPrePage());
        assertFalse(paginatedList.isExistNextPage());
        assertEquals(0, paginatedList.getPrePageNumber());
        assertEquals(2, paginatedList.getNextPageNumber());
        assertEquals(1, paginatedList.getPageNumberList().size());
    }
}
