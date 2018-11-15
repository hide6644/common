package common.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.hibernate.search.query.facet.Facet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
        Stream<User> userList = hibernateSearch.search(new String[]{"normaluser"}, new String[]{"username"});

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
            hibernateSearch.search(new String[]{""}, new String[]{""});
        });
    }

    @Test
    public void testPaged() {
        hibernateSearch.reindex();
        List<User> userList = hibernateSearch.search("normaluser", 0, 10);
        Long userCount = hibernateSearch.count("normaluser");

        Page<User> pagedUser = new PageImpl<>(userList, PageRequest.of(0, 10), userCount);

        assertEquals(1, pagedUser.getTotalPages());
        assertEquals(1, pagedUser.getTotalElements());
        assertEquals(1, pagedUser.getContent().size());
    }

    @Test
    public void testPagedByField() {
        hibernateSearch.reindex();
        List<User> userList = hibernateSearch.search(new String[]{"normaluser"}, new String[]{"username"}, 0, 10);
        Long userCount = hibernateSearch.count(new String[]{"normaluser"}, new String[]{"username"});

        Page<User> pagedUser = new PageImpl<>(userList, PageRequest.of(0, 10), userCount);

        assertEquals(1, pagedUser.getTotalPages());
        assertEquals(1, pagedUser.getTotalElements());
        assertEquals(1, pagedUser.getContent().size());
    }

    @Test
    public void testFacet() {
        hibernateSearch.reindex();
        List<Facet> userFacet = hibernateSearch.facet("firstNameFacet", 2);

        assertNotNull(userFacet);
        assertEquals(2, userFacet.size());
    }
}
