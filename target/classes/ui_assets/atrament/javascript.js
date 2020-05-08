$(document).ready(function () {
    console.log("ready!");

    // find elements on the page
    var banner = $("#banner-message");

    console.log("ok");

    const canvas = document.querySelector('#sketchpad');
    const sketchpad = new Atrament(canvas, {
        width: 500,
        height: 500,
        color: 'black',
    });


    var save_button = document.getElementById("save_button");
    save_button.addEventListener("click", saveToWindows);

    function saveToWindows() {
        console.log("clicked");
        //we have to get the dataURL of the image
        const dataURL = sketchpad.toImage();
        //then we can, for instance, open a new window with it
        console.log(dataURL);
    }


});
