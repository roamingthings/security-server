jQuery(document).ready(function($) {
    $("#expenses-table tr .attach-click").click(function () {
        window.location = $(this).parent().find('td:eq(0)').attr("href");
    });
    $("#expenses-table tr td").addClass("selectable")
})

