package common.webapp.controller;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import common.model.PaginatedList;
import common.model.Role;
import common.model.User;
import common.model.Users;
import common.service.UserManager;
import common.webapp.filter.FlashMap;

public class UserListControllerTest extends BaseControllerTestCase {

    @Autowired
    private UserListController c;

    @Test
    public void testHandleRequest() throws Exception {
        ModelAndView mav = c.handleRequest(new User(), null);
        Map<String, Object> m = mav.getModel();

        assertNotNull(m.get("paginatedList"));
        assertEquals("admin/master/users", mav.getViewName());
    }

    @Test
    public void testSearch() throws Exception {
        UserManager userManager = (UserManager) applicationContext.getBean("userManager");
        userManager.reindex();

        User user = new User("admin");
        user.setEmail("admin");
        ModelAndView mav = c.handleRequest(user, null);
        Map<String, Object> m = mav.getModel();
        @SuppressWarnings("unchecked")
        PaginatedList<User> results = (PaginatedList<User>) m.get("paginatedList");

        assertNotNull(results);
        assertTrue(results.getAllRecordCount() >= 1);
        assertEquals("admin/master/users", mav.getViewName());
    }

    @Test
    public void testRemove() throws Exception {
        MockHttpServletRequest request = newGet("/userform.html");
        request.setRemoteUser("administrator");

        User user = new User();
        user.setId(-2L);

        Set<Role> roles = new HashSet<Role>();
        roles.add(new Role("ROLE_ADMIN"));

        Set<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();
        authorities.addAll(roles);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("administrator", "administrator", authorities);
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        c.onSubmit(new String[]{"-2"}, request);

        assertNotNull(FlashMap.get("flash_info_messages"));
    }

    @Test
    public void testCsvList() throws Exception {
        ModelAndView mav = c.setupCsvList();
        Map<String, Object> m = mav.getModel();

        assertNotNull(m.get("csv"));
        assertEquals("admin/master/csv/users", mav.getViewName());
    }

    @Test
    public void testXlsList() throws Exception {
        ModelAndView mav = c.setupXlsList();
        Map<String, Object> m = mav.getModel();

        assertNotNull(m.get("users"));
        assertEquals("admin/master/jxls/users", mav.getViewName());
    }

    @Test
    public void testXmlList() throws Exception {
        Users users = c.setupXmlList();

        assertNotNull(users);
        assertEquals(2, users.getCount());
    }
}
