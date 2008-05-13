import processing.opengl.*;
import geomerative.*;

RGroup grp;

boolean ignoringStyles = false;

void setup(){
  size(600, 600, OPENGL);
  smooth();
  g.smooth = true; 

  // VERY IMPORTANT: Allways initialize the library before using it
  RGeomerative.init(this);
  RGeomerative.ignoreStyles(ignoringStyles);
  
  RCommand.setSegmentator(RCommand.ADAPTATIVE);
  
  RSVG svgLoader = new RSVG();
  grp = svgLoader.toGroup("tiger.svg");
  grp.centerIn(g);
  
}

void draw(){
  translate(width/2, height/2);
  background(#2D4D83);

  noFill();
  stroke(255, 200);
  //shp.draw();
  float t = map(mouseX, 0, width, 0, 1);
  RGroup[] splittedGroups = grp.split(t); 
  splittedGroups[1].draw();
  
}

void mousePressed(){
  ignoringStyles = !ignoringStyles;
  RGeomerative.ignoreStyles(ignoringStyles);
}
