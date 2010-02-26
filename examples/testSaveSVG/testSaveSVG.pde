import processing.xml.*;
import processing.opengl.*;
import geomerative.*;

RSVG svg;

size(800, 600);
smooth();

// VERY IMPORTANT: Allways initialize the library before using it
RG.init(this);

svg = new RSVG();
RGroup grp = svg.toGroup("tiger.svg");
String strSvg = svg.groupToString(grp);
String[] lstStr = split("<svg>\n"+strSvg+"</svg>\n", "\n");
saveStrings("test.svg", lstStr);

exit();
