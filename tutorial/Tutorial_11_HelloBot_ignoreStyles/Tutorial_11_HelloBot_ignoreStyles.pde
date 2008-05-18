import processing.xml.*;
import processing.opengl.*;
import geomerative.*;

RSVG svgLoader;
RGroup grp;

RGroup newGrp;

void setup(){
  size(800, 600);
  smooth();
  g.smooth = true;

  // VERY IMPORTANT: Allways initialize the library before using it
  RGeomerative.init(this);
  //RGeomerative.ignoreStyles();

  svgLoader = new RSVG();
  grp = svgLoader.toGroup("lion.svg");
  grp.centerIn(g);
  //printGroup(grp, "");
} 

void draw(){
  background(255);
  translate(width/2, height/2);
  RCommand.setSegmentator(RCommand.UNIFORMLENGTH);
  RCommand.setSegmentLength(map(constrain(mouseX, 200, width-200), 200, width-200, 5, 300));
  newGrp = grp.toPolygonGroup();
  
  grp.draw();
  RGeomerative.ignoreStyles(false);  
  newGrp.draw();
  
  RGeomerative.ignoreStyles();
  noFill();
  stroke(0, 100);
  grp.draw();
}


void printGroup(RGroup grp, String prefix){
  if(grp.elements != null){
    for(int i=0;i<grp.elements.length;i++){
      RGeomElem elem = grp.elements[i];
      println(prefix + "id: " + elem.id);
      println(prefix + "  fillDef: " + elem.fillDef);
      println(prefix + "  fill: " + elem.fill);
      println(prefix + "  fillColor: " + hex(elem.fillColor));
      println(prefix + "  fillAlphaDef: " + elem.fillAlphaDef);
      println(prefix + "  fillAlpha: " + elem.fillAlpha);
      println(prefix + "  strokeDef: " + elem.fillDef);
      println(prefix + "  stroke: " + elem.fill);
      println(prefix + "  strokeColor: " + hex(elem.fillColor));
      println(prefix + "  strokeAlphaDef: " + elem.fillAlphaDef);
      println(prefix + "  strokeAlpha: " + elem.fillAlpha);
      println(prefix + "  strokeWeightDef: " + elem.strokeWeightDef);
      println(prefix + "  strokeWeight: " + elem.strokeWeight);
      println(prefix + "-----------------------");

      if(elem.getType() == RGeomElem.GROUP){
        printGroup((RGroup)elem, prefix+"  ");
      }
    }
  }
}
