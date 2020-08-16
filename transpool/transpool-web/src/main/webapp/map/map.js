var mapInitServlet = "/transpool/map/mapInit";
var mapName = sessionStorage.getItem("mapName");
var tripOffersServlet = "/transpool/map/tripOffers";
var tripRequestsServlet = "/transpool/map/tripRequests";
var myTripsServlet = "/transpool/map/userTrips";
var matchesServlet = "/transpool/map/matches";
var setMatchServlet = "/transpool/map/setMatch";
var mapNamesServlet = "/transpool/map/getMapNames";
var alertServlet = "/transpool/map/alert";
var matchesTripsServlet = "/transpool/map/getMatchRequest";
var addFeedbackServlet = "/transpool/map/addFeedback";
var showFeedbacksServlet = "/transpool/map/showFeedbacks";
var offerRow = 0;
var isOffer;
var allRequests;
var allOffers;
var choise = "";
var matchMap = "";
var matchArray = [];
$(function () {
    $("#map-name-title").text(mapName);
    mapInit();
    $("#add-trip").click(function () {
        if (isOffer === true) {
            $(location).attr('href', '../Trip/offerTrip.html');
        } else $(location).attr('href', '../Trip/requestTrip.html');


    });


    $("#Rank").click(function () {
        ajaxMatches();
        $("#modall").modal('toggle');
    });

    $("#showfeedbacks").click(function () {
        ajakShowFeedbacks();
        $("#modall").modal('toggle');
    });

    $("#back").click(function () {
        $(location).attr('href', '../user/user.html');
    });

    $("#match").click(function () {
        getUnMatchedAjax();
        $("#modall").modal('toggle');
    });

    $('body').on('click', '#modall li', function () {
        $("#list").children().removeClass("active");
        $(this).addClass("active");
        choise = $(this);

    });


    getRequestsAjax();
    getOffersAjax();
    sendAjaxAlert();


    $('#request-table').on('dblclick', 'tr', function () {
        var id = $(this).attr('name');
        $.each(allRequests, function (index, request) {
            if (id == request.id) {
                $("#modal-title").text(request.name);
                $("#modal-body").empty();

                if (request.isMatch == false)
                    $("#modal-body").append("this request has not been matched");
                else {
                    $("#modal-body").append("Match details:" + "<br>" + request.match.roadStory);

                }
            }

        });
        $("#buttons").empty();
        $("#buttons").append("<button type='button' class='btn btn-secondary' id='back'>Back</button>");
        setBackEvent();
        $("#modall").modal('toggle');
    });


    $('#offer-table').on('dblclick', 'tr', function () {
        var id = $(this).attr('name');
        $.each(allOffers, function (index, offer) {
            if (id == offer.id) {

                $("#modal-title").text(offer.name);

                var message = "passengers attached <br>";
                if (offer.attached == false)
                    message = "no passengers attached";
                else {
                    $.each(offer.attachedPassengers, function (index, passenger) {
                        var details = passenger.date + " , name:" + passenger.name + " , going up at " + passenger.up +
                            " and going down at " + passenger.down + "<br>";
                        message = message.concat(details);
                    })
                }
                $("#modal-body").empty();
                $("#modal-body").append("route:" + offer.route.join(" , ") + "<br>" + message);
            }

        });
        $("#buttons").empty();
        $("#buttons").append("<button type='button' class='btn btn-secondary' id='back'>Back</button>");
        setBackEvent();
        $("#modall").modal('toggle');
    });


})
;

function ajakShowFeedbacks() {
    $.ajax({
        url: showFeedbacksServlet,
        timeout: 2500,
        success: function (data) {
            createModalFeedbacks(data);
            $("#modall").modal('toggle');

        },
        error: function (error) {
            alert("fail to get response from server");
        }
    });


}

function createModalFeedbacks(data) {
    $("#modal-title").text("FeedBacks");
    var body = $("#modal-body").empty();
    $("#buttons").empty();
    $("#buttons").append("<button type='button' class='btn btn-secondary' id='back'>Back</button>");
    setBackEvent();

    if(data == false) {
        body.text("You dont have feedBacks");
        return;
    }

    var list = body.append("<ul></ul>").children().last();
    list.attr("id", "list");
    list.addClass('list-group');
    $.each(data, function (index, feedback) {
        var item = list.append("<li></li>").children().last();
        item.addClass("list-group-item");
        item.text(feedback.user + " : " + feedback.feedback);

    });
}

