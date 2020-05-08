$(document).ready(function () {
    console.log("ready!");

    // find elements on the page
    var banner = $("#banner-message");
    var button_create_group = $("#submit_button_create_group");
    var button_join_group = $("#submit_button_join_group");
    var numOfPlayersBox = $("#num_of_players");
    var numOfSessionsBox = $("#num_of_sessions");
    var gameCodeBox = $("#code_group");
    var joinGroupSucces = $("#join_group_succes");
    var newGroupSucces = $("#new_group_succes");
    var userId;
    // var newGroupHead = $("#new_group_head");
    // var groupOptions = $("#group_options");
    // var joinGroupHead = $("#join_group_head");
    // var enterCodeGroup = $("#enter_code_group");

    // newGroupHead.on("click", function(){
    //     groupOptions.show();
    //     enterCodeGroup.hide();
    // });

    // joinGroupHead.on("click", function(){
    //     groupOptions.hide();
    //     enterCodeGroup.show();

    // });

    document.onload = checkCookie();

    // handle create group click
    button_create_group.on("click", function () {
        banner.addClass("alt");
        console.log("clicked");

        // send request to the server
        $.ajax({
            method: "POST",
            contentType: "application/json",
            data: createRequestCreateGame(),
            url: "create_game",
            dataType: "json",
            success: onHttpResponseNewGame
        });
    });

    // handle join click
    button_join_group.on("click", function () {
        banner.addClass("alt");
        console.log("clicked");

        // send request to the server
        $.ajax({
            method: "POST",
            contentType: "application/json",
            data: createRequestJoinGroup(),
            url: "join_game",
            dataType: "json",
            success: onHttpResponseJoinGroup
        });
    });

    function createRequestCreateGame() {
        var url = window.location.href;
        var numOfPlayers = parseFloat(numOfPlayersBox.val(), 10);
        if (isNaN(numOfPlayers)) {
            numOfPlayers = 4;
        }

        var numOfSessions = parseInt(numOfSessionsBox.val());

        if (isNaN(numOfSessions)) {
            numOfSessions = 2;
        }

        // Search request to the server
        var frontEndRequest = {

            user_id: userId,
            num_of_players: numOfPlayers,
            num_of_sessions: numOfSessions,
            url: url
        };

        console.log(frontEndRequest);

        return JSON.stringify(frontEndRequest);
    }

    function createRequestJoinGroup() {

        var gameCode = parseFloat(gameCodeBox.val(), 10);

        // Search request to the server
        var frontEndRequest = {

            user_id: userId,
            game_code: gameCode
        };

        return JSON.stringify(frontEndRequest);
    }

    function onHttpResponseNewGame(data, status) {
        console.log("got response");
        if (status === "success") {
            console.log(data);
            newGroupSucces = document.getElementById("new_group_succes");
            newGroupSucces.innerHTML = data + " קוד קבוצה";
            window.location.replace("./join_game");
        } else {
            alert("Error connecting to the server " + status);
        }
    }

    function onHttpResponseJoinGroup(data, status) {
        console.log("got response");
        if (status === "success") {
            console.log(data);
            joinGroupSucces.show();
        } else {
            alert("Error connecting to the server " + status);
        }
    }

    function setCookie(cname, cvalue, exdays) {
        var d = new Date();
        d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
        var expires = "expires=" + d.toGMTString();
        document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
    }

    function getCookie(cname) {
        var name = cname + "=";
        var decodedCookie = decodeURIComponent(document.cookie);
        var ca = decodedCookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length, c.length);
            }
        }
        return "";
    }

    function checkCookie() {
        
        userId = getCookie("userId");
        if (userId == "") {
            userId = Math.floor(Math.random() * Math.pow(10,18));
            setCookie("userId", userId, 30);
        }
        console.log("userId = " + userId);
    }


});
