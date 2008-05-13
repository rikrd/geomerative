import processing.opengl.*;
import geomerative.*;

RShape shp;

boolean ignoringStyles = false;

void setup(){
  size(600, 600);
  smooth();
  g.smooth = true; 

  // VERY IMPORTANT: Allways initialize the library before using it
  RGeomerative.init(this);
  RGeomerative.ignoreStyles(ignoringStyles);
  
  RCommand.setSegmentator(RCommand.ADAPTATIVE);
  
  RSVG svgLoader = new RSVG();
  shp = svgLoader.toShape("bot1.svg");
  
  shp.centerIn(g);
}

void draw(){
  translate(width/2, height/2);
  background(#2D4D83);

  noFill();
  stroke(255, 200);
  //shp.draw();
  float t = map(mouseX, 0, width, 0, 1);
  RShape[] splittedShapes = shp.split(0.2);
  //splittedShapes[0].rotate(cos(frameCount*0.01));
  //splittedShapes[0].print();
  RPolygon poly = splittedShapes[0].toPolygon();
  
  for(int i = 0; i<poly.countContours();i++){
    poly.contours[i].draw();
  }
  splittedShapes[0].draw();
  
}

void mousePressed(){
  ignoringStyles = !ignoringStyles;
  RGeomerative.ignoreStyles(ignoringStyles);
}