function sendAjaxAlert() {
    $.ajax({
        url: alertServlet,
        timeout: 2500,
        success: function (data) {
            if (data != false) {
                if (data.type == "match")
                    createModalAlert(data.list);
                else createModalalertRank(data.list);
                $("#modall").modal('toggle');
            }
            triggerAlertAjax();
        },
        error: function (error) {
            alert("fail to get response from server alert");
            triggerAlertAjax();

        }
    });

}

function createModalalertRank(data) {
    $("#buttons").empty();
    $("#buttons").append("<button type='button' class='btn btn-secondary' id='back'>Back</button>");
    setBackEvent();
    $("#modal-title").text("New Rank");
    var body = $("#modal-body").empty();
    $.each(data || [], function (index, item) {
        if (index == 0)
            body.append(item + " has give you: ");
        if (index == 1)
            body.append(item + " stars!\n");
        if (index == 2)
            body.append("feedback: " + item);


    });

}

function sendAjaxMatches(mapName, id, limit) {
    $.ajax({
        data: "mapName=" + mapName + "&requestId=" + id + "&limit=" + limit,
        dataType: 'json',
        url: matchesServlet,
        timeout: 2500,
        success: function (data) {
            createModalMatches(data);
        },
        error: function (error) {
            alert("fail to get response from server");
        }
    });
}

function createModalAlert(data) {
    $("#buttons").empty();
    $("#buttons").append("<button type='button' class='btn btn-secondary' id='back'>Back</button>");
    setBackEvent();
    $("#modal-title").text("New Match");
    var body = $("#modal-body").empty();
    $.each(data || [], function (index, item) {
        if (index == 0)
            body.append("Map name: " + item);
        if (index == 1)
            body.append(" , trip Id: " + item);
        if (index == 2)
            body.append(" , price: " + item);


    });

}

function createModalMatches(data) {
    matchMap = choise.attr("name");
    choise = "";
    $("#modal-title").text("Set Match");
    var body = $("#modal-body").empty();
    $("#buttons").empty();
    $("#buttons").append("<button type='button' class='btn btn-secondary' id='back'>Back</button>");
    setBackEvent();
    if (data == false) {
        body.text("no matches found");
        return;
    }
    $("#buttons").append("<button type='button' class='btn btn-primary' id='setMatch'>set Match</button>");

    var list = body.append("<ul></ul>").children().last();
    list.attr("id", "list");
    list.addClass('list-group');
    $.each(data, function (index, match) {
        var item = list.append("<li></li>").children().last();
        item.addClass("list-group-item");
        item.attr("id", match.matchId);
        item.text(match.roadStory);

    });

    body.append("<div id='alert-choise' style='width: 400px; margin-top: 15px; position: center' role='alert'></div>");


    $("#setMatch").click(function () {

        if (choise == "") {
            $("#alert-choise").addClass("alert-danger");
            $("#alert-choise").text("you must pick a match");
            return false;
        }

        setMatchAjax(matchMap, choise.attr("id"));

    });
}

function setMatchAjax(mapName, id) {
    $.ajax({
        data: "mapName=" + mapName + "&id=" + id,
        dataType: 'json',
        url: setMatchServlet,
        timeout: 2500,
        success: function (data) {
            $("#modall").modal('hide');
            alert("the match was set successfully!");
            resetModal();

        },
        error: function (error) {
            alert("fail to get response from server");
        }
    });


}


function mapInit() {
    $.ajax({
        data: "mapName=" + mapName,
        dataType: 'json',
        url: mapInitServlet,
        timeout: 4000,
        success: function (data) {
            createMap(data.myMap, data.length, data.width);
            $('.arg-Graph_item').css("font-size", "10px");
            $('.arg-Graph').ArgGraph();
            $('.arg-Graph_item').off();

            isOffer = data.offer;
            if (isOffer === true) {
                $('#Rank').remove();
                $('#match').remove();
            } else {
                $('#showfeedbacks').remove();

            }

        },
        error: function (error) {
            alert("failed to get response from server");
        }
    });

}

function createMap(map, length, width) {

    $.each(map || [], function (index, stop) {
        var node = $("#graph").append("<div class='arg-Graph_item'></div>").children().last();
        var neighbors = stop.paths;
        var top = (stop.x / length) * 680;
        var left = (stop.y / width) * 1100;
        var style = "left: " + left + "px" + ';' + "top: " + top + "px" + ";";
        node.attr("id", stop.stopName.replace(/\s/g, ''));
        node.attr("data-neighbors", neighbors.join(",").replace(/\s/g, ''));
        node.attr("style", style);
        node.text(stop.stopName);

    })

}

