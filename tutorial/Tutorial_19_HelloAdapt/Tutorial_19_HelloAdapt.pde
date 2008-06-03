import processing.xml.*;
import processing.opengl.*;
import geomerative.*;

RGroup grp;
//RShape grp;
RShape shp;

boolean ignoringStyles = false;

void setup(){
  size(600, 600);
  smooth();
  g.smooth = true; 

  // VERY IMPORTANT: Allways initialize the library before using it
  RGeomerative.init(this);
  RGeomerative.ignoreStyles(ignoringStyles);
  RGeomerative.setAdaptor(RGeomerative.BYPOINT);
  
  RCommand.setSegmentator(RCommand.ADAPTATIVE);
  
  RSVG svgLoader = new RSVG();
  grp = svgLoader.toGroup("bot1.svg");
  grp.centerIn(g);
  
  grp = grp.toPolygonGroup().toShapeGroup();
  //grp = RShape.createStar(0, 0, 40, 4, 4);
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
  //adaptedGrp.centerIn(g);
  
  adaptedGrp.draw();
  splittedShp.draw();
  
}

void mousePressed(){
  ignoringStyles = !ignoringStyles;
  RGeomerative.ignoreStyles(ignoringStyles);
}
