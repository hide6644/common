package common.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import common.model.LabelValue;

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

        LabelValue userLabelValue = labelValueList.stream().filter(labelValue -> labelValue.equals(newLabelValue)).findFirst().get();

        assertEquals("ROLE_USER", userLabelValue.getValue());
        assertEquals("Default role for all Users", userLabelValue.getLabel());
    }
}