function getRequestsAjax() {
    $.ajax({
        data: "mapName=" + mapName,
        dataType: 'json',
        url: tripRequestsServlet,
        timeout: 2500,
        success: function (data) {
            updateTableRequests(data);
            triggerRequestTable();

        },
        error: function (error) {
            triggerRequestTable();
        }
    });
}

function getOffersAjax() {
    $.ajax({
        data: "mapName=" + mapName,
        dataType: 'json',
        url: tripOffersServlet,
        timeout: 2500,
        success: function (data) {
            updateTableOffers(data);
            triggerOffersTable();

        },
        error: function (error) {
            triggerOffersTable();
        }
    });
}


function updateTableRequests(requests) {
    allRequests = requests;
    var requestBody = $("#request-table").empty();
    $.each(requests || [], function (index, request) {
        var requestRow = requestBody.append("<tr></tr>").children().last();
        requestRow.addClass("table-row");
        requestRow.attr("name", request.id);
        requestRow.append("<td>" + request.id + "</td><td>" + request.name + '</td>' +
        "<td>" + request.from + "</td>" + "<td>" + request.to + "</td>" + "<td>" + request.time.whichTime + "</td>" +
        "<td>" + request.isMatch + "</td>" +
        "<td>" + timeAsString(request.time.checkoutTime.day, request.time.checkoutTime.minutes, request.time.checkoutTime.hours) + "</td>");

    })
}

function updateTableOffers(offers) {
    allOffers = offers;
    var offerBody = $("#offer-table").empty();
    $.each(offers || [], function (index, offer) {
        var offerRow = offerBody.append("<tr></tr>").children().last();
        offerRow.addClass("table-row");
        offerRow.attr("name", offer.id);
        offerRow.append("<td>" + offer.id + "</td><td>" + offer.name + '</td>' +
        "<td>" + offer.from + "</td>" + "<td>" + offer.to + "</td>" + "<td>" + timeAsString(offer.checkoutTime.day, offer.checkoutTime.minutes, offer.checkoutTime.hours) + "</td>" +
        "<td>" + timeAsString(offer.arrivalTime.day, offer.arrivalTime.minutes, offer.arrivalTime.hours) + "</td>" +
        "<td>" + offer.initCapacity + "</td>" + "<td>" + offer.fuelCon + "</td>" + "<td>" + offer.ppk + "</td>");

    })
}


function triggerRequestTable() {
    setTimeout(getRequestsAjax, 2000);


}

function triggerOffersTable() {
    setTimeout(getOffersAjax, 2000);


}

function triggerAlertAjax() {
    setTimeout(sendAjaxAlert, 2000);
}

function timeAsString(day, minutes, hours) {
    var hourS = hours, minuteS = minutes;

    if (hours < 10)
        hourS = "0" + hours;

    if (minutes < 10)
        minuteS = "0" + minutes;

    return "Day:" + day + " , " + hourS + ":" + minuteS;

}

function getUnMatchedAjax() {
    $.ajax({
        url: myTripsServlet,
        timeout: 2500,
        success: function (data) {
            createModal(data);

        },
        error: function (error) {
            alert("fail to get response from server");
        }
    });


}

function ajaxMatches() {
    $.ajax({
        url: matchesTripsServlet,
        timeout: 2500,
        success: function (data) {
            createModalMatchesRequests(data);

        },
        error: function (error) {
            alert("fail to get response from server");
        }
    });


}

function createModalMatchesRequests(data) {

    choise = "";
    matchArray = [];
    $("#buttons").empty();
    $("#buttons").append("<button type='button' class='btn btn-secondary' id='back'>Back</button>");
    setBackEvent();
    $("#modal-title").text("Choose matched Request");
    var body = $("#modal-body").empty();
    if (data == false) {
        body.text("you dont have any match requests");
        return;
    }

    $("#buttons").append("<button type='button' class='btn btn-primary' id='rank'>Rank</button>");
    var list = body.append("<ul></ul>").children().last();
    list.attr("id", "list");
    list.addClass('list-group');
    $.each(data, function (index, request) {
        var item = list.append("<li></li>").children().last();
        item.addClass("list-group-item");
        matchArray.push(request.match.offersNames);
        item.attr("name", index);
        item.attr("id", request.id);
        item.text("ID: " + request.id + "   Route: " +
        request.from + " - " + request.to);

    });
    body.append("<div id='alert-limit' style='width: 400px; margin-top: 15px; position: center' role='alert'></div>");

    $("#rank").click(function () {

        if (choise == "") {
            $("#alert-limit").addClass("alert-danger");
            $("#alert-limit").text("must choose request");
            return false;
        }

        showOffers(choise.attr("name"));

    });

}

