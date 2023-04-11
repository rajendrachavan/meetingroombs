
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
  } catch (error) {
    console.error(error); // log any errors to the console
  }
}

//Fetching Holidays from External API
const apiGetHolidays = "https://holidays.abstractapi.com/v1/?api_key=aca3700e1738496494153157a635d1ec&country=IE&year=2023&month=04&day=10";

async function getHolidays() {
    console.log("Inside GetHolidays Method!");

    try {
      console.log("Calling API...")
        var response = await fetch(apiGetHolidays, { // send a POST request to the API
          method: 'GET'
        });
        var jsonData = await response.json(); // parse the response as JSON
        console.log(jsonData); // log the JSON data to the console
      } catch (error) {
        console.error(error); // log any errors to the console
      }
}
