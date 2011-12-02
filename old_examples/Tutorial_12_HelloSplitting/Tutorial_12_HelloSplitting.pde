import geomerative.*;

RCommand curva = new RCommand(0 , 0, -200, -200, 200, -200, 0, -100);
RCommand[] piezas = new RCommand[2];

void setup(){
  size(600, 600);
  smooth();
  
  RG.init(this);
  
}

void draw(){
  translate(width/2, height/2);
  background(0);
  piezas = curva.split(map(mouseX, 0, width, 0, 1));

  noFill();
  stroke(0,50);
  curva.draw();
  
  stroke(255, 0, 0, 255);
  piezas[0].draw();
  ellipse(piezas[0].endPoint.x, piezas[0].endPoint.y, 10, 10);
  
  stroke(0, 255, 0, 255);
  piezas[1].draw();
  ellipse(piezas[1].endPoint.x, piezas[1].endPoint.y, 10, 10);
}
