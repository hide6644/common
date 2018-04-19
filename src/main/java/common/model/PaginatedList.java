package common.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

/**
 * ページング処理情報保持クラス.
 */
public final class PaginatedList<T> {

    /** デフォルトの1ページあたりのレコード数 */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /** デフォルトのページリンク一覧の表示件数 */
    public static final int DEFAULT_RANGE_SIZE = 2;

    /** TODO */
    private Page<T> paged;

    /** ページリンク一覧の表示件数 */
    private int pageRangeSize;

    /**
     * コンストラクタ.
     *
     * @param paged
     *            TODO
     */
    public PaginatedList(Page<T> paged) {
        this(paged, null);
    }

    /**
     * コンストラクタ.
     *
     * @param paged
     *            TODO
     * @param pageRangeSize
     *            ページリンク一覧の表示件数
     */
    public PaginatedList(Page<T> paged, Integer pageRangeSize) {
        this.paged = paged;
        this.pageRangeSize = pageRangeSize != null ? pageRangeSize : DEFAULT_RANGE_SIZE;
    }

    /**
     * ページングなしの総レコード数を取得する.
     *
     * @return ページングなしの総レコード数
     */
    public long getAllRecordCount() {
        return paged.getTotalElements();
    }

    /**
     * 現在のページ番号を取得する.
     *
     * @return 現在のページ番号
     */
    public int getCurrentPageNumber() {
        return paged.getNumber() + 1;
    }

    /**
     * 1ページあたりのレコード数を取得する.
     *
     * @return 1ページあたりのレコード数
     */
    public int getPageSize() {
        return paged.getSize();
    }

    /**
     * ページリンク一覧の表示件数を取得する.
     *
     * @return ページリンク一覧の表示件数
     */
    public int getPageRangeSize() {
        return pageRangeSize;
    }

    /**
     * 現在のページを取得する.
     *
     * @return 現在のページ
     */
    public List<T> getCurrentPage() {
        return paged.getContent();
    }

    /**
     * 総ページ数を取得する.
     *
     * @return 総ページ数
     */
    public int getAllPageCount() {
        return paged.getTotalPages();
    }

    /**
     * 前のページが存在するか.
     *
     * @return true 存在する, false 存在しない
     */
    public boolean isExistPrePage() {
        return paged.hasPrevious();
    }

    /**
     * 次のページが存在するか.
     *
     * @return true 存在する, false 存在しない
     */
    public boolean isExistNextPage() {
        return paged.hasNext();
    }

    /**
     * ページリンク一覧を取得する.
     *
     * @return ページリンク一覧
     */
    public List<Integer> getPageNumberList() {
        List<Integer> pageNumberList = new ArrayList<>();
        pageNumberList.add(getCurrentPageNumber());

        int limitSize = pageRangeSize * 2 + 1;

        if (getCurrentPageNumber() > 1) {
            for (int i = getCurrentPageNumber() - 1; pageNumberList.size() <= pageRangeSize && i > 0; i--) {
                pageNumberList.add(0, i);
            }
        }

        for (int i = getCurrentPageNumber() + 1; pageNumberList.size() < limitSize && i <= getAllPageCount(); i++) {
            pageNumberList.add(i);
        }

        for (int i = pageNumberList.get(0) - 1; pageNumberList.size() < limitSize && i > 0; i--) {
            pageNumberList.add(0, i);
        }

        return pageNumberList;
    }
}
