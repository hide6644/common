package common.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import common.model.User;

/**
 * ユーザDAOインターフェイス.
 */
public interface UserDao extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * 指定されたユーザのパスワードを取得する.
     *
     * @param id
     *            ユーザID
     * @return パスワード
     */
    String findPasswordById(Long id);
}
