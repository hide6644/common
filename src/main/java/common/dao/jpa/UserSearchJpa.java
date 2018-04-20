package common.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import common.Constants;
import common.model.User;

/**
 * UserのHibernate Search DAOの実装クラス.
 */
@Repository("userSearch")
public class UserSearchJpa extends HibernateSearchJpa<User> {

    /** Entity Managerクラス */
    @PersistenceContext(unitName = Constants.PERSISTENCE_UNIT_NAME)
    protected EntityManager entityManager;

    /**
     * デフォルト・コンストラクタ.
     */
    public UserSearchJpa() {
        super(User.class);
    }
}
