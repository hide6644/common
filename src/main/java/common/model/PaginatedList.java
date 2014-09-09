package common.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ページング処理情報保持クラス.
 */
public final class PaginatedList<T> {

    /** デフォルトの表示するページ番号 */
    public static final int DEFAULT_CURRENT_PAGE_NUMBER = 1;

    /** デフォルトの1ページあたりのレコード数 */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /** デフォルトのページリンク一覧の表示件数 */
    public static final int DEFAULT_RANGE_SIZE = 2;

    /** 総レコード数 */
    private long allRecordCount;

    /** 検索条件 */
    private Object searchCondition;

    /** 現在のページ番号 */
    private int currentPageNumber;

    /** 1ページあたりのレコード数 */
    private int pageSize;

    /** ページリンク一覧の表示件数 */
    private int pageRangeSize;

    /** 現在のページ */
    private List<T> currentPage;

    /**
     * コンストラクタ.
     *
     * @param currentPageNumber
     *            表示するページ番号
     */
    public PaginatedList(Integer currentPageNumber) {
        this(currentPageNumber, null, null);
    }

    /**
     * コンストラクタ.
     *
     * @param currentPageNumber
     *            表示するページ番号
     * @param pageSize
     *            1ページあたりのレコード数
     * @param pageRangeSize
     *            ページリンク一覧の表示件数
     */
    public PaginatedList(Integer currentPageNumber, Integer pageSize, Integer pageRangeSize) {
        this.currentPageNumber = currentPageNumber != null ? currentPageNumber : DEFAULT_CURRENT_PAGE_NUMBER;
        this.pageSize = pageSize != null ? pageSize : DEFAULT_PAGE_SIZE;
        this.pageRangeSize = pageRangeSize != null ? pageRangeSize : DEFAULT_RANGE_SIZE;
    }

    /**
     * ページングなしの総レコード数を取得する.
     *
     * @return ページングなしの総レコード数
     */
    public long getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * ページングなしの総レコード数を設定する.
     *
     * @param allRecordCount
     *            ページングなし総レコード数
     */
    public void setAllRecordCount(long allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    /**
     * 検索条件を取得する.
     *
     * @return 検索条件
     */
    public Object getSearchCondition() {
        return searchCondition;
    }

    /**
     * 検索条件を設定する.
     *
     * @param searchCondition
     *            検索条件
     */
    public void setSearchCondition(Object searchCondition) {
        this.searchCondition = searchCondition;
    }

    /**
     * 現在のページ番号を取得する.
     *
     * @return 現在のページ番号
     */
    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    /**
     * 現在のページ番号を設定する.
     *
     * @param currentPageNumber
     *            現在のページ番号
     */
    public void setCurrentPageNumber(int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    /**
     * 1ページあたりのレコード数を取得する.
     *
     * @return 1ページあたりのレコード数
     */
    public int getPageSize() {
        return pageSize;
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
        return currentPage;
    }

    /**
     * 現在のページを設定する.
     *
     * @param page
     *            現在のページ
     */
    public void setCurrentPage(List<T> page) {
        this.currentPage = page;
    }

    /**
     * 総ページ数を取得する.
     *
     * @return 総ページ数
     */
    public int getAllPageCount() {
        return (int) Math.ceil(((double) allRecordCount) / pageSize);
    }

    /**
     * 前のページが存在するか.
     *
     * @return true 存在する, false 存在しない
     */
    public boolean isExistPrePage() {
        return getPrePageNumber() > 0;
    }

    /**
     * 次のページが存在するか.
     *
     * @return true 存在する, false 存在しない
     */
    public boolean isExistNextPage() {
        return getNextPageNumber() < getAllPageCount() + 1;
    }

    /**
     * 前のページ番号を取得する.
     *
     * @return 前のページ番号
     */
    public int getPrePageNumber() {
        return currentPageNumber - 1;
    }

    /**
     * 次のページ番号を取得する.
     *
     * @return 次のページ番号
     */
    public int getNextPageNumber() {
        return currentPageNumber + 1;
    }

    /**
     * 現在ページの最初のレコード番号を取得する.
     *
     * @return 現在ページの最初のレコード番号
     */
    public int getCurrentStartRecordNumber() {
        return (currentPageNumber - 1) * pageSize;
    }

    /**
     * 現在ページの最後のレコード番号を取得する.
     *
     * @return 現在ページの最後のレコード番号
     */
    public int getCurrentEndRecordNumber() {
        return currentPageNumber * pageSize;
    }

    /**
     * ページリンク一覧を取得する.
     *
     * @return ページリンク一覧
     */
    public List<Integer> getPageNumberList() {
        List<Integer> resultList = new ArrayList<Integer>();
        for (int i = currentPageNumber - pageRangeSize; i < currentPageNumber; i++) {
            if (i < 1) {
                continue;
            }
            resultList.add(i);
        }

        resultList.add(currentPageNumber);

        int endPageNumber = (currentPageNumber + pageRangeSize);
        for (int i = currentPageNumber + 1; i <= endPageNumber && i <= getAllPageCount(); i++) {
            resultList.add(i);
        }

        int limitSize = pageRangeSize * 2 + 1;
        if (!resultList.isEmpty() && resultList.size() < limitSize) {
            Integer firstElements = resultList.get(0);
            Integer lastElements = resultList.get(resultList.size() - 1);

            if (firstElements.intValue() > 1) {
                for (int i = firstElements.intValue() - 1; resultList.size() < limitSize && i > 0; i--) {
                    resultList.add(0, i);
                }
            }

            for (int i = lastElements.intValue() + 1; resultList.size() < limitSize && i <= getAllPageCount(); i++) {
                resultList.add(i);
            }
        }

        return resultList;
    }
}
