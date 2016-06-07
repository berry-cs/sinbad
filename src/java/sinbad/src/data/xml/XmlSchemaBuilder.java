package data.xml;

import java.util.HashMap;
import java.util.Map.Entry;

import core.schema.*;

public class XmlSchemaBuilder {

	public static ISchema inferSchema(XML xml) {
	    //System.err.println("XmlSchemaBuilder::inferSchema(" + xml.getName() + ")");
		XML firstChild = firstNonemptyChild(xml);
		if (firstChild != null) {
			return inferComp(xml, false);
		} else {
			if (isEmptyXML(xml) || xml.getName().equals("#text")) {
				System.err.println("No data in XML (" + xml.getName() + ")");
				return null;
			} else {
				return new PrimSchema(xml.getName());
			}
		}
	}
	
	static CompSchema inferComp(XML xml, boolean addBasePath) {
	    HashMap<String, ISchema> hm = new HashMap<String, ISchema>();
	    
		//System.out.println("XmlSchemaBuilder::inferComp(" + xml.getName() + ")");
		for (XML t : xml.getChildren()) {  // for each subnode of xml
			if (!isEmptyXML(t) && !t.getName().equals("#text") 
					&& !hm.containsKey(t.getName())) {
				ISchema sf;  // inferred schema for t
				boolean isPrim = t.getChildCount() <= 1;   // looks like t subnode has no nested nodes
				boolean areMultiple = xml.getChildren(t.getName()).length > 1;  // there are several children like <t>...</t>
				
				if (isPrim) sf = new PrimSchema(t.getName());
				else sf = inferComp(t, !areMultiple);

				if (areMultiple) {  
					hm.put(t.getName(), new ListSchema(t.getName(), sf)); 
				} else {
					hm.put(t.getName(), sf);
				}
				
				if (xml.getChildren(t.getName()).length == xml.getChildren().length) {
				    // because all the children are the same tag
				    break;
				}
			}
		}	
		CompField[] flds = new CompField[hm.size()];
		int i = 0;
		for (Entry<String,ISchema> e : hm.entrySet()) {
		    flds[i++] = new CompField(e.getKey(), e.getValue());
		}
        CompSchema cs = new CompSchema(addBasePath ? xml.getName() : null, flds);
		return cs;
	}

	static XML firstNonemptyChild(XML xml) {
		XML[] children = xml.getChildren();
		for (int i = 0; i < children.length; i++) {
			XML c = children[i];
			if (!isEmptyXML(c)) return c;
		}
		return null;
	}
	
	static boolean isEmptyXML(XML node) {
		return node.getContent()==null || node.getContent().trim().equals("");
	}
}
