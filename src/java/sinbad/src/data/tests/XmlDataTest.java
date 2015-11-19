package data.tests;

import static org.junit.Assert.*;

import java.io.*;
import java.util.Scanner;

import org.junit.Test;

import core.cache.DataCacher;
import core.data.DataSource;
import core.ops.SchemaSigUnifier;
import core.ops.SigClassUnifier;
import core.schema.*;
import core.sig.*;
import core.util.IOUtil;
import data.xml.XmlDataAccess;

public class XmlDataTest {

    @Test
    public void testXmlAccess() {
        DataCacher dc = DataCacher.defaultCacher();
        
        InputStream is = IOUtil.createInput(dc.resolvePath("http://xisbn.worldcat.org/webservices/xid/isbn/9780201038019?method=getMetadata&fl=*&format=xml"));
        XmlDataAccess xda = new XmlDataAccess(is);
        System.out.println(xda.get("isbn/author").getContents());
        System.out.println(xda.getSchema().toString(true));
        
        String isbn = "9780201038019"; // in.nextLine();
        
        DataSource ds = DataSource.connectAs("xml", "http://xisbn.worldcat.org/webservices/xid/isbn/" + isbn);
        ds.set("method", "getMetadata").set("fl", "*").set("format", "xml").load();
        //ds.printUsageString();
        
        ISchema sch = ds.getDataAccess().getSchema();
        ISig sig = SigUtils.buildCompSig(String.class, "isbn/title").apply(new SigClassUnifier(String.class));
        System.out.println("sch: " + sch.toString(true));
        System.out.println("sig: " + sig);
        System.out.println();
        System.out.println(" op: " + SchemaSigUnifier.unifyWith(sch, sig));
        
        String title = ds.fetchString("isbn/title");
        String author = ds.fetchString("isbn/author");
        int year = ds.fetchInt("isbn/year");
        
        System.out.println(isbn + ": " + title + ", " + author + ", " + year + ".");

    }

    
    
    
}
