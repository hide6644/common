package common.validator.constraints;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class MaxFileSizeTest {

    private static Validator validator;

    @MaxFileSize(max = 3, unitSign = FileSizeUnitSign.BYTE)
    private MultipartFile fileData;

    @MaxFileSize(max = 3, unitSign = FileSizeUnitSign.K_BYTE)
    private MultipartFile fileDataK;

    @MaxFileSize(max = 3)
    private MultipartFile fileDataM;

    @BeforeAll
    static void setUpClass() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testEmpty() throws Exception {
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
    void testNotEmpty() throws Exception {
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

    void setFileData(MultipartFile fileData) {
        this.fileData = fileData;
    }

    void setFileDataK(MultipartFile fileDataK) {
        this.fileDataK = fileDataK;
    }

    void setFileDataM(MultipartFile fileDataM) {
        this.fileDataM = fileDataM;
    }
}
