package data.tests;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

import core.access.IDataAccess;
import core.util.FileLoader;
import data.json.JsonFactory;

public class JsonDataTest {

    @Test
    public void testSingleResult() {
        InputStream example = new FileLoader().createInput("src/data/tests/example.json");
        IDataAccess json = new JsonFactory().newInstance(example);
        
        assertEquals("FeatureCollection", json.get("type").getContents());
        assertEquals("ak13636713", json.get("features/0/id").getContents());
        
        assertEquals("37.8753", json.get("features/1/geometry/coordinates", 1).getContents());
        assertEquals("3km SSE of The Geysers, California", json.get("features").get(null, 2).get("properties").get("place").getContents());
    }
    
    
    
    
}
