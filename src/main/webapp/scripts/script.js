$(function() {
    $('#toggle_checkAll').click(function() {
        $('input[name$=Ids]:not(:disabled)').prop('checked', this.checked);
    });

    $('input[name$=Ids]:not(:disabled)').click(function() {
        if(this.checked) {
            if ($('input[name$=Ids]:not(:disabled):not(:checked)').length == 0) {
                $('#toggle_checkAll').prop('checked', true);
            } else {
                $('#toggle_checkAll').prop('checked', false);
            }
        } else {
            $('#toggle_checkAll').prop('checked', false);
        }
    });
});