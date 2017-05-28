jQuery(document).ready(function($) {
    $(".btn-delete").click(function(event) {

        // Prevent the form from submitting via the browser.
        event.preventDefault();
        var serviceUri = event.target.href;
        performDelete(serviceUri);
    });
});

function performDelete(serviceUri) {
    $.ajax({
        type : "DELETE",
        contentType : "application/json",
        url : serviceUri,
        data : '',
        dataType : 'json',
        timeout : 100000,
        success : function(data) {
            console.log("SUCCESS: ", data);
            display(data);
        },
        error : function(e) {
            console.log("ERROR: ", e);
            display(e);
        },
        done : function(e) {
            console.log("DONE");
        }
    });
}

function display(data) {
    $('#summary-table').html(data.responseText);
}
