package data.tests;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.stream.Stream;

import org.junit.Test;

import core.access.IDataAccess;
import core.util.IOUtil;
import data.csv.CSVAccessFactory;

public class CSVAccessTest {

    @Test
    public void testSingleRow() {
        InputStream example = IOUtil.createInput("src/data/tests/example.csv");
        IDataAccess csv = new CSVAccessFactory().newInstance(example);
        
        assertEquals("1997", csv.get(null, 0).get("Year").getContents());
        assertEquals("Venture \"Extended Edition, Very Large\"", csv.get(null, 3).get("Model").getContents());
        assertEquals("ac, abs, moon", csv.get(null, 0).get("Description").getContents());
        assertEquals("", csv.get(null, 1).get("Description").getContents());
    }
    
    @Test
    public void testGetAll() {
        InputStream example = IOUtil.createInput("src/data/tests/example.csv");
        IDataAccess csv = new CSVAccessFactory().newInstance(example);
        Stream<IDataAccess> s = csv.getAll(null);
        IDataAccess[] rows = s.toArray(IDataAccess[]::new);
        
        assertEquals("1997", rows[0].get("Year").getContents());
        assertEquals("", rows[1].get("Description").getContents());
        assertEquals("Venture \"Extended Edition, Very Large\"", rows[3].get("Model").getContents());

    }

}
