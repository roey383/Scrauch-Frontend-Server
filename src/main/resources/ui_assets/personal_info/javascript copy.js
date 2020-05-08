$(document).ready(function () {
    console.log("ready!");

    // find elements on the page
    var banner = $("#banner-message");
    var button_submit = $("#submit_button");
    var colorIdBlue = $("#blue");
    var colorIdYellow = $("#yellow");
    var colorIdRed = $("#red");
    var userId;
    var name_player_var = $("#name_player");

    document.onload = checkCookie();
    // document.onload = init();
    // colorIdBlue.on("click", colorBlue);
    // colorIdYellow.on("click", colorYellow);
    // colorIdRed.on("click", colorRed);

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
            url: "data/personal_info",
            dataType: "json",
            success: onHttpResponse
        });
    });

    function createRequest() {
        // var dataURL = canvas.toDataURL("image/png");
        var dataURL = sketchpad.toImage();
        image_data = dataURL.replace(/^data:image\/(png|jpg);base64,/, "");
        var player_name = name_player_var.val();
        console.log(name_player_var);
        console.log(player_name);
        if (player_name === null) {
            player_name = "roey";
        }

        console.log(player_name);

        // Search request to the server
        var frontEndRequest = {

            user_id: userId,
            profil: image_data,
            name: player_name
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

    // var canvas, ctx, flag = false,
    //     prevX = 0,
    //     currX = 0,
    //     prevY = 0,
    //     currY = 0,
    //     dot_flag = false;

    // var x = "black",
    //     y = 2;

    // function init() {
    //     canvas = document.getElementById('can');
    //     ctx = canvas.getContext("2d");
    //     w = canvas.width;
    //     h = canvas.height;

    //     canvas.addEventListener("mousemove", function (e) {
    //         findxy('move', e)
    //     }, false);
    //     canvas.addEventListener("mousedown", function (e) {
    //         findxy('down', e)
    //     }, false);
    //     canvas.addEventListener("mouseup", function (e) {
    //         findxy('up', e)
    //     }, false);
    //     canvas.addEventListener("mouseout", function (e) {
    //         findxy('out', e)
    //     }, false);
    // }


    // function findxy(res, e) {
    //     if (res == 'down') {
    //         prevX = currX;
    //         prevY = currY;
    //         currX = e.clientX - canvas.offsetLeft;
    //         currY = e.clientY - canvas.offsetTop;

    //         flag = true;
    //         dot_flag = true;
    //         if (dot_flag) {
    //             ctx.beginPath();
    //             ctx.fillStyle = x;
    //             ctx.fillRect(currX, currY, 2, 2);
    //             ctx.closePath();
    //             dot_flag = false;
    //         }
    //     }
    //     if (res == 'up' || res == "out") {
    //         flag = false;
    //     }
    //     if (res == 'move') {
    //         if (flag) {
    //             prevX = currX;
    //             prevY = currY;
    //             currX = e.clientX - canvas.offsetLeft;
    //             currY = e.clientY - canvas.offsetTop;
    //             draw();
    //         }
    //     }
    // }

    // function draw() {
    //     ctx.beginPath();
    //     ctx.moveTo(prevX, prevY);
    //     ctx.lineTo(currX, currY);
    //     ctx.strokeStyle = x;
    //     ctx.lineWidth = y;
    //     ctx.stroke();
    //     ctx.closePath();
    // }

    // function colorBlue() {
    //     x = "blue";

    // }


    // function colorYellow() {
    //     x = "yellow";

    // }


    // function colorRed() {
    //     x = "red";

    // }




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
