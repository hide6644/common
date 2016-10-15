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
}
