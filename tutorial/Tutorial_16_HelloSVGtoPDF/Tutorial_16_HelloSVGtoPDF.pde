import processing.pdf.*;
import geomerative.*;

size(400, 400);

smooth();
g.smooth = true;

// VERY IMPORTANT: Allways initialize the library before using it
RG.init(this);

beginRecord(PDF, "bot1.pdf"); 
background(255);
RShape grp = RG.loadShape("bot1.svg");
grp.draw();
endRecord();
