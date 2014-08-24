package common.dao;

import static org.junit.Assert.*;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import common.dao.hibernate.GenericDaoHibernate;
import common.model.User;

public class GenericDaoTest extends BaseDaoTestCase {

    GenericDao<User, Long> genericDao;

    @Autowired
    SessionFactory sessionFactory;

    @Before
    public void setUp() {
        genericDao = new GenericDaoHibernate<User, Long>(User.class, sessionFactory);
    }

    @Test
    public void getUser() {
        User user = genericDao.get(-2L);
        assertNotNull(user);
        assertEquals("normaluser", user.getUsername());
    }
}
