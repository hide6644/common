package common.webapp.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import common.dto.PaginatedList;
import common.dto.UserSearchCriteria;
import common.dto.UserSearchResults;
import common.entity.Role;
import common.entity.User;
import common.service.UserManager;
import common.webapp.filter.FlashMap;

class UsersControllerTest extends BaseControllerTestCase {

    @Autowired
    private UsersController c;

    @Test
    void testShowForm() {
        PaginatedList<UserSearchResults> paginatedList = c.showForm(new UserSearchCriteria(), null);

        assertNotNull(paginatedList);
    }

    @Test
    void testSearch() {
        UserManager userManager = (UserManager) applicationContext.getBean("userManager");
        userManager.reindex();

        UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
        userSearchCriteria.setUsername("admin");
        userSearchCriteria.setEmail("admin");
        PaginatedList<UserSearchResults> results = c.showForm(userSearchCriteria, null);

        assertNotNull(results);
        assertTrue(results.getAllRecordCount() >= 1);
    }

    @Test
    void testRemove() {
        MockHttpServletRequest request = newGet("/userform.html");
        request.setRemoteUser("administrator");
        setAuthentication();

        c.onSubmit(new String[]{"-2"}, request);

        assertNotNull(FlashMap.get("flash_info_messages"));
    }

    @Test
    void testCsvList() throws Exception {
        MockHttpServletRequest request = newGet("/admin/master/users.csv");
        request.setRemoteUser("administrator");
        setAuthentication();

        mockMvc.perform(get("/admin/master/users.csv"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/master/csv/users"))
                .andExpect(content().contentType("Application/Octet-Stream"));
    }

    @Test
    void testXlsList() throws Exception {
        MockHttpServletRequest request = newGet("/admin/master/users.xlsx");
        request.setRemoteUser("administrator");
        setAuthentication();

        mockMvc.perform(get("/admin/master/users.xlsx"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/master/jxls/users"))
                .andExpect(content().contentType("Application/Vnd.ms-Excel"));
    }

    @Test
    void testXmlList() throws Exception {
        MockHttpServletRequest request = newGet("/admin/master/users.xml");
        request.setRemoteUser("administrator");
        setAuthentication();

        mockMvc.perform(get("/admin/master/users.xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/xml"));
    }

    private void setAuthentication() {
        User user = new User();
        user.setId(-2L);

        Set<Role> roles = new HashSet<>();
        roles.add(new Role("ROLE_ADMIN"));

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        authorities.addAll(roles);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("administrator", "administrator", authorities);
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
