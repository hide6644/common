package common.validator.constraints;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class NotEmptyFileTest {

    private static Validator validator;

    @NotEmptyFile
    private MultipartFile fileData;

    @BeforeAll
    static void setUpClass(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testEmpty() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/validator/constraints/empty.csv");
        fileData = new MockMultipartFile("fileData", input);

        NotEmptyFileTest bean = new NotEmptyFileTest();
        bean.setFileData(fileData);

        assertEquals(1, validator.validate(bean).size());
    }

    @Test
    void testNotEmpty() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/validator/constraints/notempty.csv");
        fileData = new MockMultipartFile("fileData", input);

        NotEmptyFileTest bean = new NotEmptyFileTest();
        bean.setFileData(fileData);

        assertEquals(0, validator.validate(bean).size());
    }

    void setFileData(MultipartFile fileData) {
        this.fileData = fileData;
    }
}
