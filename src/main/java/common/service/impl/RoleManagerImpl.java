package common.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import common.dao.RoleDao;
import common.dto.LabelValue;
import common.entity.Role;
import common.service.RoleManager;

/**
 * 権限処理の実装クラス.
 */
@Service("roleManager")
public class RoleManagerImpl implements RoleManager {

    /** 権限DAO */
    RoleDao roleDao;

    /**
     * コンストラクタ
     *
     * @param roleDao
     *            権限DAO
     */
    @Autowired
    public RoleManagerImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role getRole(String rolename) {
        return roleDao.findByName(rolename);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Role> getRoles(Set<Role> rolenames) {
        return rolenames.stream().map(role -> getRole(role.getName())).collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRole(String rolename) {
        roleDao.removeByName(rolename);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LabelValue> getLabelValues() {
        return Optional.ofNullable(roleDao.findAll()).orElseGet(ArrayList::new).stream()
                .map(role -> new LabelValue(role.getDescription(), role.getName()))
                .collect(Collectors.toList());
    }
}
