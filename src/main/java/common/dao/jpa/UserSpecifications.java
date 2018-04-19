package common.dao.jpa;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import common.model.User;

/**
 * ユーザDAOの検索条件定義クラス.
 */
public class UserSpecifications {

    /**
     * 指定文字をユーザー名に含む.
     */
    public static Specification<User> usernameContains(String username) {
        return StringUtils.isEmpty(username) ? null : (root, query, cb) -> {
            return cb.like(root.get("username"), "%" + username + "%");
        };
    }

    /**
     * 指定文字をメールアドレスに含む.
     */
    public static Specification<User> emailContains(String email) {
        return StringUtils.isEmpty(email) ? null : (root, query, cb) -> {
            return cb.like(root.get("email"), "%" + email + "%");
        };
    }
}
