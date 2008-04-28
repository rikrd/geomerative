import processing.opengl.*;
import rgeom.*;
RPolygon p;
int i=2;

void setup(){
  size(300,300,OPENGL);
  smooth();
  stroke(255,0,0);
  fill(0);
  framerate(24);
  p = new RPolygon();
}

void draw(){
  background(255);
  translate(width/2,height/2);
  i++;
  rotateY(i*PI/30);
  //rotateX(i*PI/40);
  p = p.createStar(100f, 15f, i);
  p.draw(g);
}

