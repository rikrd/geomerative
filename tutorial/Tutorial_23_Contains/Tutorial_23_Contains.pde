import processing.xml.*;
import processing.opengl.*;
import geomerative.*;

RGroup grp;
//RShape grp;

boolean ignoringStyles = false;

void setup(){
  size(800, 600, OPENGL);
  smooth();
  g.smooth = true; 

  // VERY IMPORTANT: Allways initialize the library before using it
  RGeomerative.init(this);
  RGeomerative.ignoreStyles(ignoringStyles);
  
  RCommand.setSegmentator(RCommand.ADAPTATIVE);
  
  RSVG svgLoader = new RSVG();
  grp = svgLoader.toGroup("mapaAzimutal.svg");
  grp.centerIn(g, 100, 1, 1);
}

void draw(){
  translate(width/2, height/2);
  
  background(255);
  stroke(0);
  noFill();
  
  grp.draw();
  RPoint p = new RPoint(mouseX-width/2, mouseY-height/2);
  for(int i=0;i<grp.countElements();i++){
    if(grp.elements[i].contains(p)){
       RGeomerative.ignoreStyles(true);
       fill(0,0,255,150);
       noStroke();
       println("Mouse over: "+grp.elements[i].id);
       grp.elements[i].draw();
       RGeomerative.ignoreStyles(ignoringStyles);
    }
  }
}

void mousePressed(){
  ignoringStyles = !ignoringStyles;
  RGeomerative.ignoreStyles(ignoringStyles);
}
