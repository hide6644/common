package common.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 全文検索用の検索文字列、検索項目を保持するクラス.
 */
public class SearchTermAndField {

    /** 検索文字列のリスト */
    private List<String> term;

    /** 検索項目のリスト */
    private List<String> field;

    /**
     * コンストラクタ.
     */
    public SearchTermAndField() {
        super();
        this.term = new ArrayList<>();
        this.field = new ArrayList<>();
    }

    /**
     * 検索文字列、検索項目のリストに追加する.
     *
     * @param term
     *            検索文字列
     * @param field
     *            検索項目
     */
    public void addTermAndField(String term, String field) {
        this.term.add(term);
        this.field.add(field);
    }

    /**
     * 検索文字列のリストに要素がない場合にtrueを返す.
     *
     * @return true:要素がない、false:要素がある
     */
    public boolean isEmpty() {
        return this.term.isEmpty();
    }

    /**
     * 検索文字列の配列を取得する.
     *
     * @return 検索文字列の配列
     */
    public String[] getTermToArray() {
        return term.toArray(new String[term.size()]);
    }

    /**
     * 検索項目の配列を取得する.
     *
     * @return 検索項目の配列
     */
    public String[] getFieldToArray() {
        return field.toArray(new String[field.size()]);
    }
}
