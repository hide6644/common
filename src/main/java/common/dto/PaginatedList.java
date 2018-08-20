package common.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

/**
 * ページング処理、情報保持クラス.
 */
public final class PaginatedList<T> {

    /** デフォルトの1ページあたりのレコード数 */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /** デフォルトのページリンクの表示件数 */
    public static final int DEFAULT_RANGE_SIZE = 2;

    /** ページング情報 */
    private final Page<T> paged;

    /** ページリンクの表示数 */
    private final int pageRangeSize;

    /**
     * コンストラクタ.
     *
     * @param paged
     *            ページング情報
     */
    public PaginatedList(Page<T> paged) {
        this(paged, null);
    }

    /**
     * コンストラクタ.
     *
     * @param paged
     *            ページング情報
     * @param pageRangeSize
     *            ページリンクの表示数
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
     * ページリンクの表示数を取得する.
     *
     * @return ページリンクの表示数
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
     * 前のページ番号を取得する.
     *
     * @return 前のページ番号
     */
    public int getPrePageNumber() {
        return paged.getNumber();
    }

    /**
     * 次のページ番号を取得する.
     *
     * @return 次のページ番号
     */
    public int getNextPageNumber() {
        return paged.getNumber() + 2;
    }

    /**
     * 現在ページの最初のレコード番号を取得する.
     *
     * @return 現在ページの最初のレコード番号
     */
    public int getCurrentStartRecordNumber() {
        return paged.getNumber() * paged.getSize() + 1;
    }

    /**
     * 現在ページの最後のレコード番号を取得する.
     *
     * @return 現在ページの最後のレコード番号
     */
    public int getCurrentEndRecordNumber() {
        return (paged.getNumber() + 1) * paged.getSize();
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
