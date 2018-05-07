package common.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.search.query.facet.Facet;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import common.dao.jpa.HibernateSearchImpl;
import common.model.User;

public class HibernateSearchTest extends BaseDaoTestCase {

    HibernateSearch<User> hibernateSearch;

    @Before
    public void setUp() {
        hibernateSearch = new HibernateSearchImpl<User>(User.class, entityManager);
        hibernateSearch.reindexAll(true);
    }

    @Test
    public void testSearch() {
        hibernateSearch.reindex();
        List<User> userList = hibernateSearch.search(new String[]{"normaluser"}, new String[]{"username"});

        assertNotNull(userList.get(0));
        assertEquals("normaluser", userList.get(0).getUsername());

        userList = hibernateSearch.search("*");

        assertEquals(2, userList.size());
    }

    @Test(expected = SearchException.class)
    public void testSearchException() {
        hibernateSearch.search(new String[]{""}, new String[]{""});
    }

    @Test
    public void testPaged() {
        hibernateSearch.reindex();
        List<User> userList = hibernateSearch.searchList("normaluser", 0, 10);
        Long userCount = hibernateSearch.searchCount("normaluser");

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
    }}
