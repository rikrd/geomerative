import geomerative.*;

RSVG svgLoader;
RGroup grp;

void setup(){
  size(800, 600);
  
  // VERY IMPORTANT: Allways initialize the library before using it
  RGeomerative.init(this);
  
  svgLoader = new RSVG();
  grp = svgLoader.toGroup("pirata.svg");
  grp.centerIn(g);
  
  stroke(0,150);
  fill(#F2931D, 150);
} 

void draw(){
  background(255);
  
  translate(mouseX, height/2);

  grp.draw();
}
