import processing.opengl.*;
import geomerative.*;

RShape shp;

boolean ignoringStyles = false;

void setup(){
  size(600, 600);
  smooth();
  g.smooth = true; 

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);
  
  shp = RG.loadShape("bot1.svg");
  shp.centerIn(g);
}

void draw(){
  translate(width/2, height/2);
  background(#2D4D83);

  noFill();
  stroke(255, 200);
  RShape[] splittedGroups = shp.split(map(mouseX, 0, width, 0, 1));
  splittedGroups[0].rotate(cos(frameCount*0.1));
  splittedGroups[0].draw();
}

void mousePressed(){
  ignoringStyles = !ignoringStyles;
  RG.ignoreStyles(ignoringStyles);
}
