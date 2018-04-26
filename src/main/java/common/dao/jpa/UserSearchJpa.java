package common.dao.jpa;

import org.springframework.stereotype.Repository;

import common.model.User;

/**
 * UserのHibernate Search DAOの実装クラス.
 */
@Repository("userSearch")
public class UserSearchJpa extends HibernateSearchJpa<User> {

    /**
     * デフォルト・コンストラクタ.
     */
    public UserSearchJpa() {
        super(User.class);
    }
}
