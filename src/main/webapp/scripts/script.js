var checkAll = function(selectAll, ids) {
	var checked = true;

	ids.each(function() {
        if (this.checked == false) {
        	checked = false;
        }
    });

    selectAll.prop('checked', checked);
};

$(function() {
    $('#toggle_checkAll').click(function() {
        $(':checkbox[name$=Ids]:not([disabled="disabled"])').prop('checked', this.checked);
    });

    $(':checkbox[name$=Ids]:not([disabled="disabled"])').click(function() {
        if(this.checked) {
            checkAll($('#toggle_checkAll'), $(':checkbox[name$=Ids]:not([disabled="disabled"])'));
        } else {
            $('#toggle_checkAll').prop('checked', false);
        }
    });
});
