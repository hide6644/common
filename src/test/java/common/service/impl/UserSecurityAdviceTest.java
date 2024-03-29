package common.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import common.Constants;
import common.dto.UserDetailsForm;
import common.entity.Role;
import common.entity.User;
import common.service.UserManager;
import common.service.UserSecurityAdvice;

@ExtendWith(MockitoExtension.class)
class UserSecurityAdviceTest {

    private static UserManager userManager;

    @BeforeAll
    static void setUpClass() {
        try (ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("/common/service/applicationContext-test.xml")) {
            userManager = (UserManager) ctx.getBean("target");
            User user = new User("user");
            user.setId(1L);
            user.setVersion(1L);
            when(userManager.getUserByUsername("user")).thenReturn(user);
            when(userManager.saveUser(user)).thenReturn(user);
        }
    }

    @BeforeEach
    void setUp() {
        User user = new User("user");
        user.setId(1L);
        user.setPassword("password");
        user.addRole(new Role(Constants.USER_ROLE));

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
        token.setDetails(user);
        SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(token);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void testAddUserWithoutAdminRole() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        assertTrue(auth.isAuthenticated());

        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setUsername("admin");
        userDetailsForm.setId(2L);

        try {
            userManager.saveUserDetails(userDetailsForm);
            fail("AccessDeniedException not thrown");
        } catch (AccessDeniedException expected) {
            assertNotNull(expected);
            assertEquals(UserSecurityAdvice.ACCESS_DENIED, expected.getMessage());
        }
    }

    @Test
    void testAddUserAsAdmin() throws Exception {
        User user = new User("admin");
        user.setId(2L);
        user.setPassword("password");
        user.addRole(new Role(Constants.ADMIN_ROLE));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
        token.setDetails(user);
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(token);
        SecurityContextHolder.setContext(securityContext);

        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setUsername("user");
        userDetailsForm.setId(1L);

        assertDoesNotThrow(() -> {
            userManager.saveUserDetails(userDetailsForm);
        });
    }

    @Test
    void testUpdateUserProfile() throws Exception {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setUsername("user");
        userDetailsForm.setId(1L);
        userDetailsForm.getRoles().add(new Role(Constants.USER_ROLE));
        userDetailsForm.setVersion(1L);

        assertDoesNotThrow(() -> {
            userManager.saveUserDetails(userDetailsForm);
        });
    }

    @Test
    void testChangeToAdminRoleFromUserRole() throws Exception {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setUsername("user");
        userDetailsForm.setId(1L);
        userDetailsForm.getRoles().add(new Role(Constants.ADMIN_ROLE));
        userDetailsForm.setVersion(1L);

        try {
            userManager.saveUserDetails(userDetailsForm);
            fail("AccessDeniedException not thrown");
        } catch (AccessDeniedException expected) {
            assertNotNull(expected);
            assertEquals(UserSecurityAdvice.ACCESS_DENIED, expected.getMessage());
        }
    }

    @Test
    void testAddAdminRoleWhenAlreadyHasUserRole() throws Exception {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setUsername("user");
        userDetailsForm.setId(1L);
        userDetailsForm.getRoles().add(new Role(Constants.ADMIN_ROLE));
        userDetailsForm.getRoles().add(new Role(Constants.USER_ROLE));
        userDetailsForm.setVersion(1L);

        try {
            userManager.saveUserDetails(userDetailsForm);
            fail("AccessDeniedException not thrown");
        } catch (AccessDeniedException expected) {
            assertNotNull(expected);
            assertEquals(UserSecurityAdvice.ACCESS_DENIED, expected.getMessage());
        }
    }

    @Test
    void testAddUserRoleWhenHasAdminRole() throws Exception {
        User user = new User("user");
        user.setId(1L);
        user.setPassword("password");
        user.addRole(new Role(Constants.ADMIN_ROLE));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
        token.setDetails(user);
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(token);
        SecurityContextHolder.setContext(securityContext);

        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setUsername("user");
        userDetailsForm.setId(1L);
        userDetailsForm.getRoles().add(new Role(Constants.ADMIN_ROLE));
        userDetailsForm.getRoles().add(new Role(Constants.USER_ROLE));
        userDetailsForm.setVersion(1L);

        assertDoesNotThrow(() -> {
            userManager.saveUserDetails(userDetailsForm);
        });
    }

    @Test
    void testUpdateUserWithUserRole() throws Exception {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setUsername("user");
        userDetailsForm.setId(1L);
        userDetailsForm.getRoles().add(new Role(Constants.USER_ROLE));
        userDetailsForm.setVersion(1L);

        assertDoesNotThrow(() -> {
            userManager.saveUserDetails(userDetailsForm);
        });
    }
}
