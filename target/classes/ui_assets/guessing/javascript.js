$(document).ready(function () {
    console.log("ready!");

    // find elements on the page
    var banner = $("#banner-message");
    var userId;
    var MAX = 9;
    var sentences = new Array(MAX);
    var j = 0;
    var max = MAX;
    var painter = !$("#painter").is(":empty");

    console.log("painter: " + painter);

    document.onload = checkCookie();


    function sendRequestCheck() {
        // send request to the server
        $.ajax({
            method: "POST",
            contentType: "application/json",
            url: "data/waiting_room",
            dataType: "json",
            data: JSON.stringify({ user_id: userId }),
            success: onHttpResponseContinueNextPage,
            complete: function () {
                // Schedule the next
                setTimeout(sendRequestCheck, interval);
            }
        });
    }

    function onHttpResponseContinueNextPage(data, status) {
        console.log("got response");
        if (status === "success") {
            console.log(data);
            if (data.is_continue === true) {
                window.location.replace("." + data.url_redirection);
                // "http://localhost:9000/personal_info"
            }
        } else {
            alert("Error connecting to the server " + status);
        }
    }



    for (i = 0; i < MAX; i++) {
        sentences[i] = document.getElementById("sentence_" + i);
        console.log("sentence " + i + ": " + sentences[i].innerText);
    }


    if (painter) {
        var interval = 3000;
        for (i = 0; i < MAX; i++) {
            sentences[i].style.cursor="default";
        }
        sendRequestCheck();
    }

    for (i = 0; i < MAX; i++) {
        // console.log(results[i].val());
        console.log(i);
        if (sentences[i].innerText.length == 0) {
            max = i;
            console.log("max: " + max);
            break;
        }
    }

    var buttons = new Array(max);
    for (i = 0; i < max; i++) {
        buttons[i] = $("#sentence_"+i);
        console.log(buttons[i]);
        if (painter === false) {
            buttons[i].on("click", function(){click(this)});
            
        }
    }

    

    function click(ele) {
        banner.addClass("alt");
        console.log("clicked");
        
        console.log(ele.innerText);
        // send request to the server
        $.ajax({
            method: "POST",
            contentType: "application/json",
            data: createRequest(ele.innerText),
            url: "data/guessing",
            dataType: "json",
            success: onHttpResponse
        });
    }

    function createRequest(innerText) {


        // Search request to the server
        var frontEndRequest = {

            user_id: userId,
            guess_sentence: innerText
        };

        console.log(frontEndRequest);

        return JSON.stringify(frontEndRequest);
    }


    function onHttpResponse(data, status) {
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
