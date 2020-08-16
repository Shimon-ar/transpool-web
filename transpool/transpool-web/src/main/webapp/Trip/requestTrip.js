var maps;
var mapNamesServlet = "/transpool/Trip/getMapNames";
var addRequestTripServlet = "/transpool/Trip/addRequestTrip";


$(function () {

    $.ajax({
        url: mapNamesServlet,
        timeout: 3000,
        success: function (data) {
            maps = data.maps;
            $.each(maps, function (key, value) {
                var option = $("#map").append("<option></option>").children().last();
                option.attr("id", key.replace(/\s/g, ''));
                option.text(key);
            });
        },
        error: function (error) {
            alert("failed to get response from server");
        }
    });

    $("#map").on('change', function () {

        initStops($("#map option:selected").text());
    });

    $('#arrival').click(function () {
        if ($(this).prop("checked") === true) {
            $("#checkout").prop("checked", false);
        }
    });

    $('#checkout').click(function () {
        if ($(this).prop("checked") === true) {
            $("#arrival").prop("checked", false);
        }
    });


    $("#form").submit(function () {
        if (checkValidation() === false)
            return false;
        else sendAjax();
        return false;
    });

    $("#back").click(function () {
        $(location).attr('href', '../map/map.html');
        return false;

    })


});


function checkValidation() {
    var success = true;
    var dayStart = $("#daystart").val();
    if (dayStart === "" || parseInt(dayStart, 10) != dayStart || dayStart < 1) {
        success = false;
        setError("must be a positive number", $("#alert-day"));
    } else cleanError($("#alert-day"));

    var hour = $("#hour").val();
    if (hour === "" || parseInt(hour, 10) != hour || hour < 0 || hour > 23) {
        setError("must be between 0-23", $("#alert-hours"));
        success = false;
    } else cleanError($("#alert-hours"));

    var minutes = $("#minutes").val();
    if (minutes === "" || parseInt(minutes, 10) != minutes || minutes < 0 || minutes > 59 || minutes % 5 !== 0) {
        setError("must be between 0-59 and divide by 5", $("#alert-minutes"));
        success = false;
    } else cleanError($("#alert-minutes"));

    if (($('#arrival').prop("checked") === false && $('#checkout').prop("checked") === false)) {
        setError("must choose arrival/checkout", $("#alert-arrival_checkout"));
        success = false;
    } else cleanError($("#alert-arrival_checkout"));

    var flexible = $("#flexibale").val();
    if (flexible === "" || parseInt(flexible, 10) != flexible || flexible < 0) {
        success = false;
        setError("must be a non-negetive integer", $("#alert-flexibale"));
    } else cleanError($("#alert-flexibale"));

    if (($("#source option:selected").text() == $("#destination option:selected").text()) || $("#source option:selected").text() == "Source"  || $("#destination option:selected").text() == "Destination"  ) {
        setError( "invalid route",$("#alert-station"));
        success = false;
    } else cleanError($("#alert-station"));

    return success;

}

function setError(text, alert) {
    if (!alert.hasClass("alert-danger")) {
        alert.addClass("alert-danger");
    }
    alert.text(text);

}

function cleanError(alert) {
    alert.removeClass("alert-danger");
    alert.text("");
}


function initStops(map) {
    $("#source").empty();
    $("#source").append("<option></option>").children().last().text("Source");
    $("#destination").empty();
    $("#destination").append("<option></option>").children().last().text("Destination");

    if (map == "Choose map")
        return;

    $.each(maps, function (key, value) {
        if (key == map) {
            $.each(value, function (index, name) {
                var source = $("#source").append("<option></option>").children().last();
                var destination = $("#destination").append("<option></option>").children().last();
                source.text(name);
                destination.text(name);
            });

        }
    });
}

function sendAjax() {
    $.ajax({
        method: 'POST',
        data: createFormData(),
        url: addRequestTripServlet,
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        timeout: 4000,
        error: function (e) {
            alert.text("error connecting to server");
        },
        success: function (data) {
            alert("Trip offer added successfully");
            $(location).attr('href', '../map/map.html');

        }
    });


}

function createFormData() {
    var formData = new FormData();
    formData.append("mapName", $("#map option:selected").text());
    formData.append("source", $("#source option:selected").text());
    formData.append("destination", $("#destination option:selected").text());
    formData.append("dayStart", $("#daystart").val());
    formData.append("hour", $("#hour").val());
    formData.append("minutes", $("#minutes").val() || "");
    formData.append("flexible", $("#flexibale").val());
    formData.append("checkout", $('#checkout').prop("checked"));
    formData.append("flexTrip", $('#flex').prop("checked"));
    return formData;
}

