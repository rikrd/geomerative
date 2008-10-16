import processing.opengl.*;

import geomerative.*;

RSVG svgLoader;

size(400, 400);

smooth();
g.smooth = true;

// VERY IMPORTANT: Allways initialize the library before using it
RG.init(this);

svgLoader = new RSVG();

beginRecord(PDF, "bot1.pdf"); 
background(255);
svgLoader.draw("bot1.svg");
endRecord();
