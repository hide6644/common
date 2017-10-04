package common.webapp.converter;

/**
 * アップロードファイルタイプ
 */
public enum FileType {

    /** ファイルタイプ(XML) */
    XML,
    /** ファイルタイプ(EXCEL) */
    EXCEL,
    /** ファイルタイプ(CSV) */
    CSV;

    /**
     * 全ての型をキャッシュする.
     */
    private static final FileType[] ENUMS = FileType.values();

    /**
     * int値をアップロードファイルタイプに変換する.
     *
     * @param fileType
     *            アップロードファイルタイプのint値
     * @return アップロードファイルタイプ
     */
    public static FileType of(int fileType) {
        return ENUMS[fileType - 1];
    }

    /**
     * アップロードファイルタイプのint値を取得する.
     *
     * @return アップロードファイルタイプのint値
     */
    public int getValue() {
        return ordinal() + 1;
    }
}
