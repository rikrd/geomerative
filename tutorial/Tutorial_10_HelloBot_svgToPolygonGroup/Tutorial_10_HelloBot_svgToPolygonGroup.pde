import processing.opengl.*;
import geomerative.*;

RSVG svgLoader;
RGroup grp;

RGroup polyGrp;

void setup(){
  size(800, 600, OPENGL);
  smooth();
  g.smooth = true;

  // VERY IMPORTANT: Allways initialize the library before using it
  RGeomerative.init(this);

  svgLoader = new RSVG();
  grp = svgLoader.toGroup("pirata.svg");
  grp.centerIn(g);
} 

void draw(){
  background(255);

  // We decided the separation between the polygon points dependent of the mouseX
  float pointSeparation = map(constrain(mouseX, 200, width-200), 200, width-200, 5, 300);
  
  // We create the polygonized version
  RCommand.setSegmentator(RCommand.UNIFORMLENGTH);
  RCommand.setSegmentLength(pointSeparation);
  polyGrp = grp.toPolygonGroup();
  
  // We move ourselves to the mouse position
  translate(mouseX, mouseY);
  
  // We draw the polygonized group with the SVG styles
  RGeomerative.ignoreStyles(false);  
  polyGrp.draw();

  // We draw the outlines with our own style  
  RGeomerative.ignoreStyles();
  noFill();
  stroke(0, 100);
  grp.draw();
}
