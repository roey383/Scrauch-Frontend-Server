$(document).ready(function () {
    console.log("ready!");

    // find elements on the page
    var banner = $("#banner-message");
    var button_submit = $("#submit_button");
    var colorIdBlue = $("#blue");
    var colorIdYellow = $("#yellow");
    var colorIdRed = $("#red");
    var userId;
    var trueSentence = document.getElementById("true_sentence");

    document.onload = checkCookie();
    
    colorIdBlue.on("click", colorBlue);
    colorIdYellow.on("click", colorYellow);
    colorIdRed.on("click", colorRed);

    const canvas = document.querySelector('#sketchpad');
    const sketchpad = new Atrament(canvas);

    // handle create group click
    button_submit.on("click", function () {
        banner.addClass("alt");
        console.log("clicked");
        // send request to the server
        $.ajax({
            method: "POST",
            contentType: "application/json",
            data: createRequest(),
            url: "data/drawing",
            dataType: "json",
            success: onHttpResponse
        });
    });

        function createRequest() {
        // var dataURL = canvas.toDataURL("image/png");
        var dataURL = sketchpad.toImage();
        image_data = dataURL.replace(/^data:image\/(png|jpg);base64,/, "");
        var sentence = trueSentence.innerText;
        console.log(sentence);


        // Search request to the server
        var frontEndRequest = {

            user_id: userId,
            drawing: image_data,
            true_sentence: sentence
        };

        console.log(frontEndRequest);

        return JSON.stringify(frontEndRequest);
    }

    function onHttpResponse(data, status) {
        console.log("got response");
        if (status === "success") {
            console.log(data);
            window.location.replace("http://localhost:9000/waiting_room");
        } else {
            alert("Error connecting to the server " + status);
        }
    }
    
    function colorBlue() {
        sketchpad.color = "blue";
    }

    function colorYellow() {
        sketchpad.color = "yellow";
    }

    function colorRed() {
        sketchpad.color = "red";
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
