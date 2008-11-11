import processing.opengl.*;

import geomerative.*;
import org.apache.batik.svggen.font.table.*;
import org.apache.batik.svggen.font.*;

RShape bot;
RProjector proj;

void setup(){
  size(500, 500, P3D);
  smooth();

  RG.init(this);

  bot = RG.loadShape("bot1.svg");

  proj = new RLambertAzimuthalEqualAreaProjector(0,0);

}

void draw(){
  //RG.shape(bot, 10, 10, 490, 490);
  //RG.shape(bot, 10, 10);
  background(0);
  stroke(255);
  noFill();
  RShape shp = new RShape(bot);
  RPoint[] ps = shp.getHandles();
  proj.unproject(ps);
    //println(ps[i].x);
  //ps = shp.getPoints();
  
  translate(width/2, height/2);  

  rotateX(frameCount/20F);
  rotateY(frameCount/30F);
  
  for(int i=0;i<ps.length;i++){
    pushMatrix();
    rotateZ(ps[i].x*4);
    rotateY(ps[i].y*4);
    translate(100, 0, 0); 
    point(0, 0, 0);
    popMatrix();
  }
}

