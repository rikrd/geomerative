import geomerative.*;

RShape grp;

boolean ignoringStyles = false;

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

  noFill();
  stroke(255, 200);
  
  float t = constrain(map(mouseX, 10, width-10, 0, 1), 0, 1);
  
  RShape[] splittedGroups = RG.split(grp, t); 
  splittedGroups[0].draw();
  
}

void mousePressed(){
  ignoringStyles = !ignoringStyles;
  RG.ignoreStyles(ignoringStyles);
}
