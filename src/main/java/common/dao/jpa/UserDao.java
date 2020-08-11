package common.dao.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import common.entity.User;

/**
 * ユーザDAOインターフェイス.
 */
public interface UserDao extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * 指定されたユーザを取得する.
     *
     * @param username
     *            ユーザ名
     * @return ユーザ
     */
    User findByUsername(@Param("username") String username);

    /**
     * 指定されたユーザのパスワードを取得する.
     *
     * @param id
     *            ユーザID
     * @return パスワード
     */
    String findPasswordById(@Param("id") Long id);
}
