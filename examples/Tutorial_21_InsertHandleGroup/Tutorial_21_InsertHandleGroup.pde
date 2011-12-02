import geomerative.*;

RShape grp;
RShape grp2;

boolean ignoringStyles = false;

void setup(){
  size(600, 600);
  smooth();

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);
  RG.ignoreStyles(ignoringStyles);
  
  grp = RG.loadShape("bot1.svg");
  grp = RG.centerIn(grp, g, 100);
  
}

void draw(){
  translate(width/2, height/2);
  background(#2D4D83);

  noFill();
  stroke(255, 200);

  // Split and scale the Group
  grp2 = new RShape(grp);
  grp2.insertHandleInPaths(0.5);
  grp2.scale(1.1);
  
  // Draw the splitted and scaled Group
  stroke(80,220,100);
  grp2.draw();
  
  // Draw the handles and the lines joining them
  stroke(255,100);
  RPoint[] ps = grp2.getHandles();
  beginShape();
  for(int i= 0; i< ps.length; i++){
    vertex(ps[i].x, ps[i].y);
    ellipse(ps[i].x, ps[i].y, 8, 8);
  }
  endShape();
}

void mousePressed(){
  ignoringStyles = !ignoringStyles;
  RG.ignoreStyles(ignoringStyles);
}
