import processing.xml.*;
import processing.opengl.*;
import geomerative.*;

RGroup grp;
RGroup newGrp;

void setup(){
  size(800, 600);
  smooth();
  g.smooth = true;

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);

  grp = RG.loadSVG("lion.svg");
  grp.centerIn(g);
} 

void draw(){
  background(255);
  translate(width/2, height/2);
  RCommand.setSegmentator(RCommand.UNIFORMLENGTH);
  RCommand.setSegmentLength(map(constrain(mouseX, 200, width-200), 200, width-200, 5, 300));
  newGrp = grp.toPolygonGroup();
  
  grp.draw();
  RG.ignoreStyles(false);  
  newGrp.draw();
  
  RG.ignoreStyles();
  noFill();
  stroke(0, 100);
  grp.draw();
}