function showOffers(index) {
    choise = "";
    $("#modal-title").text("Choose offer's name");
    var body = $("#modal-body").empty();
    var list = body.append("<ul></ul>").children().last();
    list.attr("id", "list");
    list.addClass('list-group');
    $.each(matchArray[index] || [], function (index, name) {
        var item = list.append("<li></li>").children().last();
        item.addClass("list-group-item");
        item.attr("name", name);
        item.text("Offer name: " + name);

    });

    body.append("<div id='alert-limit' style='width: 400px; margin-top: 15px; position: center' role='alert'></div>");

    $("#rank").off();
    $("#rank").click(function () {

        if (choise == "") {
            $("#alert-limit").addClass("alert-danger");
            $("#alert-limit").text("must choose offer name");
            return false;
        }

        showRankModal();

    });


}

function showRankModal() {
    $("#modal-title").text("Rank");
    var body = $("#modal-body").empty();
    body.append("<input type='text' id='stars' class='form-control' style='width: 200px; position: center; margin-top: 20px;'  placeholder='stars : 1 - 5' value=''>");
    body.append("<textarea type='text' id='feedback' class='form-control' style='width: 400px; position: center; margin-top: 15px;'  placeholder='Feedback - optional' value=''>");
    body.append("<div id='alert-limit' style='width: 400px; margin-top: 15px; position: center' role='alert'></div>");
    $("#rank").off();
    $("#rank").click(function () {
        var stars = $("#stars").val();
        if (stars === "" || parseInt(stars, 10) != stars || stars > 5 || stars < 1) {
            $("#alert-limit").addClass("alert-danger");
            $("#alert-limit").text("rank must be integer between 1 - 5");
            return false;
        }

        ajaxRank(stars, $("#feedback").val());
    });

}

function ajaxRank(rank, feedback) {
    $.ajax({
        data: "rank=" + rank + "&feedback=" + feedback + "&name=" + choise.attr("name"),
        dataType: 'json',
        url: addFeedbackServlet,
        timeout: 2500,
        success: function (data) {
            if (data == true)
                alert("rank was set successfully");
            else alert("You already rank this user");

            $("#modall").modal('hide');

        },
        error: function (error) {
            $("#modall").modal('hide');
            alert("error at getting response from server");
        }
    });
}


function createModal(data) {
    choise = "";
    $("#buttons").empty();
    $("#buttons").append("<button type='button' class='btn btn-secondary' id='back'>Back</button>");
    setBackEvent();
    $("#modal-title").text("Find Match");
    var body = $("#modal-body").empty();
    if (data == false) {
        body.text("you dont have any requests");
        return;
    }

    $("#buttons").append("<button type='button' class='btn btn-primary' id='find'>Find matches</button>");
    var list = body.append("<ul></ul>").children().last();
    list.attr("id", "list");
    list.addClass('list-group');
    $.each(data, function (index, request) {
        var item = list.append("<li></li>").children().last();
        item.addClass("list-group-item");
        item.attr("id", request.id);
        item.attr("name", request.mapName);
        item.text(timeAsString(request.time.day, request.time.minutes, request.time.hours) +
        " map name: " + request.mapName + " route: " +
        request.from + " - " + request.to);

    });
    body.append("<input type='text' id='limit' class='form-control' style='width: 200px; position: center; margin-top: 50px;'  placeholder='limit matches' value=''>");
    body.append("<div id='alert-limit' style='width: 400px; margin-top: 15px; position: center' role='alert'></div>");

    $("#find").click(function () {
        var limit = $("#limit").val();

        if (limit === "" || parseInt(limit, 10) != limit || limit < 1 || choise == "") {
            $("#alert-limit").addClass("alert-danger");
            $("#alert-limit").text("must choose limit of matches and request");
            return false;
        }

        sendAjaxMatches(choise.attr("name"), choise.attr("id"), limit);

    });

}

function setBackEvent() {

    $("#modall #back").click(function () {
        $("#modall").modal('hide');
        resetModal();

    })

}

function resetModal() {
    $("#modal-body").empty();
    $("#buttons").empty();

}




