package common.dao.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import common.dao.BaseDaoTestCase;

class UserDetailsDaoImplTest extends BaseDaoTestCase {

    @Autowired
    private UserDetailsService dao;

    @Test
    void testLoadUserByUsername() {
        UserDetails user = dao.loadUserByUsername("administrator");

        assertNotNull(user);
    }

    @Test
    void testLoadUserByUsernameNotFoundException() {
        try {
            dao.loadUserByUsername("foo");
            fail("UsernameNotFoundException not thrown");
        } catch (UsernameNotFoundException expected) {
            assertNotNull(expected);
            assertEquals("user 'foo' not found...", expected.getMessage());
        }
    }
}
