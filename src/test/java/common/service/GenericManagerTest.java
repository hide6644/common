package common.service;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.search.query.facet.Facet;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import common.Constants;
import common.dao.jpa.GenericDaoJpa;
import common.model.User;
import common.service.impl.BaseManagerImpl;

public class GenericManagerTest extends BaseManagerTestCase {

    public static final String PERSISTENCE_UNIT_NAME = "ApplicationEntityManager";

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    protected EntityManager entityManager;

    GenericManager<User, Long> genericManager;

    @Autowired
    private RoleManager roleManager;

    @Before
    public void setUp() {
        genericManager = new BaseManagerImpl<User, Long>(new GenericDaoJpa<User, Long>(User.class, entityManager));
    }

    @Test
    public void testGet() {
        User user = genericManager.get(-1L);

        assertNotNull(user);
    }

    @Test
    public void testExists() {
        assertTrue(genericManager.exists(-1L));
        assertFalse(genericManager.exists(-10L));
    }

    @Test
    public void testAddAndRemove() throws Exception {
        User user = new User();
        user = (User) populate(user);
        user.addRole(roleManager.getRole(Constants.USER_ROLE));
        user = genericManager.save(user);

        assertNotNull(genericManager.get(user.getId()));

        genericManager.remove(user);

        try {
            genericManager.get(user.getId());
            fail("Expected 'Exception' not thrown");
        } catch (Exception e) {
            log.debug(e);
            assertNotNull(e);
        }
    }

    @Test
    public void testAddAndRemoveByPK() throws Exception {
        User user = new User();
        user = (User) populate(user);
        user.addRole(roleManager.getRole(Constants.USER_ROLE));
        user = genericManager.save(user);

        assertNotNull(genericManager.get(user.getId()));

        genericManager.remove(user.getId());

        try {
            genericManager.get(user.getId());
            fail("Expected 'Exception' not thrown");
        } catch (Exception e) {
            log.debug(e);
            assertNotNull(e);
        }
    }

    @Test
    public void testSearch() {
        genericManager.reindexAll(false);
        List<User> userList = genericManager.search("normaluser");

        assertNotNull(userList);
        assertEquals(1, userList.size());

        userList = genericManager.search(null);

        assertNotNull(userList);
        assertEquals(2, userList.size());
    }

    @Test
    public void testFacet() {
        genericManager.reindexAll(false);
        List<Facet> userFacet = genericManager.facet("firstNameFacet", 2);

        assertNotNull(userFacet);
        assertEquals(2, userFacet.size());
    }
}
