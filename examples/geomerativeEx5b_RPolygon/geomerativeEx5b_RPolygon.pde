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
        frameRate( 10 );
    
        RG.init( this );
    }
    
    
    void draw ()
    {
        background( color( 12, 200, 170 ) );
        translate( width/2, height/2 );
        
        if ( frameCount % 50 == 1 )
            rp = RShape.createCircle( 0, 0, random(1,30) ).toPolygon();
        
        RPolygon r2 = RShape.createCircle( random(-width/2,width/2) , random(-height/2,height/2), random(1,30) ).toPolygon();
        
        rp = r2.union( rp );
        
        rp.draw();
    }
