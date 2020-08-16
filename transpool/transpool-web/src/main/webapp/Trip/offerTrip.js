var mapNamesServlet = "/transpool/Trip/getMapNames";
var addTripOfferServlet = "/transpool/Trip/addTripOffer";
var route = "";
var maps;

$(function () {
    $.ajax({
        url: mapNamesServlet,
        timeout: 2000,
        success: function (data) {
            maps = data.maps;
            $.each(maps, function(key, value){
                var option = $("#map").append("<option></option>").children().last();
                option.attr("id", key.replace(/\s/g, ''));
                option.text(key);
            });
        },
        error: function (error) {
            alert("failed to get response from server");
        }
    });

    $("#map").on('change', function() {

        initStops($("#map option:selected").text());
    });

    defineRoute();


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

function sendAjax() {
    $.ajax({
        method: 'POST',
        data: createFormData(),
        url: addTripOfferServlet,
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        timeout: 4000,
        error: function (e) {
            alert.text("error connecting to server");
        },
        success: function (data) {
            if (data == "false")
                setError("invalid Route",$("#alert-route"));
            else {
                alert("Trip offer added successfully");
                $(location).attr('href', '../map/map.html');
            }
        }
    });


}

function createFormData() {
    var formData = new FormData();
    formData.append("mapName", $("#map option:selected").text());
    formData.append("recurrences", $("#recurrences option:selected").text());
    formData.append("route", route);
    formData.append("dayStart", $("#daystart").val());
    formData.append("hour", $("#hour").val());
    formData.append("minutes", $("#minutes").val());
    formData.append("ppk", $("#ppk").val());
    formData.append("capacity", $("#capacity").val());
    return formData;
}

function defineRoute() {
    $("#add-stop-route").click(function () {
        if ($("#stop option:selected").text() === "Station") {
            setError("please choose station", $("#alert-route"));
        } else {
            cleanError($("#alert-route"));
            var stop = $("#stop option:selected").text();
            if (route === "")
                route = stop;
            else {
                route = route.concat(",");
                route = route.concat(stop);

            }
            $("#route").text(route);
        }
    });

    $("#clear-route").click(function () {
        route = "";
        $("#route").text("");
    });

}



function initStops(map) {
    $("#stop").empty();
    $("#stop").append("<option></option>").children().last().text("Station");

    if(map == "Choose map")
        return;

    $.each(maps, function(key, value){
        if(key == map){
            $.each(value,function (index,name) {
                var option = $("#stop").append("<option></option>").children().last();
                option.attr("id", name.replace(/\s/g, ''));
                option.text(name);
            });

        }
    });
}



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

    var ppk = $("#ppk").val();
    if (ppk === "" || parseInt(ppk, 10) != ppk || ppk < 1) {
        setError("ppk must be positive", $("#alert-ppk"));
        success = false;
    } else cleanError($("#alert-ppk"));

    var capacity = $("#capacity").val();
    if (capacity === "" || parseInt(capacity, 10) != capacity || capacity < 0) {
        setError("must be positive", $("#alert-capacity"));
        success = false;
    } else cleanError($("#alert-capacity"));

    if(route == ""){
        setError("invalid route",$("#alert-route"));
        success = false;
    }else cleanError(($("#alert-route")));
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


