package core.ops;

import java.util.*;

import org.apache.commons.lang3.StringUtils;

import core.schema.CompSchema;
import core.schema.ISchema;
import core.schema.ISchemaVisitor;
import core.schema.ListSchema;
import core.schema.PrimSchema;

public class SchemaPrettyPrint implements ISchemaVisitor<String> {
    int indent;
    boolean indentFirst;
    ISchema parent;

    public SchemaPrettyPrint(int indent, boolean indentFirst) {
        this(indent, indentFirst, null);
    }
    
    public SchemaPrettyPrint(int indent, boolean indentFirst, ISchema parent) {
        super();
        this.indent = indent;
        this.indentFirst = indentFirst;
        this.parent = parent;
    }


    public String defaultVisit(ISchema sch) {
        throw new RuntimeException("Unhandled schema: " + sch);
    }

    @Override
    public String visit(PrimSchema sch) {
        String s = (indentFirst ? StringUtils.repeat(' ', indent) : "") + 
                ((sch.getDescription() == null) ? "*" : sch.getDescription());
        return s;
    }

    @Override
    public String visit(CompSchema sch) {
        String initSpaces = StringUtils.repeat(' ', indent);
        String header = "";
        if (parent != null && parent instanceof ListSchema) {
            header = "structures with fields:";
        } else {
            header = "a structure with fields:";
        }
        
        String s = (indentFirst ? initSpaces : "") + header + "\n" + initSpaces + "{\n";
        String spaces = StringUtils.repeat(' ', indent + 2);
        HashMap<String,ISchema> fieldMap = sch.getFieldMap();
        ArrayList<String> keys = new ArrayList<String>(fieldMap.keySet());
        Collections.sort(keys);
        for (String name : keys) {
                ISchema fsch  = fieldMap.get(name);
                if (fsch instanceof PrimSchema) {
                        String leader = spaces + name + " : ";
                        s += leader + fsch.apply(new SchemaPrettyPrint(leader.length(), false, fsch)) + "\n";
                }
        }
        for (String name : keys) {
            ISchema fsch = fieldMap.get(name);
                if (fsch instanceof CompSchema) {
                        String leader = spaces + name + " : ";
                        s += leader + fsch.apply(new SchemaPrettyPrint(leader.length(), false, fsch)) + "\n";
                }
        }
        for (String name : keys) {
            ISchema fsch = fieldMap.get(name);
                if (fsch instanceof ListSchema) {
                        String leader = spaces + name + " : ";
                        s += leader + fsch.apply(new SchemaPrettyPrint(leader.length(), false, fsch)) + "\n";
                }
        }
        s += initSpaces + "}";
        return s;
    }

    @Override
    public String visit(ListSchema sch) {
        ISchema eltSch = sch.getElementSchema();
        String s = (indentFirst ? StringUtils.repeat(' ', indent) : "") + "A list of:\n";
        s += eltSch.apply(new SchemaPrettyPrint(indent+2, true, sch));
        return s;
    }

  
    

}
