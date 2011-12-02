import geomerative.*;

RShape grp;

int first = 0;

void setup(){
  size(600, 600);
  smooth();

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);
  
  grp = RG.loadShape("bot1.svg");
  grp = RG.centerIn(grp, g);
}

void draw(){
  translate(width/2, height/2);
  background(#2D4D83);
  
  float t = map(mouseX, 0, width, 0, 1);
  RShape[] splittedGroups = grp.splitPaths(t);
  
  RG.shape(splittedGroups[first]);
}

void mousePressed(){
  first = (first + 1) % 2;
}
