import geomerative.*;

RShape grp;
RPoint[][] pointPaths;

float xmag, ymag, newYmag, newXmag = 0;
float z = 0;

boolean ignoringStyles = false;

void setup(){
  size(600, 600, P3D);

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);
  RG.ignoreStyles(ignoringStyles);
  
  RG.setPolygonizer(RG.ADAPTATIVE);
  
  grp = RG.loadShape("bot1.svg");
  grp.centerIn(g, 100, 1, 1);
  
  pointPaths = grp.getPointsInPaths();
}

void draw(){
  translate(width/2, height/2);
  
  newXmag = mouseX/float(width) * TWO_PI;
  newYmag = mouseY/float(height) * TWO_PI;
  
  float diff = xmag-newXmag;
  if (abs(diff) >  0.01) { xmag -= diff/4.0; }
  
  diff = ymag-newYmag;
  if (abs(diff) >  0.01) { ymag -= diff/4.0; }
  
  rotateX(-ymag); 
  rotateY(-xmag); 
  
  background(0);
  stroke(255);
  noFill();
  
  z = 10 * sin( frameCount/50.0 * PI);
  
  for(int i = 0; i<pointPaths.length; i++){
    translate(0,0,z);

    if (pointPaths[i] != null) {
      beginShape();
      for(int j = 0; j<pointPaths[i].length; j++){
        vertex(pointPaths[i][j].x, pointPaths[i][j].y);
      }
      endShape();
    }
  }
}

void mousePressed(){
  ignoringStyles = !ignoringStyles;
  RG.ignoreStyles(ignoringStyles);
}
