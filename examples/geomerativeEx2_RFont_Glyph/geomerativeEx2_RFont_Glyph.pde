/*
*    geomerative example
*    http://www.ricardmarxer.com/geomerative/
*
*    glyphen eines fonts auslesen
*
*    fjenett 20080417
*    fjenett 20081203 - updated to geomerative 19
*/

import geomerative.*;

RFont font;

void setup()
{
    size(600,400);
    smooth();
    
    RG.init(this);

    font = new RFont( "lucon.ttf", 80, RFont.CENTER);
}

void draw()
{
    background(255);
    translate(width/2,height/2);
    
    RGroup grp = font.toGroup("Hello?");                                // text in ein gruppen-objekt umwandeln
    
    for ( int i = 0; i < grp.elements.length; i++ )                     // elemente durchlaufen
    {
        RShape shp = grp.elements[i].toShape();                         // gruppen-element in shape-objekt umwandeln
        
        for ( int ii = 0; ii < shp.paths.length; ii++ )             // shapes durchlaufen
        {
            RPath sushp = shp.paths[ii];                        // subshape-objekt
            
            for ( int iii = 0; iii < sushp.commands.length; iii++ )     // zeichen-kommando-objekte durchlaufen
            {
                RPoint[] pnts = sushp.commands[iii].getHandles();        // punkte des kommando-objekts
                
                if ( pnts.length < 2 ) continue;
                
                switch( sushp.commands[iii].getCommandType() )          // je nach kommando-art anderern befehl ausf�hren
                {
                    case RCommand.LINETO:
                        line( pnts[0].x, pnts[0].y, pnts[1].x, pnts[1].y );
                        break;
                    case RCommand.QUADBEZIERTO:    // eigentlich falsch, denn bezier() ist kubisch
                        bezier( pnts[0].x, pnts[0].y, pnts[1].x, pnts[1].y, pnts[1].x, pnts[1].y, pnts[2].x, pnts[2].y );
                       break;
                    case RCommand.CUBICBEZIERTO:
                        bezier( pnts[0].x, pnts[0].y, pnts[1].x, pnts[1].y, pnts[2].x, pnts[2].y, pnts[3].x, pnts[3].y );
                       break;
                }
            }
        }
    }
}
