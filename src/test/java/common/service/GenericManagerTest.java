package common.service;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.search.query.facet.Facet;
import org.junit.Before;
import org.junit.Test;

import common.dao.jpa.GenericDaoJpa;
import common.model.User;
import common.service.impl.GenericManagerImpl;

public class GenericManagerTest extends BaseManagerTestCase {

    public static final String PERSISTENCE_UNIT_NAME = "ApplicationEntityManager";

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    protected EntityManager entityManager;

    GenericManager<User, Long> genericManager;

    @Before
    public void setUp() {
        genericManager = new GenericManagerImpl<User, Long>(new GenericDaoJpa<User, Long>(User.class, entityManager));
    }

    @Test
    public void testSearch() {
        genericManager.reindexAll(false);
        List<User> userList = genericManager.search("normaluser");

        assertNotNull(userList);
        assertEquals(1, userList.size());
    }

    @Test
    public void testFacet() {
        genericManager.reindexAll(false);
        List<Facet> userFacet = genericManager.facet("firstNameFacet", 2);

        assertNotNull(userFacet);
        assertEquals(2, userFacet.size());
    }
}
