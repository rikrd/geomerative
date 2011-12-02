import geomerative.*;

RShape shp;
RPoint[] points;
RPoint[] tangents;

boolean ignoringStyles = true;

int numPoints = 500;

void setup(){
  size(600, 600);
  smooth();

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);
  RG.ignoreStyles(ignoringStyles);
  
  shp = RG.loadShape("bot1.svg");
  shp = RG.centerIn(shp, g, 100);

  
}

void draw(){
  translate(mouseX, mouseY);
  background(#2D4D83);
  
  RG.shape(shp);

  noFill();
  stroke(255, 100);
  
  points = null;
  tangents = null;
  
  for(int i=0; i<numPoints; i++){
    RPoint point = shp.getPoint(float(i)/float(numPoints));
    RPoint tangent = shp.getTangent(float(i)/float(numPoints));
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
  RG.ignoreStyles(ignoringStyles);
}
