import geomerative.*;

RSVG svgLoader;
RGroup grp;

void setup(){
  size(800, 600);
  smooth();
  // VERY IMPORTANT: Allways initialize the library before using it
  RGeomerative.init(this);
  
  svgLoader = new RSVG();
  grp = svgLoader.toGroup("bot1.svg");
  grp.centerIn(g);
  
  stroke(0,150);
  fill(255);
} 

void draw(){
  background(255);
  
  translate(mouseX, height/2);
  grp.draw();
}
