import processing.xml.*;
import processing.opengl.*;
import geomerative.*;

RSVG svgLoader;
RGroup grp;

void setup(){
  size(800, 600);
  smooth();
  g.smooth = true;

  // VERY IMPORTANT: Allways initialize the library before using it
  RGeomerative.init(this);
  //RGeomerative.ignoreStyles();

  svgLoader = new RSVG();
  grp = svgLoader.toGroup("tiger.svg");
  grp.centerIn(g);

  //printGroup(grp, "");
} 

void draw(){
  background(255);
  //translate(width/2, height/2);
  translate(mouseX, mouseY);
  grp.draw();
}


void printGroup(RGroup grp, String prefix){
  if(grp.elements != null){
    for(int i=0;i<grp.elements.length;i++){
      RGeomElem elem = grp.elements[i];
      println(prefix + "id: " + elem.id);
      println(prefix + "  fill: " + elem.fill);
      println(prefix + "  fillColor: " + hex(elem.fillColor));
      println(prefix + "-----------------------");

      if(elem.getType() == RGeomElem.GROUP){
        printGroup((RGroup)elem, prefix+"  ");
      }
    }
  }
}
