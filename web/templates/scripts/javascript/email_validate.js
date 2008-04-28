/* To include this script into an xhtml page without copying and pasting it in 
add the following tags into your xhtml page. Please note that these comments are 
only valid within .js (JavaScript files), 
do not include them if you wish to use this script within an xhtml document.

<script type="text/javascript" src="./validate.js"></script>

or copy and paste the script into your document head enclosed in 
<script type="text/javascript"></script> tags 

add the following in place of your usual <body> tag
<body onload="document.form1.yourname.focus();">


variable names		variable descrition

yourname		first name input
yoursurname		second name input	
addy			email address

*/



function validate(){
			if (document.form1.yourname.value.length<3){
				alert("Please enter your full Forname");
				document.form1.yourname.focus();
				return false;
				}
			if (document.form1.yoursirname.value.length<3){
				alert("Please enter your full Sirname.");
				document.form1.yoursirname.focus();
				return false;
				}
			if (document.form1.addy.value.length < 5){
			    alert("Please enter a complete email address in the form: yourname@yourdomain.com")
    			document.form1.addy.focus();
    			return false;
  				}
var addystring = document.form1.addy.value;
        var ampIndex = addystring.indexOf("@");
        var afterAmp = addystring.substring((ampIndex + 1), addystring.length);
        var dotIndex = afterAmp.indexOf(".");
        dotIndex = dotIndex + ampIndex + 1;
        afterAmp = addystring.substring((ampIndex + 1), dotIndex);
        var afterDot = addystring.substring((dotIndex + 1), addystring.length);
        var beforeAmp = addystring.substring(0,(ampIndex));
        var addy_regex ="^[a-zA-Z0-9_\-\.]+@[a-zA-Z0-9_\-]+\.[a-zA-Z0-9_\-\.]+$";
      	  if ((addystring.indexOf("@") != "-1") &&
       	     	(addystring.length > 5) &&
       	     	(afterAmp.length > 0) &&
				(beforeAmp.length > 1) &&
            	(afterDot.length > 1) &&
            	(addy_regex.test(addystring)) ) {
	          	return true;
				}
			else{
				alert("Invalid! Please enter a complete email address in the form: yourname@yourdomain.com")
    			document.form1.addy.focus();
				return false;
				}
}
