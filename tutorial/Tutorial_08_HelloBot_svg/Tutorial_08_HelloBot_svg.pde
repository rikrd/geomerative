import processing.opengl.*;

import geomerative.*;

RSVG svgLoader;

void setup(){
  size(400, 400, OPENGL);
  
  smooth();
  g.smooth = true;
  
  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);
  
  svgLoader = new RSVG();
} 

void draw(){
  background(255);
  translate(mouseX, mouseY);
  scale(0.5);
  svgLoader.draw("bot1.svg");
}
