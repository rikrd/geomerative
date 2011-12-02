import processing.opengl.*;
import geomerative.*;

RCommand line1, line2;

void setup(){
  size(600, 600, OPENGL);
  smooth();

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);

  line1 = new RCommand(10, 10, 200, 200);
}

void draw(){
  background(#2D4D83);
  
  noFill();
  stroke(255);
  
  line2 = new RCommand(width, 10, mouseX, mouseY);
    
  line1.draw();
  line2.draw();
    
  // Get the intersection points
  RClosest c = line1.closestPoints(line2);
  RPoint[] ps = c.distance > 0 ? c.closest : c.intersects;
  
  if (ps != null) {
    for (int i=0; i<ps.length; i++) {
      ellipse(ps[i].x, ps[i].y, 10, 10);
    }
  }
}
