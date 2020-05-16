$(document).ready(function () {
    console.log("ready!");

    // find elements on the page
    var userId;

    document.onload = checkCookie();
    
    var interval = 500;
    sendRequestCheck();

    function sendRequestCheck(){
        // send request to the server
        $.ajax({
            method: "POST",
            contentType: "application/json",
            url: "data/waiting_room",
            dataType: "json",
            data: JSON.stringify({user_id: userId}),
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
            if (data.is_continue === true){
                window.location.replace("." + data.url_redirection);
                // "http://localhost:9000/personal_info"
            }
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
