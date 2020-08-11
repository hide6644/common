package common.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import common.dao.jpa.RoleDao;
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
    @Transactional
    public Role getRole(String name) {
        return roleDao.getOne(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Set<Role> getRoles(Set<Role> names) {
        return names.stream().map(role -> getRole(role.getName())).collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void removeRole(String name) {
        roleDao.deleteById(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<LabelValue> getLabelValues() {
        return Optional.ofNullable(roleDao.findAll()).orElseGet(ArrayList::new).stream()
                .map(role -> LabelValue.of(role.getDescription(), role.getName()))
                .collect(Collectors.toList());
    }
}
