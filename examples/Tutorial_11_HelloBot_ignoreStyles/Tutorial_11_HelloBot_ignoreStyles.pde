import geomerative.*;

RShape grp;
RShape newGrp;

void setup(){
  size(800, 600);
  smooth();

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);

  grp = RG.loadShape("Toucan.svg");
  grp.centerIn(g);
} 

void draw(){
  background(255);
  translate(width/2, height/2);
  
  float pointSeparation = map(constrain(mouseX, 200, width-200), 200, width-200, 5, 300);
  
  RG.setPolygonizer( RG.UNIFORMLENGTH );
  RG.setPolygonizerLength( pointSeparation );
  newGrp = RG.polygonize( grp );
  
  
  RG.ignoreStyles(false);  
  newGrp.draw();
  
  RG.ignoreStyles();
  noFill();
  stroke(0, 100);
  grp.draw();
}
