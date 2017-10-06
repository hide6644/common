var checkAll = function(selectAll, ids) {
    ids.each(function() {
        if (this.prop('checked') == false) {
            selectAll.prop('checked', false);
        }
    });
    selectAll.prop('checked', true);
};

$(function() {
    $('#toggle_checkAll').click(function() {
        $(':checkbox[name$=Ids]:not([disabled="disabled"])').prop('checked', this.checked);
    });

    $(':checkbox[name$=Ids]:not([disabled="disabled"])').click(function() {
        if($('#toggle_checkAll').prop('checked') && this.prop('checked') == false) {
            $('#toggle_checkAll').prop('checked', false);
        }

        if (this.prop('checked') == true) {
            checkAll($('#toggle_checkAll'), $(':checkbox[name$=Ids]:not([disabled="disabled"])'));
        }
    });
});
