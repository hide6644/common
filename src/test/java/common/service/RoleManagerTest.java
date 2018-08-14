package common.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import common.dto.LabelValue;

public class RoleManagerTest extends BaseManagerTestCase {

    @Autowired
    private RoleManager manager;

    @Test
    public void testGetLabelValues() {
        List<LabelValue> labelValueList = manager.getLabelValues();

        assertNotNull(labelValueList);

        LabelValue newLabelValue = new LabelValue();

        assertFalse(labelValueList.contains(newLabelValue));

        newLabelValue.setValue("ROLE_USER");
        newLabelValue.setLabel("TEST_LABEL");

        Collections.sort(labelValueList);
        assertFalse(labelValueList.get(0).equals(newLabelValue));
        assertTrue(labelValueList.get(1).equals(newLabelValue));

        LabelValue userLabelValue = labelValueList.stream().filter(labelValue -> labelValue.equals(newLabelValue)).findFirst().get();

        assertEquals("ROLE_USER", userLabelValue.getValue());
        assertEquals("Default role for all Users", userLabelValue.getLabel());
    }
}
