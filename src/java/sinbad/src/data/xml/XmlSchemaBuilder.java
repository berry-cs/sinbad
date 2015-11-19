package data.xml;

import java.util.HashMap;
import java.util.Map.Entry;

import core.schema.*;

public class XmlSchemaBuilder {

	public static ISchema inferSchema(XML xml) {
		XML firstChild = firstNonemptyChild(xml);
		if (firstChild != null) {
			return inferComp(xml);
		} else {
			if (isEmptyXML(xml) || xml.getName().equals("#text")) {
				System.err.println("No data in XML (" + xml.getName() + ")");
				return null;
			} else {
				return new PrimSchema(xml.getName());
			}
		}
	}
	
	static CompSchema inferComp(XML xml) {
	    HashMap<String, ISchema> hm = new HashMap<String, ISchema>();
	    
		//System.out.println("inferCompField:\n" + xml);
		for (XML t : xml.getChildren()) {  // for each subnode of xml
			if (!isEmptyXML(t) && !t.getName().equals("#text") 
					&& !hm.containsKey(t.getName())) {
				ISchema sf;  // inferred schema for t
				boolean isPrim = t.getChildCount() <= 1;
				   // looks like t subnode has no nested nodes
				
				if (isPrim) sf = new PrimSchema(t.getName());
				else sf = inferComp(t);

				if (xml.getChildren(t.getName()).length > 1) {  // there are several children like <t>...</t>
					hm.put(t.getName(), new ListSchema(xml.getName(), t.getName(), sf)); // TODO: should basepath be xml.getName() or t.getName() ?
				} else {
					hm.put(t.getName(), sf);
				}
			}
		}	
		CompField[] flds = new CompField[hm.size()];
		int i = 0;
		for (Entry<String,ISchema> e : hm.entrySet()) {
		    flds[i++] = new CompField(e.getKey(), e.getValue());
		}
        CompSchema cs = new CompSchema(xml.getName(), flds);
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
