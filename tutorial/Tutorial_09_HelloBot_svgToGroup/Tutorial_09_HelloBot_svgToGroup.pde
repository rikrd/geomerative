import processing.xml.*;
import processing.opengl.*;
import geomerative.*;

RGroup grp;

void setup(){
  size(800, 600);
  smooth();

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);

  grp = RG.loadSVG("tiger.svg");
  grp = RG.centerIn(grp, g);
} 

void draw(){
  background(255);
  translate(mouseX, mouseY);
  grp.draw();
}
