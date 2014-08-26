package common.webapp.controller;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import common.model.PaginatedList;
import common.model.User;
import common.service.UserManager;

public class UserListControllerTest extends BaseControllerTestCase {

    @Autowired
    private UserListController c;

    @Test
    public void testHandleRequest() throws Exception {
        ModelAndView mav = c.handleRequest(new User(), null);
        Map<String, Object> m = mav.getModel();

        assertNotNull(m.get("paginatedList"));
        assertEquals("admin/master/userList", mav.getViewName());
    }

    @Test
    public void testSearch() throws Exception {
        // reindex before searching
        UserManager userManager = (UserManager) applicationContext.getBean("userManager");
        userManager.reindex();

        ModelAndView mav = c.handleRequest(new User("admin"), null);
        Map<String, Object> m = mav.getModel();
        @SuppressWarnings("unchecked")
        PaginatedList<User> results = (PaginatedList<User>) m.get("paginatedList");

        assertNotNull(results);
        assertTrue(results.getAllRecordCount() >= 1);
        assertEquals("admin/master/userList", mav.getViewName());
    }
}
