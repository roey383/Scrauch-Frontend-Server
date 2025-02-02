$(document).ready(function () {
    console.log("ready!");

    // find elements on the page
    var banner = $("#banner-message");
    var button_create_group = $("#submit_button_create_group");
    var button_join_group = $("#submit_button_join_group");
    var numOfPlayersBox = $("#num_of_players");
    var numOfSessionsBox = $("#num_of_sessions");
    var gameCodeBox = $("#code_group");
    var userId;

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
            url: "data/create_game",
            dataType: "json",
            success: onHttpResponseNewGame
        });
    });

    // handle join click
    button_join_group.on("click", function () {
        banner.addClass("alt");
        console.log("clicked");
        console.log(gameCodeBox.val());
        var gameCode = gameCodeBox.val();

        if (gameCode === null) {
            return;
        }
        // send request to the server
        $.ajax({
            method: "POST",
            contentType: "application/json",
            data: createRequestJoinGroup(),
            url: "data/join_game",
            dataType: "json",
            success: onHttpResponseJoinGroup
        });
    });

    function createRequestCreateGame() {
        var url = window.location.href;
        // var numOfPlayers = parseFloat(numOfPlayersBox.val(), 10);
        var numOfPlayers = parseInt(numOfPlayersBox.val());
        console.log(numOfPlayers);
        if (isNaN(numOfPlayers)) {
            numOfPlayers = 2;
        }

        var numOfSessions = parseInt(numOfSessionsBox.val());

        if (isNaN(numOfSessions)) {
            numOfSessions = 1;
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
        var gameCode = gameCodeBox.val()
        console.log(gameCode);
        // Search request to the server
        var frontEndRequest = {

            user_id: userId,
            game_code: gameCode
        };

        return JSON.stringify(frontEndRequest);
    }

    function onHttpResponseNewGame(data, status) {
        console.log("got response: " + data);
        if (status === "success") {
            if (data.already_participating === true){
                console.log("already participating");
                alert("מבטל את המשחק שהינך משתתף בו, ויוצר קבוצה חדשה");
                // return;
            }
            console.log(data);
            window.location.replace("." + data.url_redirection);
        } else {
            alert("Error connecting to the server " + status);
        }
    }

    function onHttpResponseJoinGroup(data, status) {
        console.log("got response: " + data);
        if (data === "full"){
            console.log("game is full");
            alert("המשחק מלא");
            return;
        }
        if (data === "no such game"){
            console.log("no such game");
            alert("קוד קבוצה שגוי");
            return;
        }
        if (status === "success") {
            if (data.redirecting_last === true){
                console.log("redirecting last endpoint");
                alert("חוזר להמשך המשחק");
                // return;
            }
            else if (data.already_participating === true){
                console.log("ok no");
                alert("מבטל את המשחק שהינך משתתף בו, ומצטרף לקבוצה חדשה");
                // return;
            }
            
            console.log(data);
            window.location.replace("." + data.url_redirection);
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
