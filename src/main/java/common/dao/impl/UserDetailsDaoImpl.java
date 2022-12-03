package common.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import common.Constants;
import common.entity.User;

/**
 * ユーザ認証DAOクラス.
 */
@Repository("userDetails")
public class UserDetailsDaoImpl implements UserDetailsService {

    /** Entity Managerクラス */
    @PersistenceContext(unitName = Constants.PERSISTENCE_UNIT_NAME)
    private EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        try {
            return entityManager.createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();
        } catch (NoResultException e) {
            throw new UsernameNotFoundException("user '" + username + "' not found...");
        }
    }
}
