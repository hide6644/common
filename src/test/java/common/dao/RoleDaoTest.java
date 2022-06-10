package common.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import common.Constants;
import common.dao.jpa.RoleDao;
import common.entity.Role;

public class RoleDaoTest extends BaseDaoTestCase {

    @Autowired
    private RoleDao dao;

    @Test
    public void testGetRoleInvalid() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            dao.getReferenceById("badrolename");
        });
    }

    @Test
    public void testGetRole() {
        Role role = dao.getReferenceById(Constants.USER_ROLE);

        assertNotNull(role);
        assertTrue(role.equals(new Role(Constants.USER_ROLE)));
    }

    @Test
    public void testUpdateRole() {
        Role role = dao.getReferenceById(Constants.USER_ROLE);
        role.setDescription("test descr");
        dao.save(role);
        role = dao.getReferenceById(Constants.USER_ROLE);

        assertEquals("test descr", role.getDescription());
    }

    @Test
    public void testAddAndRemoveRole() {
        Role role = new Role("testrole");
        role.setDescription("new role descr");
        dao.save(role);
        role = dao.getReferenceById("testrole");

        assertNotNull(role.getDescription());

        dao.deleteById("testrole");

        Assertions.assertThrows(DataAccessException.class, () -> {
            dao.getReferenceById("testrole");
        });
    }
}
