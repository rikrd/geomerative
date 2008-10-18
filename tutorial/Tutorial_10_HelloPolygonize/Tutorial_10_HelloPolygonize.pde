import processing.xml.*;
import processing.opengl.*;
import geomerative.*;

RGroup grp;
RGroup polyGrp;

void setup(){
  size(600, 600);
  smooth();

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);

  grp = RG.loadSVG("lion.svg");
  grp = RG.centerIn(grp, g, 100);
} 

void draw(){
  background(255);

  // We decided the separation between the polygon points dependent of the mouseX
  float pointSeparation = map(constrain(mouseX, 100, width-100), 100, width-100, 5, 200);
  
  // We create the polygonized version
  RG.setPolygonizer(RG.UNIFORMLENGTH);
  RG.setPolygonizerLength(pointSeparation);
  polyGrp = RG.polygonize(grp);
  
  // We move ourselves to the mouse position
  translate(mouseX, mouseY);
  
  // We draw the polygonized group with the SVG styles
  polyGrp.draw();
}
