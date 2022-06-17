package common.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import common.Constants;
import common.dto.LabelValue;
import common.entity.Role;

class RoleManagerTest extends BaseManagerTestCase {

    @Autowired
    private RoleManager manager;

    @Test
    void testGetRole() {
        Role role = manager.getRole(Constants.USER_ROLE);

        assertNotNull(role);
    }

    @Test
    void testGetLabelValues() {
        List<LabelValue> labelValueList = manager.getLabelValues();

        assertNotNull(labelValueList);

        assertFalse(labelValueList.contains(LabelValue.of("", "")));

        LabelValue newLabelValue = LabelValue.of("TEST_LABEL", "ROLE_USER");

        Collections.sort(labelValueList);
        assertNotEquals(labelValueList.get(0), newLabelValue);
        assertEquals(labelValueList.get(1), newLabelValue);

        LabelValue userLabelValue = labelValueList.stream().filter(labelValue -> labelValue.equals(newLabelValue)).findFirst().get();

        assertEquals("ROLE_USER", userLabelValue.getValue());
        assertEquals("Default role for all Users", userLabelValue.getLabel());

        Set<LabelValue> labelValue = new HashSet<>();
        labelValue.add(userLabelValue);
        labelValue.add(newLabelValue);

        assertTrue(labelValue.contains(newLabelValue));
    }
}
