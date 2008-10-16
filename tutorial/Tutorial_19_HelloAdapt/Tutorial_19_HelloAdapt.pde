import processing.xml.*;
import processing.opengl.*;
import geomerative.*;

RGroup grp;
RShape shp;

boolean ignoringStyles = false;

void setup(){
  size(600, 600);
  smooth();
  g.smooth = true; 

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);
  RG.ignoreStyles(ignoringStyles);
  RG.setAdaptor(RG.BYPOINT);
  
  RCommand.setSegmentator(RCommand.ADAPTATIVE);
  
  grp = RG.loadSVG("bot1.svg");
  grp.centerIn(g);
  
  grp.polygonize();
  grp.centerIn(g, 200, 1, 1);
  
  shp = RShape.createCircle(0, 0, 20);
  shp.centerIn(g, 200, 1, 1);
}

void draw(){
  translate(width/2, height/2);
  background(#2D4D83);

  noFill();
  stroke(255, 200);
  
  RGroup adaptedGrp = new RGroup(grp);
  
  RShape splittedShp = shp.split(map(mouseX, 0, width, 0.01, 0.99))[0];
  adaptedGrp.adapt(splittedShp);
  
  adaptedGrp.draw();
  splittedShp.draw();
  
}

void mousePressed(){
  ignoringStyles = !ignoringStyles;
  RG.ignoreStyles(ignoringStyles);
}
