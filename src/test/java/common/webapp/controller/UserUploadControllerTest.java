package common.webapp.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import common.dto.UploadForm;
import common.webapp.converter.FileType;

class UserUploadControllerTest extends BaseControllerTestCase {

    @Autowired
    private UserUploadController c;

    @Test
    void testSetupUpload() {
        UploadForm uploadForm = c.setupUpload();

        assertEquals(Integer.valueOf(FileType.XML.getValue()), uploadForm.getFileType());
    }

    @Test
    void testOnSubmitCsv() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/webapp/controller/users.csv");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData", input);

        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(FileType.CSV.getValue());
        uploadForm.setFileData(mockMultipartFile);

        BindingResult errors = new DataBinder(uploadForm).getBindingResult();
        c.onSubmit(uploadForm, errors);

        assertFalse(errors.hasErrors());
        assertEquals(3, uploadForm.getUploadResult().getProcessingCount());
        assertEquals(1, uploadForm.getUploadResult().getSuccessTotalCount());
    }

    @Test
    void testOnSubmitCsvError() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/webapp/controller/usersError.csv");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData", input);

        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(FileType.CSV.getValue());
        uploadForm.setFileData(mockMultipartFile);

        BindingResult errors = new DataBinder(uploadForm).getBindingResult();
        c.onSubmit(uploadForm, errors);

        assertEquals(2, uploadForm.getUploadResult().getUploadErrors().get(0).getRowNo());
        assertTrue(uploadForm.getUploadResult().getUploadErrors().get(0).getFieldName().length() > 0);
        assertTrue(uploadForm.getUploadResult().getUploadErrors().get(0).getMessage().length() > 0);
    }

    @Test
    void testOnSubmitXls() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/webapp/controller/users.xlsx");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData", input);

        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(FileType.EXCEL.getValue());
        uploadForm.setFileData(mockMultipartFile);

        BindingResult errors = new DataBinder(uploadForm).getBindingResult();
        c.onSubmit(uploadForm, errors);

        assertFalse(errors.hasErrors());
        assertEquals(1, uploadForm.getUploadResult().getSuccessTotalCount());
        assertTrue(uploadForm.getUploadResult().getUploadErrors().size() > 0);
    }

    @Test
    void testOnSubmitXml() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/webapp/controller/users.xml");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData", input);

        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(FileType.XML.getValue());
        uploadForm.setFileData(mockMultipartFile);

        BindingResult errors = new DataBinder(uploadForm).getBindingResult();
        c.onSubmit(uploadForm, errors);

        assertFalse(errors.hasErrors());
        assertEquals(1, uploadForm.getUploadResult().getSuccessTotalCount());
        assertTrue(uploadForm.getUploadResult().getUploadErrors().size() > 0);
    }

    @Test
    void testHasErrors() throws Exception {
        UploadForm uploadForm = new UploadForm();

        BindingResult errors = new DataBinder(uploadForm).getBindingResult();
        errors.rejectValue("fileType", "Range.message", "");
        String rtn = c.onSubmit(uploadForm, errors);

        assertEquals("admin/master/uploadUsers", rtn);
    }

    @Test
    void testOnSubmitCsvFileException() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/webapp/controller/users.xml");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData", input);

        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(FileType.CSV.getValue());
        uploadForm.setFileData(mockMultipartFile);

        BindingResult errors = new DataBinder(uploadForm).getBindingResult();
        c.onSubmit(uploadForm, errors);

        assertFalse(errors.hasErrors());
        assertEquals(0, uploadForm.getUploadResult().getSuccessTotalCount());
        assertNotNull(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("error_messages"));
    }

    @Test
    void testOnSubmitXlsFileException() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/webapp/controller/users.csv");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData", input);

        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(FileType.EXCEL.getValue());
        uploadForm.setFileData(mockMultipartFile);

        BindingResult errors = new DataBinder(uploadForm).getBindingResult();
        c.onSubmit(uploadForm, errors);

        assertFalse(errors.hasErrors());
        assertEquals(0, uploadForm.getUploadResult().getSuccessTotalCount());
        assertNotNull(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("error_messages"));
    }

    @Test
    void testOnSubmitXmlFileException() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/webapp/controller/users.xlsx");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData", input);

        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(FileType.XML.getValue());
        uploadForm.setFileData(mockMultipartFile);

        BindingResult errors = new DataBinder(uploadForm).getBindingResult();
        c.onSubmit(uploadForm, errors);

        assertFalse(errors.hasErrors());
        assertEquals(0, uploadForm.getUploadResult().getSuccessTotalCount());
        assertNotNull(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("error_messages"));
    }
}
