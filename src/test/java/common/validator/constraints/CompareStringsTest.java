package common.validator.constraints;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@CompareStrings.List({
        @CompareStrings(
                propertyNames = { "equalTest1", "equalTest2" },
                comparisonMode = ComparisonMode.EQUAL
        ),
        @CompareStrings(
                propertyNames = { "equalIgnoreCaseTest1", "equalIgnoreCaseTest2" },
                comparisonMode = ComparisonMode.EQUAL_IGNORE_CASE
        ),
        @CompareStrings(
                propertyNames = { "notEqualTest1", "notEqualTest2" },
                comparisonMode = ComparisonMode.NOT_EQUAL
        ),
        @CompareStrings(
                propertyNames = { "notEqualIgnoreCaseTest1", "notEqualIgnoreCaseTest2" },
                comparisonMode = ComparisonMode.NOT_EQUAL_IGNORE_CASE
        )
    })
class CompareStringsTest {

    private static Validator validator;

    String equalTest1;

    String equalTest2;

    String equalIgnoreCaseTest1;

    String equalIgnoreCaseTest2;

    String notEqualTest1;

    String notEqualTest2;

    String notEqualIgnoreCaseTest1;

    String notEqualIgnoreCaseTest2;

    @BeforeAll
    static void setUpClass(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testEqual() throws Exception {
        CompareStringsTest bean = new CompareStringsTest();
        bean.equalTest1 = "same";
        bean.equalTest2 = "same";
        bean.equalIgnoreCaseTest1 = "same";
        bean.equalIgnoreCaseTest2 = "SAME";
        bean.notEqualTest1 = "same";
        bean.notEqualTest2 = "not same";
        bean.notEqualIgnoreCaseTest1 = "same";
        bean.notEqualIgnoreCaseTest2 = "NOT SAME";

        assertEquals(0, validator.validate(bean).size());

        bean.equalTest2 = "not same";
        bean.equalIgnoreCaseTest2 = "NOT SAME";
        bean.notEqualTest2 = "same";
        bean.notEqualIgnoreCaseTest2 = "SAME";

        assertEquals(4, validator.validate(bean).size());
    }
}
