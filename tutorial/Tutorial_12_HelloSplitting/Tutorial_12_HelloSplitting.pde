import processing.opengl.*;
import geomerative.*;

//RCommand curva = new RCommand(0 , 0, -200, 200, 400, -300, 0, -300);
//RCommand curva = new RCommand(0 , 0, 400, 300, 0, -200);
RCommand curva = new RCommand(-200 , -300, 200, 300);
RCommand[] piezas = new RCommand[2];

void setup(){
  size(600, 600, OPENGL);
  smooth();
  g.smooth = true; 
  
  RGeomerative.init(this);
  
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
  
  stroke(0, 255, 0, 255);
  piezas[1].draw();

}
