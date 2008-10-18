import processing.xml.*;
import processing.opengl.*;
import geomerative.*;

RGroup grp;
RGroup circle;

boolean ignoringStyles = false;

void setup(){
  size(600, 600);
  smooth();
  g.smooth = true; 

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);
  RG.ignoreStyles(ignoringStyles);
  RG.setAdaptor(RG.BYPOINT);
  
  RCommand.setSegmentator(RG.ADAPTATIVE);
  
  grp = RG.loadSVG("bot1.svg");
  grp = RG.polygonize(grp);
  grp = RG.centerIn(grp, g, 200);
  
  circle = RG.getEllipse(0, 0, 20);
  circle = RG.centerIn(circle, g, 200);
}

void draw(){
  translate(width/2, height/2);
  background(#2D4D83);

  noFill();
  stroke(255, 200);
  
  float t = map(mouseX, 0, width, 0.01, 0.99);
  RGroup circleSeg = RG.split(circle, t)[0];
  RGroup adaptedGrp = RG.adapt(grp, circleSeg);
  
  adaptedGrp.draw();
  circleSeg.draw();
  
}

void mousePressed(){
  ignoringStyles = !ignoringStyles;
  RG.ignoreStyles(ignoringStyles);
}
