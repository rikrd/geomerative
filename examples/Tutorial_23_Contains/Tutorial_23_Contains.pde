import geomerative.*;

RShape grp;

boolean ignoringStyles = false;

void setup(){
  size(600, 600);
  smooth();

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);
  RG.ignoreStyles(ignoringStyles);
  
  RG.setPolygonizer(RG.ADAPTATIVE);
   
  grp = RG.loadShape("mapaAzimutal.svg");
  grp.centerIn(g, 100, 1, 1);
}

void draw(){
  translate(width/2, height/2);
  
  background(255);
  stroke(0);
  noFill();
  
  grp.draw();
  RPoint p = new RPoint(mouseX-width/2, mouseY-height/2);
  for(int i=0;i<grp.countChildren();i++){
    if(grp.children[i].contains(p)){
       RG.ignoreStyles(true);
       fill(0,100,255,250);
       noStroke();
       grp.children[i].draw();
       RG.ignoreStyles(ignoringStyles);
    }
  }
}

void mousePressed(){
  ignoringStyles = !ignoringStyles;
  RG.ignoreStyles(ignoringStyles);
}
