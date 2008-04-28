import processing.opengl.*;
import rgeom.*;

RMesh m;
RPolygon p2 = new RPolygon();
RPolygon p = new RPolygon();
float t=0;

void setup(){
  size(400,400,OPENGL);
  framerate(24);
  background(255);
  smooth();
    
    
  RContour c = new RContour();
  
  int puntas=16;
  float radBig=100,radSmall=70;
  
  for(int i=0;i<2*puntas;i+=2){
    c.add(radBig*sin(PI*i/puntas),radBig*cos(PI*i/puntas));
    c.add(radSmall*sin(PI*(i+1)/puntas),radSmall*cos(PI*(i+1)/puntas));
  }
  
  p.add(c);
  
  c = new RContour();
  float radSmallest=50;
  for(int i=0;i<2*puntas;i+=1){
    c.add(radSmallest*sin(PI*i/puntas),radSmallest*cos(PI*i/puntas));
  }

  p2.add(c);
  p = (RPolygon)(p.diff(p2));
  
  m = p.toMesh();
}

void draw(){ 
  background(255);
  fill(0);
  noStroke();
  noSmooth();
  translate(width/2,height/2);


  rotateX(t/39);
  for(int i=0;i<m.strips.length;i++){
    beginShape(TRIANGLE_STRIP);
      for(int j=0;j<m.strips[i].vertices.length;j++){
        vertex((float)(m.strips[i].vertices[j].x),(float)(m.strips[i].vertices[j].y));
      }
    endShape();
  }
  
  rotateY(-t/5);
  scale(0.4);
  for(int i=0;i<m.strips.length;i++){
    beginShape(TRIANGLE_STRIP);
      for(int j=0;j<m.strips[i].vertices.length;j++){
        vertex((float)(m.strips[i].vertices[j].x),(float)(m.strips[i].vertices[j].y));
      }
    endShape();
  }
  t++;
}

