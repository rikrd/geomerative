import processing.xml.*;
import processing.opengl.*;
import geomerative.*;

RGroup grp;
RPoint[] points;
RPoint[] tangents;

boolean ignoringStyles = true;

int numPoints = 500;

void setup(){
  size(600, 600);
  smooth();
  g.smooth = true; 

  // VERY IMPORTANT: Allways initialize the library before using it
  RGeomerative.init(this);
  RGeomerative.ignoreStyles(ignoringStyles);
  
  RSVG svgLoader = new RSVG();
  grp = svgLoader.toGroup("bot1.svg");
  grp.centerIn(g);

  
}

void draw(){
  translate(mouseX, mouseY);
  background(#2D4D83);
  
  grp.draw();

  noFill();
  stroke(255, 100);
  
  points = null;
  tangents = null;
  
  for(int i=0; i<numPoints; i++){
    RPoint point = grp.getPoint(float(i)/float(numPoints));
    RPoint tangent = grp.getTangent(float(i)/float(numPoints));
    if(points == null){
      points = new RPoint[1];
      tangents = new RPoint[1];
      
      points[0] = point;
      tangents[0] = tangent;
    }else{
      points = (RPoint[])append(points, point);
      tangents = (RPoint[])append(tangents, tangent);
    }
  }
  
  for(int i=0;i<points.length;i++){
    pushMatrix();
    translate(points[i].x, points[i].y);
    ellipse(0, 0, 10, 10);
    line(0, 0, tangents[i].x, tangents[i].y);
    popMatrix();  
  }
  
}

void mousePressed(){
  ignoringStyles = !ignoringStyles;
  RGeomerative.ignoreStyles(ignoringStyles);
}
