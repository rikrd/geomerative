/* To include this script into an xhtml page without copying and pasting it in 
add the following tags into your xhtml page. Please note that these comments are 
only valid within .js (JavaScript files), 
do not include them if you wish to use this script within an xhtml document.

<script type="text/javascript" src="./preload.js"></script>

or copy and paste the script into your document head enclosed in 
<script type="text/javascript"></script> tags */

var arImages=new Array();
function Preload() {
 var temp = Preload.arguments; 
 for(x=0; x < temp.length; x++) {
  arImages[x]=new Image();
  arImages[x].src=Preload.arguments[x];
 }
}

 /*this replaces your normal 'body' tag
 substitute your own image names*/
 /*
 < body onload="Preload('thing.png','anotherthing.png','etc etc.png')" >
 */