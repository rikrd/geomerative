import geomerative.*;

RShape shp;
RShape shp2;

boolean ignoringStyles = false;

void setup(){
  size(600, 600);
  smooth();

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);
  RG.ignoreStyles(ignoringStyles);
  
  shp = RShape.createCircle(0, 0, 20);
  shp.centerIn(g, 200, 1, 1);
  
}

void draw(){
  translate(width/2, height/2);
  background(#2D4D83);

  noFill();
  stroke(255, 200);

  // Split and scale the shape
  shp2 = new RShape(shp);
  shp2.insertHandleInPaths(0.5);
  shp2.scale(1.1);
  
  // Draw the handles and the lines joining them
  RPoint[] ps = shp2.getHandles();
  beginShape();
  for(int i= 0; i< ps.length; i++){
    vertex(ps[i].x, ps[i].y);
    ellipse(ps[i].x, ps[i].y, 8, 8);
  }
  endShape();
  
  // Draw the splitted and scaled shape
  stroke(80,220,100);
  shp2.draw();

  // Draw the original shape in orange  
  stroke(200,80,0);
  shp.draw();
}

void mousePressed(){
  ignoringStyles = !ignoringStyles;
  RG.ignoreStyles(ignoringStyles);
}
