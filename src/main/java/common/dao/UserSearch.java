package common.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;

import common.dto.UserSearchCriteria;
import common.entity.User;

/**
 * UserのHibernate Search DAOのインターフェース.
 */
public interface UserSearch extends HibernateSearch {

    /**
     * 指定の範囲のオブジェクトを取得(全文検索)する.
     *
     * @param userSearchCriteria
     *            ユーザ検索条件を保持するクラス
     * @param pageRequest
     *            ページング情報を保持するクラス
     * @return 検索結果のリスト
     */
    List<User> search(UserSearchCriteria userSearchCriteria, PageRequest pageRequest);

    /**
     * ファセットを作成する.
     *
     * @param field
     *            対象となる項目
     * @param maxCount
     *            ファセットの最大件数
     * @return ファセットのマップ
     */
    Map<String, Long> facet(String field, int maxCount);
}
