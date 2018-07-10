package common.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import common.Constants;
import common.model.Role;
import common.model.User;
import common.service.UserManager;
import common.service.UserSecurityAdvice;

@ExtendWith(MockitoExtension.class)
public class UserSecurityAdviceTest {

    ApplicationContext ctx;

    SecurityContext initialSecurityContext;

    @BeforeEach
    public void setUp() {
        initialSecurityContext = SecurityContextHolder.getContext();

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

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.setContext(initialSecurityContext);
    }

    @Test
    public void testAddUserWithoutAdminRole() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        assertTrue(auth.isAuthenticated());

        User user = new User("admin");
        user.setId(2L);

        try {
            makeInterceptedTarget().saveUser(user);
            fail("AccessDeniedException not thrown");
        } catch (AccessDeniedException expected) {
            assertNotNull(expected);
            assertEquals(UserSecurityAdvice.ACCESS_DENIED, expected.getMessage());
        }
    }

    @Test
    public void testAddUserAsAdmin() throws Exception {
        User user = new User("admin");
        user.setId(2L);
        user.setPassword("password");
        user.addRole(new Role(Constants.ADMIN_ROLE));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
        token.setDetails(user);
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(token);
        SecurityContextHolder.setContext(securityContext);

        User adminUser = new User("admin");
        adminUser.setId(2L);

        makeInterceptedTarget().saveUser(adminUser);
    }

    @Test
    public void testUpdateUserProfile() throws Exception {
        User user = new User("user");
        user.setId(1L);
        user.getRoles().add(new Role(Constants.USER_ROLE));
        user.setVersion(1L);

        makeInterceptedTarget().saveUser(user);
    }

    @Test
    public void testChangeToAdminRoleFromUserRole() throws Exception {
        User user = new User("user");
        user.setId(1L);
        user.getRoles().add(new Role(Constants.ADMIN_ROLE));
        user.setVersion(1L);

        try {
            makeInterceptedTarget().saveUser(user);
            fail("AccessDeniedException not thrown");
        } catch (AccessDeniedException expected) {
            assertNotNull(expected);
            assertEquals(UserSecurityAdvice.ACCESS_DENIED, expected.getMessage());
        }
    }

    @Test
    public void testAddAdminRoleWhenAlreadyHasUserRole() throws Exception {
        User user = new User("user");
        user.setId(1L);
        user.getRoles().add(new Role(Constants.ADMIN_ROLE));
        user.getRoles().add(new Role(Constants.USER_ROLE));
        user.setVersion(1L);

        try {
            makeInterceptedTarget().saveUser(user);
            fail("AccessDeniedException not thrown");
        } catch (AccessDeniedException expected) {
            assertNotNull(expected);
            assertEquals(UserSecurityAdvice.ACCESS_DENIED, expected.getMessage());
        }
    }

    @Test
    public void testAddUserRoleWhenHasAdminRole() throws Exception {
        User user = new User("user");
        user.setId(1L);
        user.setPassword("password");
        user.addRole(new Role(Constants.ADMIN_ROLE));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
        token.setDetails(user);
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(token);
        SecurityContextHolder.setContext(securityContext);

        user = new User("user");
        user.setId(1L);
        user.getRoles().add(new Role(Constants.ADMIN_ROLE));
        user.getRoles().add(new Role(Constants.USER_ROLE));
        user.setVersion(1L);

        makeInterceptedTarget().saveUser(user);
    }

    @Test
    public void testUpdateUserWithUserRole() throws Exception {
        User user = new User("user");
        user.setId(1L);
        user.getRoles().add(new Role(Constants.USER_ROLE));
        user.setVersion(1L);

        makeInterceptedTarget().saveUser(user);
    }

    private UserManager makeInterceptedTarget() throws Exception {
        ctx = new ClassPathXmlApplicationContext("/common/service/applicationContext-test.xml");

        UserManager userManager = (UserManager) ctx.getBean("target");

        return userManager;
    }
}
