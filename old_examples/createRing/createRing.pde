import rgeom.*;
RPolygon p,q;
int i=0;

void setup(){
  size(400,400,P3D);
  fill(255);
  framerate(24);
  
  p = new RPolygon();
  q = new RPolygon();
  
  p = p.createRing(100f, 80f, 4);
  q = p.createRing(60f, 40f, 3);
}

void draw(){
  background(255);
  translate(width/2,height/2);
  
  rotateY(i*PI/50);
  p.draw(g);
  rotateX((i+1)*PI/60);
  q.draw(g);
  
  i++;
}
