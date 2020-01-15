var button = document.getElementById("LoginButton")
button.addEventListener("click", validateLogin);

function validateLogin(){
	
	var email = document.getElementById("uname").value;
	var psw = document.getElementById("psw").value;
	var errorMessage = "";
	
	if (psw.length < 8|| psw.length > 16) {
		errorMessage = "Invalid password length";
	}
		if (email.length < 4 || email.length > 16) {
		errorMessage = "Username not valid";
	}
	
  // Handle Errors here.
 // var errorCode = error.code;
  document.getElementById("LoginErrorText").innerHTML = errorMessage;
}
