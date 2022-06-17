package common.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import common.dto.UserSearchCriteria;
import common.entity.User;

class UserSearchTest extends BaseDaoTestCase {

    @Autowired
    private UserSearch userSearch;

    @BeforeEach
    void setUp() {
        userSearch.reindexAll(false);
    }

    @Test
    void testSearch() {
        userSearch.reindex();

        UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
        PageRequest pageRequest = PageRequest.of(0, 2);

        List<User> userList = userSearch.search(userSearchCriteria, pageRequest);

        assertEquals(2, userList.size());

        userSearchCriteria.setUsername("normaluser");

        userList = userSearch.search(userSearchCriteria, pageRequest);

        userList.forEach(user -> {
            assertNotNull(user);
            assertEquals("normaluser", user.getUsername());
        });
    }

    @Test
    void testFacet() {
        userSearch.reindex();
        Map<String, Long> userFacet = userSearch.facet("firstNameFacet", 2);

        assertNotNull(userFacet);
        assertEquals(2, userFacet.size());
    }
}
