package data.tests;

import static org.junit.Assert.*;

import java.io.*;
import java.util.Scanner;

import org.junit.Test;

import core.cache.DataCacher;
import core.util.IOUtil;
import data.xml.XmlDataAccess;

public class XmlDataTest {

    @Test
    public void testXmlAccess() {
        DataCacher dc = DataCacher.defaultCacher();
        
        InputStream is = IOUtil.createInput(dc.resolvePath("http://xisbn.worldcat.org/webservices/xid/isbn/9780201038019?method=getMetadata&fl=*&format=xml"));
        XmlDataAccess xda = new XmlDataAccess(is);

    }

}
