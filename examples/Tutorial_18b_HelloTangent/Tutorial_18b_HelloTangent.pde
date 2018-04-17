import geomerative.*;

RShape grp;
RShape s;
float pos;
float len;
float angle;

// The error range for the tangent position and angle
float ANGLEERROR = 0;//0.01;
float POINTERROR = 0;

// The length variation of the tangnet
//   -> 500: sketchy, blueprint
//   -> 150: light blueprint
//   -> 2000: mystic
float LENGTHTANGENT = 40;
float LENGTHTANGENTGROWTH = 1.0001;//04;

// The alpha value of the lines
int ALPHAVALUE = 3;

// The velocity of the calligraphy
int VELOCITY = 500;

void setup(){
  size(600, 600);
  smooth();

  // VERY IMPORTANT: Allways initialize the library before using it
  RG.init(this);
  
  grp = RG.loadShape("bot1.svg");
  grp = RG.centerIn(grp, g);
 
  LENGTHTANGENT = LENGTHTANGENT * width/800F;
  
  background(#2D4D83);
  translate(width/2, height/2);
  stroke(255, ALPHAVALUE);
  noFill();
}

void draw(){
  translate(width/2, height/2);
  
  pushMatrix();
  for(int k=0;k<VELOCITY;k++){
    pos = random(0.01, 0.99);

    RPoint tg = grp.getTangent(pos);
    RPoint p = grp.getPoint(pos);
    
    p.x = p.x + random(-POINTERROR,POINTERROR);
    p.y = p.y + random(-POINTERROR,POINTERROR);

    len = random(-LENGTHTANGENT, LENGTHTANGENT);
    angle = atan2(tg.y, tg.x) + random(-ANGLEERROR, ANGLEERROR);

    line(p.x, p.y, p.x + len*cos(angle), p.y + len*sin(angle));
  }
      
  popMatrix();
  LENGTHTANGENT *= LENGTHTANGENTGROWTH;
}
