package common.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import common.dao.jpa.HibernateSearchJpa;
import common.model.User;

public class PaginatedDaoTest extends BaseDaoTestCase {

    HibernateSearch<User, Long> hibernateSearch;

    @Before
    public void setUp() {
        hibernateSearch = new HibernateSearchJpa<User, Long>(User.class, entityManager);
        hibernateSearch.reindexAll(false);
    }

    @Test
    public void testGetList() {
        List<User> userList = hibernateSearch.getList(null, 1, 1);

        assertEquals(1, userList.size());
        assertEquals(2, hibernateSearch.getCount(null));

        User user = new User();
        user.setFirstName("admin");
        user.setAccountLocked(false);
        user.setEnabled(true);

        userList = hibernateSearch.getList(user, 1, 1);

        assertEquals(0, userList.size());
        assertEquals(1, hibernateSearch.getCount(user));
    }

    @Test
    public void testSearchList() {
        hibernateSearch.reindex();
        List<User> userList = hibernateSearch.searchList("foo", 1, 1);

        assertEquals(1, userList.size());
        assertEquals(2, hibernateSearch.searchCount("foo"));
    }
}
