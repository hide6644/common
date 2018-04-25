package common.dao.jpa;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import common.model.User;

/**
 * ユーザDAOの検索条件定義クラス.
 */
public class UserSpecifications {

    /**
     * プライベート・コンストラクタ.
     */
    private UserSpecifications() {
    }

    /**
     * 指定文字をユーザ名に含む.
     *
     * @param username
     *            ユーザ名
     * @return {@link Specification}
     */
    public static Specification<User> usernameContains(String username) {
        return StringUtils.isEmpty(username) ? null : (root, query, cb) -> {
            return cb.like(root.get("username"), "%" + username + "%");
        };
    }

    /**
     * 指定文字をｅメールに含む.
     *
     * @param email
     *            ｅメール
     * @return {@link Specification}
     */
    public static Specification<User> emailContains(String email) {
        return StringUtils.isEmpty(email) ? null : (root, query, cb) -> {
            return cb.like(root.get("email"), "%" + email + "%");
        };
    }
}
