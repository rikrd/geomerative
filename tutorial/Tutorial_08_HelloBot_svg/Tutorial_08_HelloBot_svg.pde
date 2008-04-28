import geomerative.*;

RSVG svgLoader;

void setup(){
  size(300, 300);
  
  // VERY IMPORTANT: Allways initialize the library before using it
  RGeomerative.init(this);
  
  svgLoader = new RSVG();
} 

void draw(){
  background(255);

  svgLoader.draw("bot1.svg");
}
