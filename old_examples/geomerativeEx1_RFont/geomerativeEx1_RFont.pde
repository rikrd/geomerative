/*
*    geomerative example
*    http://www.ricardmarxer.com/geomerative/
*
*    einen font laden aus einer (win) truetype datei
*
*    fjenett 20080417
*    fjenett 20081203 - updated to geomerative 19
*/

import geomerative.*;

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
    background(255);
    translate(width/2,height/2.0+font.size/3.0); // optische mitte?
    
    font.draw("Hell'o");
}
