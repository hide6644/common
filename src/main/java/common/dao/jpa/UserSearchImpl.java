package common.dao.jpa;

import org.springframework.stereotype.Repository;

import common.entity.User;

/**
 * UserのHibernate Search DAOの実装クラス.
 */
@Repository("userSearch")
public class UserSearchImpl extends HibernateSearchImpl<User> {

    /**
     * デフォルト・コンストラクタ.
     */
    public UserSearchImpl() {
        super(User.class);
    }
}
