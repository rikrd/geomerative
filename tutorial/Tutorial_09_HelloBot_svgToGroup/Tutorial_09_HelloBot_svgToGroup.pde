import processing.xml.*;
import processing.opengl.*;
import geomerative.*;

RSVG svgLoader;
RGroup grp;

void setup(){
  size(800, 600);
  smooth();
  g.smooth = true;

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);

  grp = RG.loadSVG("tiger.svg");
  grp.centerIn(g);
} 

void draw(){
  background(255);
  translate(mouseX, mouseY);
  grp.draw();
}
