package common.validator.constraints;

import static org.junit.Assert.*;

import java.io.InputStream;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class NotEmptyFileTest {

    private Validator validator;

    @NotEmptyFile
    private MultipartFile fileData;

    @Before
    public void setUp() throws Exception {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    public void testEmpty() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/validator/constraints/empty.csv");
        fileData = new MockMultipartFile("fileData", input);

        NotEmptyFileTest bean = new NotEmptyFileTest();
        bean.setFileData(fileData);

        assertEquals(1, validator.validate(bean).size());
    }

    @Test
    public void testNotEmpty() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/validator/constraints/notempty.csv");
        fileData = new MockMultipartFile("fileData", input);

        NotEmptyFileTest bean = new NotEmptyFileTest();
        bean.setFileData(fileData);

        assertEquals(0, validator.validate(bean).size());
    }

    public void setFileData(MultipartFile fileData) {
        this.fileData = fileData;
    }
}