import processing.core.*; 
import processing.xml.*; 

import processing.xml.*; 
import processing.opengl.*; 
import geomerative.*; 

import java.applet.*; 
import java.awt.*; 
import java.awt.image.*; 
import java.awt.event.*; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class Tutorial_10_HelloBot_svgToPolygonGroup extends PApplet {





RSVG svgLoader;
RGroup grp;

RGroup polyGrp;

public void setup(){
  size(800, 600);
  smooth();
  g.smooth = true;

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);

  grp = RG.loadSVG("pirata.svg");
  grp.centerIn(g);
} 

public void draw(){
  background(255);

  // We decided the separation between the polygon points dependent of the mouseX
  float pointSeparation = map(constrain(mouseX, 200, width-200), 200, width-200, 5, 300);
  
  // We create the polygonized version
  RCommand.setSegmentator(RG.UNIFORMLENGTH);
  RCommand.setSegmentLength(pointSeparation);
  polyGrp = grp.toPolygonGroup();
  
  // We move ourselves to the mouse position
  translate(mouseX, mouseY);
  
  // We draw the polygonized group with the SVG styles
  polyGrp.draw();
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "Tutorial_10_HelloBot_svgToPolygonGroup" });
  }
}
