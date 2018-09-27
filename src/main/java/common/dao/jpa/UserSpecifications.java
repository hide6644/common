package common.dao.jpa;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import common.entity.User;

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
        return buildSpecification("username", username);

    }

    /**
     * 指定文字をｅメールに含む.
     *
     * @param email
     *            ｅメール
     * @return {@link Specification}
     */
    public static Specification<User> emailContains(String email) {
        return buildSpecification("email", email);
    }

    /**
     * 検索条件を作成する.
     *
     * @param columnName
     *            列名
     * @param pattern
     *            検索パターン
     * @return {@link Specification}
     */
    private static Specification<User> buildSpecification(String columnName, String pattern) {
        return StringUtils.isEmpty(pattern) ? null : (root, query, cb) -> cb.like(root.get(columnName), "%" + pattern + "%");
    }
}
