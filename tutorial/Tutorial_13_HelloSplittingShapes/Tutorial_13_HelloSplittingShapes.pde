import processing.opengl.*;
import geomerative.*;

RSVG svgLoader;
RGroup grp;

boolean ignoringStyles = false;

void setup(){
  size(600, 600, OPENGL);
  smooth();
  g.smooth = true; 

  // VERY IMPORTANT: Allways initialize the library before using it
  RGeomerative.init(this);
  RGeomerative.ignoreStyles(ignoringStyles);

  svgLoader = new RSVG();
  grp = svgLoader.toGroup("bot1.svg");
  grp.centerIn(g);
}

void draw(){
  translate(width/2, height/2);
  background(#2D4D83);

  noFill();
  stroke(255, 200);
  RGroup[] splittedGroups = grp.split(map(mouseX, 0, width, 0, 1));
  splittedGroups[0].rotate(cos(frameCount*0.1));
  splittedGroups[0].draw();
}

void mousePressed(){
  ignoringStyles = !ignoringStyles;
  RGeomerative.ignoreStyles(ignoringStyles);
}
