package common.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import common.dao.jpa.GenericDaoJpa;
import common.model.User;

public class GenericDaoTest extends BaseDaoTestCase {

    GenericDao<User, Long> genericDao;

    @Before
    public void setUp() {
        genericDao = new GenericDaoJpa<User, Long>(User.class, entityManager);
        genericDao.reindexAll(true);
    }

    @Test
    public void testGet() {
        User user = genericDao.get(-2L);

        assertNotNull(user);
        assertEquals("normaluser", user.getUsername());
    }

    @Test
    public void testGetAll() {
        List<User> userList = genericDao.getAll();

        assertEquals(2, userList.size());
    }

    @Test
    public void testGetAllDistinct() {
        List<User> userDistinctList = genericDao.getAllDistinct();

        assertEquals(2, userDistinctList.size());
    }

    @Test
    public void testSearch() {
        genericDao.reindex();
        List<User> userList = genericDao.search(new String[]{"normaluser"}, new String[]{"username"});

        assertNotNull(userList.get(0));
        assertEquals("normaluser", userList.get(0).getUsername());

        userList = genericDao.search("*");

        assertEquals(2, userList.size());
    }

    @Test(expected = SearchException.class)
    public void testSearchException() {
        genericDao.search(new String[]{""}, new String[]{""});
    }
}
