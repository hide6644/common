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

import common.webapp.converter.CsvFileConverter;
import common.webapp.converter.XlsFileConverter;
import common.webapp.converter.XmlFileConverter;
import common.webapp.form.UploadForm;

public class UserUploadControllerTest extends BaseControllerTestCase {

    @Autowired
    private UserUploadController c;

    @Test
    public void testSetupUpload() throws Exception {
        UploadForm uploadForm = c.setupUpload();

        assertEquals(XmlFileConverter.FILE_TYPE, uploadForm.getFileType());
    }

    @Test
    public void testOnSubmitCsv() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/webapp/controller/users.csv");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData", input);

        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(CsvFileConverter.FILE_TYPE);
        uploadForm.setFileData(mockMultipartFile);

        BindingResult errors = new DataBinder(uploadForm).getBindingResult();
        c.onSubmit(uploadForm, errors);

        assertFalse(errors.hasErrors());
        assertEquals(1, uploadForm.getCount());
    }

    @Test
    public void testOnSubmitXls() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/webapp/controller/users.xlsx");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData", input);

        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(XlsFileConverter.FILE_TYPE);
        uploadForm.setFileData(mockMultipartFile);

        BindingResult errors = new DataBinder(uploadForm).getBindingResult();
        c.onSubmit(uploadForm, errors);

        assertFalse(errors.hasErrors());
        assertEquals(1, uploadForm.getCount());
    }

    @Test
    public void testOnSubmitXml() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/webapp/controller/users.xml");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData", input);

        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(XmlFileConverter.FILE_TYPE);
        uploadForm.setFileData(mockMultipartFile);

        BindingResult errors = new DataBinder(uploadForm).getBindingResult();
        c.onSubmit(uploadForm, errors);

        assertFalse(errors.hasErrors());
        assertEquals(1, uploadForm.getCount());
    }

    @Test
    public void testFileException() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("common/webapp/controller/users.xml");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData", input);

        UploadForm uploadForm = new UploadForm();
        uploadForm.setFileType(CsvFileConverter.FILE_TYPE);
        uploadForm.setFileData(mockMultipartFile);

        BindingResult errors = new DataBinder(uploadForm).getBindingResult();
        c.onSubmit(uploadForm, errors);

        assertFalse(errors.hasErrors());
        assertEquals(0, uploadForm.getCount());
        assertNotNull(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getAttribute("error_messages"));
    }
}
