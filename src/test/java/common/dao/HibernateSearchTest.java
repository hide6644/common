package common.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.apache.lucene.search.BooleanClause.Occur;
import org.hibernate.search.query.facet.Facet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.dao.jpa.HibernateSearchImpl;
import common.entity.User;

public class HibernateSearchTest extends BaseDaoTestCase {

    private HibernateSearch<User> hibernateSearch;

    @BeforeEach
    public void setUp() {
        hibernateSearch = new HibernateSearchImpl<User>(User.class, entityManager);
        hibernateSearch.reindexAll(false);
    }

    @Test
    public void testSearch() {
        hibernateSearch.reindex();
        Stream<User> userList = hibernateSearch.search("normaluser");

        userList.forEach(user -> {
            assertNotNull(user);
            assertEquals("normaluser", user.getUsername());
        });

        userList = hibernateSearch.search(new String[]{"normaluser"}, new String[]{"username"}, new Occur[]{Occur.SHOULD});

        userList.forEach(user -> {
            assertNotNull(user);
            assertEquals("normaluser", user.getUsername());
        });

        userList = hibernateSearch.search(new String[]{"normaluser"}, new String[]{"username"});

        userList.forEach(user -> {
            assertNotNull(user);
            assertEquals("normaluser", user.getUsername());
        });

        userList = hibernateSearch.search("*");

        assertEquals(2, userList.count());
    }

    @Test
    public void testSearchException() {
        Assertions.assertThrows(SearchException.class, () -> {
            hibernateSearch.search(new String[]{""}, new String[]{""}, new Occur[]{Occur.SHOULD});
        });
        Assertions.assertThrows(SearchException.class, () -> {
            hibernateSearch.search(new String[]{""}, new String[]{""});
        });
    }

    @Test
    public void testPaged() {
        hibernateSearch.reindex();
        Stream<User> userList = hibernateSearch.search("normaluser", 0, 10);
        Long userCount = hibernateSearch.count("normaluser");

        assertEquals(1, userList.count());
        assertEquals(Long.valueOf(1), userCount);
    }

    @Test
    public void testPagedByField() {
        hibernateSearch.reindex();
        Stream<User> userList = hibernateSearch.search(new String[]{"normaluser"}, new String[]{"username"}, 0, 10);
        Long userCount = hibernateSearch.count(new String[]{"normaluser"}, new String[]{"username"});

        assertEquals(1, userList.count());
        assertEquals(Long.valueOf(1), userCount);
    }

    @Test
    public void testFacet() {
        hibernateSearch.reindex();
        List<Facet> userFacet = hibernateSearch.facet("firstNameFacet", 2);

        assertNotNull(userFacet);
        assertEquals(2, userFacet.size());
    }
}
