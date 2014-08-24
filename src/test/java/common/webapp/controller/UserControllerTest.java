package common.webapp.controller;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import common.Constants;
import common.model.Role;
import common.model.User;
import common.service.UserManager;
import common.webapp.filter.FlashMap;

public class UserControllerTest extends BaseControllerTestCase {

    @Autowired
    private UserController c = null;

    private MockHttpServletRequest request;

    private User user;

    @Test
    public void testAdd() throws Exception {
        log.debug("testing add new user...");
        request = newGet("/userform.html");
        request.addParameter("method", "Add");
        request.addUserRole(Constants.ADMIN_ROLE);

        user = c.showForm(request, new MockHttpServletResponse());
        assertNull(user.getUsername());
    }

    @Test
    public void testAddWithoutPermission() throws Exception {
        log.debug("testing add new user...");
        request = newGet("/userform.html");
        request.addParameter("method", "Add");

        try {
            c.showForm(request, new MockHttpServletResponse());
            fail("AccessDeniedException not thrown...");
        } catch (AccessDeniedException ade) {
            assertNotNull(ade.getMessage());
        }
    }

    @Test
    public void testCancel() throws Exception {
        log.debug("testing cancel...");
        request = newPost("/userform.html");
        request.addParameter("cancel", "");

        BindingResult errors = new DataBinder(user).getBindingResult();
        String view = c.onSubmit(user, errors, request, new MockHttpServletResponse());

        assertEquals("redirect:/top", view);
    }

    @Test
    public void testEdit() throws Exception {
        log.debug("testing edit...");
        request = newGet("/userform.html");
        request.addParameter("id", "-1"); // regular user
        request.addUserRole(Constants.ADMIN_ROLE);

        User user = c.showForm(request, new MockHttpServletResponse());
        assertEquals("admin", user.getFirstName());
    }

    @Test
    public void testEditWithoutPermission() throws Exception {
        log.debug("testing edit...");
        request = newGet("/userform.html");
        request.addParameter("id", "-2"); // regular user

        try {
            c.showForm(request, new MockHttpServletResponse());
            fail("AccessDeniedException not thrown...");
        } catch (AccessDeniedException ade) {
            assertNotNull(ade.getMessage());
        }
    }

    @Test
    public void testEditProfile() throws Exception {
        log.debug("testing edit profile...");
        request = newGet("/userform.html");
        request.setRemoteUser("normaluser");

        user = c.showForm(request, new MockHttpServletResponse());
        assertEquals("user", user.getFirstName());
    }

    @Test
    public void testSave() throws Exception {
        request = newPost("/userform.html");
        // set updated properties first since adding them later will
        // result in multiple parameters with the same name getting sent
        User user = ((UserManager) applicationContext.getBean("userManager")).getUser("-1");
        user.setConfirmPassword(user.getPassword());
        user.setLastName("Updated Last Name");

        request.setRemoteUser(user.getUsername());

        BindingResult errors = new DataBinder(user).getBindingResult();
        c.onSubmit(user, errors, request, new MockHttpServletResponse());

        assertFalse(errors.hasErrors());
        assertNotNull(FlashMap.get("flash_info_messages"));
    }

    @Test
    public void testRemove() throws Exception {
        request = newPost("/userform.html");
        request.addParameter("delete", "");
        user = new User();
        user.setId(-2L);

        Set<Role> roles = new HashSet<Role>();
        roles.add(new Role("ROLE_ADMIN"));

        Set<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();
        authorities.addAll(roles);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("administrator", "administrator", authorities);
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        BindingResult errors = new DataBinder(user).getBindingResult();
        c.onSubmit(user, errors, request, new MockHttpServletResponse());

        assertNotNull(FlashMap.get("flash_info_messages"));
    }
}
