package common.validator.constraints;

/**
 * CompareStringsにて指定可能な比較方法.
 */
public enum ComparisonMode {

    /** 等しい */
    EQUAL,
    /** 等しい(大文字小文字を考慮しない) */
    EQUAL_IGNORE_CASE,
    /** 等しくない */
    NOT_EQUAL,
    /** 等しくない(大文字小文字を考慮しない) */
    NOT_EQUAL_IGNORE_CASE
}
