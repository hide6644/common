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
