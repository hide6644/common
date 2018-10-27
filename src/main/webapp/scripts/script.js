$(function() {
    $('#switch_locale_url').prop('href', location.href + (location.search ? '&' : '?') + 'locale=en');

    $('#toggle_checkAll').click(function() {
        $('input[name$=Ids]:not(:disabled)').prop('checked', this.checked);
    });

    $('input[name$=Ids]:not(:disabled)').click(function() {
        if(this.checked) {
            if ($('input[name$=Ids]:not(:disabled):not(:checked)').length) {
                $('#toggle_checkAll').prop('checked', false);
            } else {
                $('#toggle_checkAll').prop('checked', true);
            }
        } else {
            $('#toggle_checkAll').prop('checked', false);
        }
    });
});
