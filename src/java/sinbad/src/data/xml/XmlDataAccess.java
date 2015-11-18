package data.xml;

import static core.log.Errors.exception;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import core.access.DataAccessException;
import core.access.FailAccess;

public class XmlDataAccess extends FailAccess {

    private XML xml;
    
    public XmlDataAccess(InputStream is) {
        try {
            xml = new XML(is);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw exception(DataAccessException.class, "da:construct", e.getMessage());
        }
        System.out.println(xml.format(2));
    }
    
    
    
    private Document loadDocument(InputStream is) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            // Prevent 503 errors from www.w3.org
            factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (IllegalArgumentException e) {
            // ignore this; Android doesn't like it
        }

        Document document;
        try {
            factory.setExpandEntityReferences(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(is));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw exception(DataAccessException.class, "da:construct", e.getMessage());
        }
        return document;
    }
}
