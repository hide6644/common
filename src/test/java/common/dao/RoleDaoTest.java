package common.dao;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import common.Constants;
import common.model.Role;

public class RoleDaoTest extends BaseDaoTestCase {

    @Autowired
    private RoleDao dao;

    @Test
    public void testGetRoleInvalid() {
        Role role = dao.getByNameEquals("badrolename");

        assertNull(role);
    }

    @Test
    public void testGetRole() {
        Role role = dao.getByNameEquals(Constants.USER_ROLE);

        assertNotNull(role);
    }

    @Test
    public void testUpdateRole() {
        Role role = dao.getByNameEquals("ROLE_USER");
        role.setDescription("test descr");
        dao.save(role);
        role = dao.getByNameEquals("ROLE_USER");

        assertEquals("test descr", role.getDescription());
    }

    @Test
    public void testAddAndRemoveRole() {
        Role role = new Role("testrole");
        role.setDescription("new role descr");
        dao.save(role);
        role = dao.getByNameEquals("testrole");

        assertNotNull(role.getDescription());

        dao.removeRole("testrole");
        role = dao.getByNameEquals("testrole");

        assertNull(role);
    }

    @Test
    public void testFindByNamedQuery() {
        HashMap<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("name", Constants.USER_ROLE);
        List<Role> roles = dao.findByNamedQuery("Role.findByNameEquals", queryParams);

        assertNotNull(roles);
        assertTrue(roles.size() > 0);
    }
}
