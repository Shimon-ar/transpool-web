var getNameServlet = "/transpool/user/getName";
var getMapsServlet = "/transpool/user/getMaps";
var accountServlet = "/transpool/user/account";
var financeServlet = "/transpool/user/finance";
var uploadServlet = "/transpool/user/upload";
var alertServlet = "/transpool/map/alert";
var mapRow = 0;
var financeTableRow = 0;
var refreshRate = 2000;

$(function () {
    getNameAjax();
    getMapsAjax();
    sendAjaxAlert();


    $('#table-body').on('click', 'tr', function () {
        var mapName = $(this).attr('name');
        $("#modal-title").text(mapName);
        $("#map-choose-modal").modal('toggle');
        $("#go").click(function () {
            sessionStorage.setItem("mapName", mapName);
            $("#map-choose-modal").modal('hide');
            $(location).attr('href', '../map/map.html');

        })


    });


    updateAccount();
    $("#form-account").submit(function () {
        var amount = $("#textinput").val();

        $.ajax({
            data: "amount=" + amount,
            dataType: 'json',
            url: accountServlet,
        });
        $("#textinput").val("");
        return false;
    });

    updateFinancialHistory();

    $("#form-upload").submit(function () {

        var alert = $("#alert");
        if (!$("#file").val() || !$("#map-name").val()) {
            if (!alert.hasClass("alert-danger")) {
                alert.addClass("alert-danger");
            }
            alert.text("all fields are required");
            return false;
        }

        var file = this[0].files[0];
        var formData = new FormData();

        formData.append("file", file);
        formData.append("mapName", $("#map-name").val());

        $.ajax({
            method: 'POST',
            data: formData,
            url: uploadServlet,
            processData: false, // Don't process the files
            contentType: false, // Set content type to false as jQuery will tell the server its a query string request
            timeout: 4000,
            error: function (e) {
                if (!alert.hasClass("alert-danger")) {
                    alert.addClass("alert-danger");
                }
                alert.text("error connecting to server");
            },
            success: function (r) {
                if (r !== "true") {
                    if (!alert.hasClass("alert-danger")) {
                        alert.addClass("alert-danger");
                    }
                    alert.text(r);
                } else {
                    alert.removeClass("alert-danger");
                    alert.text("");
                    $("#file").val("");
                    $("#map-name").val("");
                }
            }
        });

        return false;
    })


});


function updateFinancialHistory() {
    $.ajax({
        data: "row=" + financeTableRow,
        dataType: 'json',
        url: financeServlet,
        timeout: 2000,
        success: function (data) {
            if (data.row !== financeTableRow) {
                financeTableRow = data.row;
                updateFinancialTable(data.actions);
            }
            triggerAjaxFinancialHistory();

        },
        error: function (error) {
            triggerAjaxFinancialHistory();
        }
    });


}


function updateFinancialTable(actions) {
    $.each(actions || [], function (index, action) {
        var financeRow = $("#financial-table tr:last").after("<tr></tr>");
        financeRow.addClass("table-row");
        financeRow.after("<td>" + action.time + "</td><td>" + action.action + '</td>' +
        "<td>" + action.amount + "</td>" + "<td>" + action.amountBefore + "</td>" + "<td>" + action.amountAfter
        + "</td>");
    })

}

function getNameAjax() {
    $.ajax({
        url: getNameServlet,
        success: function (response) {
            $("#greet-user").text("Welcome " + response);
        }
    });
}

function getMapsAjax() {
    $.ajax({
        url: getMapsServlet,
        timeout: 2500,
        success: function (data) {
            updateTableMap(data);
            triggerAjaxGetMaps();

        },
        error: function (error) {
            triggerAjaxGetMaps();
        }
    });
}


function updateTableMap(maps) {
    var mapBody = $("#table-body").empty();
    $.each(maps || [], function (index, map) {
        var mapRow = mapBody.append("<tr></tr>").children().last();
        mapRow.addClass("table-row");
        mapRow.attr("name", map.mapName);
        mapRow.append("<td>" + map.mapName + "</td><td>" + map.userUploaded + '</td>' +
        "<td>" + map.totalStations + "</td>" + "<td>" + map.totalRoads + "</td>" + "<td>" + map.totalTripOffers
        + "</td>" + "<td>" + map.totalMatchRequests + "/" + map.totalTripRequests + "</td>");
    })
}

function updateAccount() {
    $.ajax({
        url: accountServlet,
        timeout: 2000,
        success: function (data) {
            $("#balance").text(data);
            triggerAjaxAccount();
        }
        ,
        error: function (error) {
            triggerAjaxGetMaps();

        }
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
    $("#modal-title-sec").text("New Rank");
    var body = $("#modal-body-sec").empty();
    $.each(data || [], function (index, item) {
        if (index == 0)
            body.append(item + " has give you: ");
        if (index == 1)
            body.append(item + " stars!\n");
        if (index == 2)
            body.append("feedback: " + item);


    });

}

function createModalAlert(data) {
    $("#modal-title-sec").text("New Match");
    var body = $("#modal-body-sec").empty();
    $.each(data || [], function (index, item) {
        if (index == 0)
            body.append("Map name: " + item);
        if (index == 1)
            body.append(" , trip Id: " + item);
        if (index == 2)
            body.append(" , price: " + item);


    });

}

function triggerAjaxGetMaps() {
    setTimeout(getMapsAjax, refreshRate);
}

function triggerAjaxAccount() {
    setTimeout(updateAccount, refreshRate);

}

function triggerAjaxFinancialHistory() {
    setTimeout(updateFinancialHistory, refreshRate);

}

function triggerAlertAjax() {
    setTimeout(sendAjaxAlert, 2000);
}




