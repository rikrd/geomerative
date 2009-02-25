/*
*    geomerative example
*    http://www.ricardmarxer.com/geomerative/
*
*    export nach PDF einer geladenen TrueType datei
*
*    fjenett 20080417
*    fjenett 20081203 - updated to geomerative 19
*/

import geomerative.*;
import processing.pdf.*;

RFont font;

void setup()
{
    size(400,400);
    smooth();
    
    RG.init(this);

    font = new RFont( "lucon.ttf", 80, RFont.CENTER);
}

void draw()
{
    beginRecord(PDF, "test.pdf");
    
    background(255);
    translate(width/2,height/2);
    
    font.draw("Hello?");
    
    endRecord();
    noLoop();
}
