package common.validator.constraints;

import static org.junit.jupiter.api.Assertions.*;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

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
public class CompareStringsTest {

    private static Validator validator;

    public String equalTest1;

    public String equalTest2;

    public String equalIgnoreCaseTest1;

    public String equalIgnoreCaseTest2;

    public String notEqualTest1;

    public String notEqualTest2;

    public String notEqualIgnoreCaseTest1;

    public String notEqualIgnoreCaseTest2;

    @BeforeAll
    public static void setUpClass(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    public void testEqual() throws Exception {
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
