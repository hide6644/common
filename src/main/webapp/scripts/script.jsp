<%@ page language="java" contentType="text/javascript" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!--
var checkAll = function(selectAll, ids) {
    var flag = true;
    ids.each(function() {
        if (this.checked == false) {
            flag = false;
        }
    });
    selectAll.attr('checked', flag);
};

$(function() {
    if ($(':checkbox[name$=Ids]').size() == 0) {
        $('#button_edit').attr('disabled', true);
        $('#button_delete').attr('disabled', true);
        $('#button_download').attr('disabled', true);
    }

    $('#button_new').click(function() {
        $(location).attr('href', $('#button_new').val());
    });

    $('#button_edit').click(function() {
        var ids = $(':checked[name$=Ids]');
        if (ids.size() == 0) {
            alert('<fmt:message key="button.choose"><fmt:param><fmt:message key="button.editTarget" /></fmt:param></fmt:message>');
        } else if (ids.size() > 1) {
            alert('<fmt:message key="button.noMoreChoose"><fmt:param><fmt:message key="button.editTarget" /></fmt:param></fmt:message>');
        } else {
            $(location).attr('href', $('#button_edit').val() + ids.attr({ checked: 'checked' }).val());
        }
    });

    $('#button_delete').click(function() {
        if ($(':checked[name$=Ids]').size() == 0) {
            alert('<fmt:message key="button.choose"><fmt:param><fmt:message key="button.deleteTarget" /></fmt:param></fmt:message>');
            return false;
        } else {
            return true;
        }
    });

    $('#toggle_checkAll').click(function() {
        $(':checkbox[name$=Ids]:not([disabled="disabled"])').attr('checked', this.checked);
    });

    $(':checkbox[name$=Ids]:not([disabled="disabled"])').click(function() {
        if($('#toggle_checkAll').attr('checked') == 'checked' && this.checked == false) {
            $('#toggle_checkAll').attr('checked', false);
        }

        if (this.checked == true) {
            checkAll($('#toggle_checkAll'), $(':checkbox[name$=Ids]:not([disabled="disabled"])'));
        }
    });
});
//-->
