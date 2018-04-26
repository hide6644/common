package common.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import common.Constants;
import common.model.Role;

public class RoleDaoTest extends BaseDaoTestCase {

    @Autowired
    private RoleDao dao;

    @Test
    public void testGetRoleInvalid() {
        Role role = dao.findByName("badrolename");

        assertNull(role);
    }

    @Test
    public void testGetRole() {
        Role role = dao.findByName(Constants.USER_ROLE);

        assertNotNull(role);
    }

    @Test
    public void testUpdateRole() {
        Role role = dao.findByName("ROLE_USER");
        role.setDescription("test descr");
        dao.save(role);
        role = dao.findByName("ROLE_USER");

        assertEquals("test descr", role.getDescription());
    }

    @Test
    public void testAddAndRemoveRole() {
        Role role = new Role("testrole");
        role.setDescription("new role descr");
        dao.save(role);
        role = dao.findByName("testrole");

        assertNotNull(role.getDescription());

        dao.removeByName("testrole");
        role = dao.findByName("testrole");

        assertNull(role);
    }
}
