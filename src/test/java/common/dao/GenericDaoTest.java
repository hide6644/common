package common.dao;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;

import common.dao.jpa.GenericDaoJpa;
import common.model.User;

public class GenericDaoTest extends BaseDaoTestCase {

    GenericDao<User, Long> genericDao;

    public static final String PERSISTENCE_UNIT_NAME = "ApplicationEntityManager";

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    protected EntityManager entityManager;

    @Before
    public void setUp() {
        genericDao = new GenericDaoJpa<User, Long>(User.class, entityManager);
    }

    @Test
    public void getUser() {
        User user = genericDao.get(-2L);
        assertNotNull(user);
        assertEquals("normaluser", user.getUsername());
    }

    @Test
    public void getAll() {
        List<User> userList = genericDao.getAll();
        assertEquals(2, userList.size());

        List<User> userDistinctList = genericDao.getAllDistinct();
        assertEquals(2, userDistinctList.size());
    }

    @Test
    public void getPaged() {
        List<User> userList = genericDao.getPaged(User.class, null, 1, 1);
        assertEquals(1, userList.size());
        assertEquals(2, genericDao.getRecordCount(User.class, null));
    }
}
