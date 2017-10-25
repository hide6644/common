package common.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import common.dao.jpa.PaginatedDaoJpa;
import common.model.User;

public class PaginatedDaoTest extends BaseDaoTestCase {

    PaginatedDao<User, Long> paginatedDao;

    @Before
    public void setUp() {
        paginatedDao = new PaginatedDaoJpa<User, Long>(User.class, entityManager);
        paginatedDao.reindexAll(false);
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
        paginatedDao.reindex();
        List<User> userList = paginatedDao.searchList("foo", 1, 1);

        assertEquals(1, userList.size());
        assertEquals(2, paginatedDao.searchCount("foo"));
    }
}
