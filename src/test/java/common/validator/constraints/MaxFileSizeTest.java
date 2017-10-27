package common.validator.constraints;

import static org.junit.Assert.*;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class MaxFileSizeTest {

    private Validator validator;

    @MaxFileSize(max = 3, unitSign = "")
    private MultipartFile fileData;

    @MaxFileSize(max = 3, unitSign = "K")
    private MultipartFile fileDataK;

    @MaxFileSize(max = 3)
    private MultipartFile fileDataM;

    @Before
    public void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    public void testEmpty() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        fileData = new MockMultipartFile("fileData", classLoader.getResourceAsStream("common/validator/constraints/empty.csv"));
        fileDataK = new MockMultipartFile("fileData", classLoader.getResourceAsStream("common/validator/constraints/empty.csv"));
        fileDataM = new MockMultipartFile("fileData", classLoader.getResourceAsStream("common/validator/constraints/empty.csv"));

        MaxFileSizeTest bean = new MaxFileSizeTest();
        bean.setFileData(fileData);
        bean.setFileDataK(fileDataK);
        bean.setFileDataM(fileDataM);

        assertEquals(0, validator.validate(bean).size());
    }

    @Test
    public void testNotEmpty() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        fileData = new MockMultipartFile("fileData", classLoader.getResourceAsStream("common/validator/constraints/notempty.csv"));
        fileDataK = new MockMultipartFile("fileData", classLoader.getResourceAsStream("common/validator/constraints/notempty.csv"));
        fileDataM = new MockMultipartFile("fileData", classLoader.getResourceAsStream("common/validator/constraints/notempty.csv"));

        MaxFileSizeTest bean = new MaxFileSizeTest();
        bean.setFileData(fileData);
        bean.setFileDataK(fileDataK);
        bean.setFileDataM(fileDataM);

        assertEquals(1, validator.validate(bean).size());
    }

    public void setFileData(MultipartFile fileData) {
        this.fileData = fileData;
    }

    public void setFileDataK(MultipartFile fileDataK) {
        this.fileDataK = fileDataK;
    }

    public void setFileDataM(MultipartFile fileDataM) {
        this.fileDataM = fileDataM;
    }
}
