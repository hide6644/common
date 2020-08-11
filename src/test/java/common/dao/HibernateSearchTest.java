package common.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.facet.Facet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.dao.impl.HibernateSearchImpl;
import common.dto.UserSearchCriteria;
import common.entity.User;

public class HibernateSearchTest extends BaseDaoTestCase {

    private HibernateSearch hibernateSearch;

    @BeforeEach
    public void setUp() {
        hibernateSearch = new HibernateSearchImpl<User>(User.class, entityManager);
        hibernateSearch.reindexAll(false);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSearch() {
        hibernateSearch.reindex();
        Stream<User> userList = hibernateSearch.search("normaluser").getResultStream();

        userList.forEach(user -> {
            assertNotNull(user);
            assertEquals("normaluser", user.getUsername());
        });

        userList = hibernateSearch.search(new String[]{"normaluser"}, new String[]{"username"}, new Occur[]{Occur.SHOULD}).getResultStream();

        userList.forEach(user -> {
            assertNotNull(user);
            assertEquals("normaluser", user.getUsername());
        });

        userList = hibernateSearch.search(new String[]{"normaluser"}, new String[]{"username"}).getResultStream();

        userList.forEach(user -> {
            assertNotNull(user);
            assertEquals("normaluser", user.getUsername());
        });

        userList = hibernateSearch.search("*").getResultStream();

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
        org.apache.lucene.search.Sort sort = new org.apache.lucene.search.Sort(new SortField(UserSearchCriteria.USERNAME_FIELD + "Sort", SortField.Type.STRING));
        FullTextQuery userList = hibernateSearch.search("normaluser", 0L, 10, sort);

        assertEquals(1, userList.getResultStream().count());
        assertEquals(Integer.valueOf(1), userList.getResultSize());
    }

    @Test
    public void testPagedByField() {
        hibernateSearch.reindex();
        org.apache.lucene.search.Sort sort = new org.apache.lucene.search.Sort(new SortField(UserSearchCriteria.USERNAME_FIELD + "Sort", SortField.Type.STRING));
        FullTextQuery userList = hibernateSearch.search(new String[]{"normaluser"}, new String[]{"username"}, 0L, 10, sort);

        assertEquals(1, userList.getResultStream().count());
        assertEquals(Integer.valueOf(1), userList.getResultSize());
    }

    @Test
    public void testFacet() {
        hibernateSearch.reindex();
        List<Facet> userFacet = hibernateSearch.facet("firstNameFacet", 2);

        assertNotNull(userFacet);
        assertEquals(2, userFacet.size());
    }
}
