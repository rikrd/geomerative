/*
*    geomerative example
*
*    http://www.ricardmarxer.com/geomerative/rshape_class_rshape.htm
*
*    fjenett 20080419
 *    fjenett 20081203 - updated to geomerative 19
*/

    import geomerative.*;
    
    
    RPolygon rp;
    

    void setup ()
    {
        size( 200, 200 );
        frameRate( 2 );
    
        RG.init( this );
        
        rp = RShape.createCircle( 0, 0, random(1,100) ).toPolygon();
    }
    
    
    void draw ()
    {
        background( color( 12, 200, 170 ) );
        translate( width/2, height/2 );
        
        RPolygon r2 = RShape.createCircle( 0, 0, random(1,100) ).toPolygon();
        
        rp = r2.diff( rp );
        
        rp.draw();
    }
