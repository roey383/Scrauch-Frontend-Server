$(document).ready(function () {
    console.log("ready!");

    // find elements on the page
    var banner = $("#banner-message");
    var userId;
    var results = new Array(9);
    var button = $("#next_button");
    var j = 0;
    var max = 9;

    for (i = 0; i < 9; i++) {
        results[i] = $("#info_col_" + i);
    }


    for (i = 0; i < 9; i++) {
        // console.log(results[i].val());
        console.log(i);
        if(results[i].is(":empty")){
            max = i;
            console.log("max: " + max);
            break;
        }
    }

    // handle create group click
    button.on("click", function () {
        banner.addClass("alt");
        console.log("clicked");
        sendRequestCheck();

    });

    document.onload = checkCookie();

   function sendRequestCheck() {
        // send request to the server
        console.log("request sent");
        $.ajax({
            method: "POST",
            contentType: "application/json",
            url: "data/scores",
            dataType: "json",
            data: JSON.stringify({ user_id: userId }),
            success: onHttpResponse
        });
    }

    function onHttpResponse(data, status) {
        console.log("got response");
        if (status === "success") {
            console.log(data);
            window.location.replace("http://localhost:9000" + data.url_redirection);
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
