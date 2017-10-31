package common.webapp.controller;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import common.webapp.converter.FileType;
import common.webapp.form.UploadForm;

public class UserUploadControllerTest extends BaseControllerTestCase {

    @Autowired
    private UserUploadController c;

    @Test
    public void testSetupUpload() {
        UploadForm uploadForm = c.setupUpload();

        assertEquals(Integer.valueOf(FileType.XML.getValue()), uploadForm.getFileType());
    }

    @Test
    public void testOnSubmitCsv() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/webapp/controller/users.csv");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData", input);

        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(FileType.CSV.getValue());
        uploadForm.setFileData(mockMultipartFile);

        BindingResult errors = new DataBinder(uploadForm).getBindingResult();
        c.onSubmit(uploadForm, errors);

        assertFalse(errors.hasErrors());
        assertEquals(1, uploadForm.getUploadResult().getSuccessTotalCount());
    }

    @Test
    public void testOnSubmitXls() throws Exception {
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
    public void testOnSubmitXml() throws Exception {
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
    public void testFileException() throws Exception {
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
}
