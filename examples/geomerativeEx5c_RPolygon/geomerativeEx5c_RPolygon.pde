/*
*    geomerative example
*
*    http://www.ricardmarxer.com/geomerative/rshape_class_rshape.htm
*
*    fjenett 20080419
*/

    import geomerative.*;
    
    
    RPolygon rp;
    

    void setup ()
    {
        size( 200, 200 );
        frameRate( 5 );
    
        RG.init( this );
    }
    
    
    void draw ()
    {
        background( color( 12, 200, 170 ) );
        translate( width/2, height/2 );
        
        rp = RShape.createCircle( 0, 0, 80 ).toPolygon();
        
        noFill();
        stroke( color( 12/1.25, 200/1.25, 170/1.25 ) );
        //rp.draw();
        
        RPolygon r2 = RShape.createCircle( random(-width/2,width/2) , random(-height/2,height/2), random(30,180) ).toPolygon();
        
        rp = r2.intersection( rp );
        
        fill(255);
        stroke( 0 );
        rp.draw();
    }
