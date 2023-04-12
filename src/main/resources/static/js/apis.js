
async function generatePass() {

    const resp = await fetch("http://meetingroombooking-env-1.eba-p3jrwpav.us-east-1.elasticbeanstalk.com:5000/utility/create/crypt-password");
    const resData = await resp.json();

    if(resData.status === 200){
        console.log(resData);
        let pass = resData.data;
        $("#passwordField").val(pass);
        showPass();
    } else {
        console.log("Unable to fetch data!!");
    }
}

function showPass() {

    // toggle the type attribute
    const type = $("#passwordField").attr("type") === 'password' ? 'text' : 'password';
    $("#passwordField").attr("type", type);
    // toggle the eye slash icon
    $("#togglePassword").toggleClass('fa-eye-slash');
}

//Uploading CSV to External API
const apiCsvToJson = "http://scpcsvtojson-env-1.eba-nxqvedsm.us-east-1.elasticbeanstalk.com/api/convert";

async function csvToJson() {
  console.log("inside csvToJson");
  var fileElement = $("input:file");// get the selected file
  console.log("FileElement: ", fileElement);
  var file = fileElement[0].files[0];
  console.log("file: ", file);
  var formData = new FormData(); // create a new FormData object
  formData.set('csv', file); // append the file to the form data
  console.log(formData);
  try {
  console.log("Calling API...")
    var response = await fetch(apiCsvToJson, { // send a POST request to the API
      method: 'POST',
      headers: {
              "separator" : "comma"
      },
      body: formData,
      redirect: 'follow'
    });
    var jsonData = await response.json(); // parse the response as JSON
    console.log(jsonData); // log the JSON data to the console
    $("#info").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    $("#info").css({"color":"white", "background-color": "green", "width": "25%"});
    if(jsonData.length > 1) {
        $("#info").append("File data uploaded for batch booking!");
    } else if(jsonData.message.length > 0)  {
        $("#info").css({"background-color": "red", "width": "35%"});
        $("#info").append("File data uploading failed due to: " + jsonData.message);
    } else {
        $("#info").css({"background-color": "red", "width": "25%"});
        $("#info").append("File data uploading failed!");
    }

  } catch (error) {
    console.error(error); // log any errors to the console
  }
}

//Fetching Holidays from External API
const apiGetHolidays = "https://holidays.abstractapi.com/v1/?api_key=aca3700e1738496494153157a635d1ec&country=IE&year=2023&month=05&day=1";

async function getHolidays() {
    console.log("Inside GetHolidays Method!");

    try {
      console.log("Calling API...")
        var response = await fetch(apiGetHolidays, { // send a POST request to the API
          method: 'GET'
        });
        var jsonData = await response.json(); // parse the response as JSON
        console.log(jsonData); // log the JSON data to the console
        var info = jsonData[0].name + " on " + jsonData[0].date + " (" + jsonData[0].week_day + ")"
        $("#info").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
        $("#info").css({"color":"white", "background-color": "green", "width": "25%"});
        $("#info").append(info);
      } catch (error) {
        console.error(error); // log any errors to the console
      }
}
