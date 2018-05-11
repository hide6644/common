package common.service.impl;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import common.Constants;
import common.dao.UserDao;
import common.model.Role;
import common.model.User;
import common.service.RoleManager;
import common.service.UserManager;
import common.service.UserSecurityAdvice;

@RunWith(MockitoJUnitRunner.class)
public class UserSecurityAdviceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleManager roleManager;

    ApplicationContext ctx;

    SecurityContext initialSecurityContext;

    @Before
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

    @After
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
            Assert.assertEquals(UserSecurityAdvice.ACCESS_DENIED, expected.getMessage());
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

        given(userDao.saveAndFlush(adminUser)).willReturn(adminUser);
        given(passwordEncoder.encode(adminUser.getPassword())).willReturn(adminUser.getPassword());
        given(roleManager.getRoles(adminUser.getRoles())).willReturn(adminUser.getRoles());

        makeInterceptedTarget().saveUser(adminUser);
    }

    @Test
    public void testUpdateUserProfile() throws Exception {
        User user = new User("user");
        user.setId(1L);
        user.getRoles().add(new Role(Constants.USER_ROLE));
        user.setVersion(1L);

        given(userDao.saveAndFlush(user)).willReturn(user);
        given(passwordEncoder.encode(user.getPassword())).willReturn(user.getPassword());
        given(roleManager.getRoles(user.getRoles())).willReturn(user.getRoles());

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

        given(userDao.saveAndFlush(user)).willReturn(user);
        given(passwordEncoder.encode(user.getPassword())).willReturn(user.getPassword());
        given(roleManager.getRoles(user.getRoles())).willReturn(user.getRoles());

        makeInterceptedTarget().saveUser(user);
    }

    @Test
    public void testUpdateUserWithUserRole() throws Exception {
        User user = new User("user");
        user.setId(1L);
        user.getRoles().add(new Role(Constants.USER_ROLE));
        user.setVersion(1L);

        given(userDao.saveAndFlush(user)).willReturn(user);
        given(passwordEncoder.encode(user.getPassword())).willReturn(user.getPassword());
        given(roleManager.getRoles(user.getRoles())).willReturn(user.getRoles());

        makeInterceptedTarget().saveUser(user);
    }

    private UserManager makeInterceptedTarget() throws Exception {
        ctx = new ClassPathXmlApplicationContext("/common/service/applicationContext-test.xml");

        UserManager userManager = (UserManager) ctx.getBean("target");
        userManager.setUserDao(userDao);
        userManager.setPasswordEncoder(passwordEncoder);
        userManager.setRoleManager(roleManager);

        return userManager;
    }
}
