package common.dao;

import common.model.Role;

public interface RoleDao extends GenericDao<Role, Long> {

    Role getRoleByName(String name);

    void removeRole(String name);
}
