package data.xml;

import static core.log.Errors.exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.parsers.*;
import org.xml.sax.SAXException;

import core.access.DataAccessException;
import core.access.IDataAccess;
import core.schema.ISchema;

public class XmlDataAccess implements IDataAccess {

    private XML xml;
    private ISchema schema;

    public XmlDataAccess(InputStream is) {
        try {
            xml = fixUp(new XML(is));
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw exception(DataAccessException.class, "da:construct", e.getMessage());
        }
        //System.out.println(xml);
    }

    private XmlDataAccess(XML xml) {
        this.xml = xml;
    }

    /**
     * Provide a predefined schema for this data set
     */
    public void setSchema(ISchema schema) {
        this.schema = schema;
    }
    
    @Override
    public ISchema getSchema() {
        if (this.schema == null) {
            this.schema = XmlSchemaBuilder.inferSchema(xml);
        } 
        return this.schema;
    }

    @Override
    public String getContents() {
        return xml.getContent("").trim();
    }

    @Override
    public IDataAccess get(String path, int i) {
        XML[] cs = xml.getChildren(path);
        return new XmlDataAccess(cs[i]);
    }

    @Override
    public IDataAccess get(String path) {
        return new XmlDataAccess(xml.getChild(path));
    }

    @Override
    public Stream<IDataAccess> getAll(String path) {
        XML[] cs = xml.getChildren(path);
        return IntStream.range(0, cs.length).mapToObj(i -> new XmlDataAccess(cs[i]));
    }


    private XML fixUp(XML xml) {
        attributesToSubtags(xml);
        return xml;
    }
    

    private void attributesToSubtags(XML xml) {
        for (XML child : xml.getChildren()) {
            attributesToSubtags(child);
        }
        if (xml.getAttributeCount() > 0) {
            if (xml.getChildCount() == 1 
                    && xml.getChild(0).getName().equals("#text")) {
                String content = xml.getContent();
                xml.removeChild(xml.getChild(0));
                XML valueNode = xml.addChild("value");
                valueNode.setContent(content);
            }
            for (String attrname : xml.listAttributes()) {
                String attrval = xml.getAttribute(attrname);
                xml.removeAttribute(attrname);
                XML subnode = xml.addChild(attrname);
                subnode.setContent(attrval);
            }
        }
    }


}
