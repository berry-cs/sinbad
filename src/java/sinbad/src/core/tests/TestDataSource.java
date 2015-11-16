package core.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import core.data.DataSource;

public class TestDataSource {
    
    @Test
    public void testCSVDataSource() {
        DataSource ds = DataSource.connect("src/core/tests/example.csv");
        
        assertEquals(true, ds.readyToLoad());
        
        ds.load();
        System.out.println(ds.getDataAccess().getSchema());
        
        assertEquals("1997", ds.fetch("String", "Year"));
        assertEquals((Integer)1997, ds.fetch("Integer", "Year"));
        assertEquals(new A("Ford", 1997, 3000.0), 
                     ds.fetch("core.tests.A", "Make", "Year", "Price"));
        
        ArrayList<A> as = ds.fetchList(A.class,  "Model", "Year", "Price");
        System.out.println(as);
        
        A[] aarray = ds.fetchArray("core.tests.A", "Model", "Year", "Price");
        assertEquals(5, aarray.length);
        
        ds.printUsageString();
    }

}

class A {
    String make;
    int year;
    double price;

    A(String make, int year, double price) {
        super();
        this.make = make;
        this.year = year;
        this.price = price;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((make == null) ? 0 : make.hashCode());
        long temp;
        temp = Double.doubleToLongBits(price);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + year;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof A))
            return false;
        A other = (A) obj;
        if (make == null) {
            if (other.make != null)
                return false;
        } else if (!make.equals(other.make))
            return false;
        if (Double.doubleToLongBits(price) != Double
                .doubleToLongBits(other.price))
            return false;
        if (year != other.year)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "A [make=" + make + ", year=" + year + ", price=" + price + "]";
    }

}