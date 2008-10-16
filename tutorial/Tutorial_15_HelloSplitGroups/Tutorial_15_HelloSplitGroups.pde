import processing.xml.*;
import processing.opengl.*;
import geomerative.*;

RGroup grp;

boolean ignoringStyles = false;

void setup(){
  size(600, 600);
  smooth();
  g.smooth = true; 

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);
  RG.ignoreStyles(ignoringStyles);
  
  RCommand.setSegmentator(RCommand.ADAPTATIVE);
  
  RSVG svgLoader = new RSVG();
  grp = svgLoader.toGroup("bot1.svg");
  grp.centerIn(g);
  
}

void draw(){
  translate(width/2, height/2);
  background(#2D4D83);

  noFill();
  stroke(255, 200);
  
  float t = constrain(map(mouseX, 10, width-10, 0, 1), 0, 1);
  //println(t);
  RGroup[] splittedGroups = grp.split(t); 
  splittedGroups[0].draw();
  
}

void mousePressed(){
  ignoringStyles = !ignoringStyles;
  RG.ignoreStyles(ignoringStyles);
}
