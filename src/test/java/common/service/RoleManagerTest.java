package common.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import common.model.LabelValue;

public class RoleManagerTest extends BaseManagerTestCase {

    @Autowired
    private RoleManager mgr;

    @Test
    public void testGetUser() throws Exception {
        List<LabelValue> labelValueList = mgr.getLabelValues();

        assertNotNull(labelValueList);

        LabelValue newLabelValue = new LabelValue();

        assertFalse(labelValueList.contains(newLabelValue));

        newLabelValue.setValue("ROLE_USER");

        assertEquals(1, labelValueList.stream().filter(labelValue -> labelValue.equals(newLabelValue)).count());
    }
}
