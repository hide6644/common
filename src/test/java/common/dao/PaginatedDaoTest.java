package common.dao;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;

import common.dao.jpa.PaginatedDaoJpa;
import common.model.User;

public class PaginatedDaoTest extends BaseDaoTestCase {

    PaginatedDao<User, Long> paginatedDao;

    public static final String PERSISTENCE_UNIT_NAME = "ApplicationEntityManager";

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    protected EntityManager entityManager;

    @Before
    public void setUp() {
        paginatedDao = new PaginatedDaoJpa<User, Long>(User.class, entityManager);
    }

    @Test
    public void testGetList() {
        List<User> userList = paginatedDao.getList(null, 1, 1);
        assertEquals(1, userList.size());
        assertEquals(2, paginatedDao.getCount(null));

        User user = new User();
        user.setFirstName("admin");
        user.setAccountLocked(false);
        user.setEnabled(true);

        userList = paginatedDao.getList(user, 1, 1);
        assertEquals(0, userList.size());
        assertEquals(1, paginatedDao.getCount(user));
    }

    @Test
    public void testSearchList() {
        List<User> userList = paginatedDao.searchList("foo", 1, 1);
        assertEquals(1, userList.size());
        assertEquals(2, paginatedDao.searchCount("foo"));
    }
}
