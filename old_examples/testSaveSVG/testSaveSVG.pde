import processing.xml.*;
import processing.opengl.*;
import geomerative.*;

size(800, 600);
smooth();

// VERY IMPORTANT: Allways initialize the library before using it
RG.init(this);

/*
RSVG svg = new RSVG();
RGroup grp = svg.toGroup("tiger.svg");
String strSvg = svg.fromGroup(grp);
String[] lstStr = split(strSvg, "\n");
saveStrings("test.svg", lstStr);
*/

RShape grp = RG.loadShape("Lion.svg");
RG.saveShape("test.svg", grp);

exit();
