var login = "/transpool/login";
$(function () {

    $.ajax({
        url: login,
        success: function (response) {
            if (response === "true") {
                $(location).attr('href', 'user/user.html');
            }
        }
    });


    $('#offer').click(function () {
        if ($(this).prop("checked") === true) {
            $("#request").prop("checked", false);
        }
    });

    $('#request').click(function () {
        if ($(this).prop("checked") === true) {
            $("#offer").prop("checked", false);
        }
    });

    $('#form').submit(function () {
        var alert = $("#alert");
        if (($('#request').prop("checked") === false && $('#offer').prop("checked") === false) || !$('#name1').val()) {
            if (!alert.hasClass("alert-danger")) {
                alert.addClass("alert-danger");
            }
            alert.text("all fields are required");
            return false;
        }

        $.ajax({
            url: login,
            data: $(this).serialize(),
            success: function (response) {
                if (response === "true") {
                    $(location).attr('href', 'user/user.html');
                } else {
                    if (!alert.hasClass("alert-danger")) {
                        alert.addClass("alert-danger");
                    }
                    alert.text("User already exist");
                }
            }
        });
        return false;
    })

});


