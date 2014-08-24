package common.validator.constraints;

/**
 * CompareStringsにて指定可能な比較方法.
 *
 * @author hide6644
 */
public enum ComparisonMode {
    /** 等しいこと */
    EQUAL,
    /** 等しいこと(大文字小文字を考慮しない) */
    EQUAL_IGNORE_CASE,
    /** 等しくないこと */
    NOT_EQUAL,
    /** 等しくないこと(大文字小文字を考慮しない) */
    NOT_EQUAL_IGNORE_CASE
}
