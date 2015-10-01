package core.tests;

import org.junit.*;
import core.schema.*;

import static org.junit.Assert.*;

/**
 * Unit tests for core.schema classes
 *
 */
public class TestSchema {

    PrimSchema p1 = new PrimSchema();
    PrimSchema p2 = new PrimSchema("name", "manufacturer name");
    PrimSchema p3 = new PrimSchema("mpg", "miles per gallon");
    
    CompSchema c1 = new CompSchema( new CompField( "name", p2 ), new CompField( "mileage", p3 ) );
    CompSchema c2 = new CompSchema( "car", "mileage information about car models", 
            new CompField( "name", p2 ), new CompField( "mileage", p3 ) );
    
    ListSchema l1 = new ListSchema( p1 );
    ListSchema l2 = new ListSchema( c2 );
    ListSchema l3 = new ListSchema( "cars", "a list of car data", c2 );
    ListSchema l4 = new ListSchema( "cars", c1 );
    
    @Test
    public void testToString() {
        assertEquals( "*", p1.toString());
        assertEquals( "*", p1.toString(true));   // verbose
        
        assertEquals( "*", p2.toString());
        assertEquals( "(mpg)_*", p3.toString(true) );
        
        assertEquals( "{name: *, mileage: *}", c1.toString());
        assertEquals( "{name: (name)_*, mileage: (mpg)_*}", c1.toString(true));
        
        assertEquals( "{name: *, mileage: *}", c2.toString());
        assertEquals( "(car)_{name: (name)_*, mileage: (mpg)_*}", c2.toString(true));
        
        assertEquals( "[*]", l1.toString() );
        assertEquals( "[*]", l1.toString(true) );
        
        assertEquals( "[{name: *, mileage: *}]", l2.toString() );
        assertEquals( "[(car)_{name: (name)_*, mileage: (mpg)_*}]", l2.toString(true) );
        
        assertEquals( "[{name: *, mileage: *}]", l3.toString() );
        assertEquals( "(cars)_[(car)_{name: (name)_*, mileage: (mpg)_*}]", l3.toString(true) );
        
        assertEquals( "[{name: *, mileage: *}]", l4.toString() );
        assertEquals( "(cars)_[{name: (name)_*, mileage: (mpg)_*}]", l4.toString(true) );
    }
}
