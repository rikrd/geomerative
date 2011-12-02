import geomerative.*;

RShape   grp;
RShape   circle;

boolean ignoringStyles = false;

void setup(){
  size(600, 600);
  smooth();

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);
  
  grp = RG.getText("around the world", "FreeSans.ttf", 72, RG.CENTER);

  RG.setPolygonizer(RG.ADAPTATIVE);
  RG.setPolygonizerAngle(0.065);
  grp = RG.polygonize(grp);
    
  circle = RG.getEllipse(0, 0, 20);
  circle = RG.centerIn(circle, g, 220);  
}

void draw(){
  translate(width/2, height/2);
  background(#2D4D83);

  fill(0);
  stroke(0);
  
  float t = map(mouseX, 0, width, 0.01, 0.99);
  RShape circleSeg = RG.split(circle, t)[0];
  
  RG.setAdaptor(RG.BYELEMENTPOSITION);
  RShape adaptedGrp = RG.adapt(grp, circleSeg);
  
  RG.shape( adaptedGrp );
  
  noFill();
  stroke(255, 200);
    
  RG.shape( circleSeg );
}

void mousePressed(){
  ignoringStyles = !ignoringStyles;
  RG.ignoreStyles(ignoringStyles);
}
