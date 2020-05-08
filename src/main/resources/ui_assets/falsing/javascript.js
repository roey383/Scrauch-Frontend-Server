$(document).ready(function () {
    console.log("ready!");

    // find elements on the page
    var banner = $("#banner-message");
    var button = $("#submit_button")
    var falseSentence = $("#false_sentence");
    var userId;

    document.onload = checkCookie();
    
    button.on("click", function () {
        banner.addClass("alt");
        console.log("clicked");

        if (falseSentence.val() === null) {
            return;
        }

        // send request to the server
        $.ajax({
            method: "POST",
            contentType: "application/json",
            data: createRequestCreateGame(),
            url: "data/falsing",
            dataType: "json",
            success: onHttpResponseNewGame
        });
    });

    function createRequestCreateGame() {

        var sentence = falseSentence.val();

        console.log(sentence);

        // Search request to the server
        var frontEndRequest = {

            user_id: userId,
            false_sentence: sentence
        };

        console.log(frontEndRequest);

        return JSON.stringify(frontEndRequest);
    }


    function onHttpResponseNewGame(data, status) {
        console.log("got response");
        if (status === "success") {
            console.log(data);
            window.location.replace("./waiting_room");
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
            userId = Math.floor(Math.random() * Math.pow(10, 18));
            setCookie("userId", userId, 30);
        }
        console.log("userId = " + userId);
    }


});
