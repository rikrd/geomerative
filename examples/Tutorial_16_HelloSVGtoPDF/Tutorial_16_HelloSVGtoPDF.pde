import processing.pdf.*;
import geomerative.*;

RShape grp;
PGraphics pdf;

void setup(){
  size(400, 400);
  smooth();
  
  RG.init(this);
  grp = RG.loadShape("bot1.svg");
  
  pdf = createGraphics(width, height, PDF, "bot1.pdf");
}

void draw(){
  background(255);
  grp.draw();

  pdf.beginDraw(); 
  pdf.background(255);
  grp.draw(pdf);
  pdf.dispose();
  pdf.endDraw();
}

