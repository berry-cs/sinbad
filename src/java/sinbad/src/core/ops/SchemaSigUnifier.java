package core.ops;

import java.util.*;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import core.schema.*;
import core.sig.*;


@SuppressWarnings("unchecked")
public class SchemaSigUnifier {

	private static DataOpFactory opf = new DataOpFactory();

	
	public static <T> IDataOp<T> unifyWith(PrimSchema sch, PrimSig sig) {
	    if (sig == PrimSig.BOOLEAN_SIG)
	        return (IDataOp<T>) opf.makeParseBool();
	    else if (sig == PrimSig.BYTE_SIG)
	        return (IDataOp<T>) opf.makeParseByte();
	    else if (sig == PrimSig.CHAR_SIG)
	        return (IDataOp<T>) opf.makeParseChar();
	    else if (sig == PrimSig.DOUBLE_SIG)
	        return (IDataOp<T>) opf.makeParseDouble();
	    else if (sig == PrimSig.FLOAT_SIG)
	        return (IDataOp<T>) opf.makeParseFloat();
	    else if (sig == PrimSig.INT_SIG) 
	        return (IDataOp<T>)opf.makeParseInt();
	    else if (sig == PrimSig.LONG_SIG)
            return (IDataOp<T>) opf.makeParseLong();
        else if(sig == PrimSig.STRING_SIG)
            return (IDataOp<T>)opf.makeParseString();
        else  
            throw new RuntimeException("Unknown Primitive Signature Type"); 
	    // there isn't one for WILDCARD_SIG because that should never occur at this point
	}
	
	
    public static <T> IDataOp<T> unifyWith(PrimSchema sch, CompSig<?> sig) {
	    if(sig.getFieldCount() != 1)
            throw new RuntimeException("Cannot unify a Primitive with a compound of more than 1 field.");
        else 
        {
            // TODO: findConstructor - could throw a SignatureUnificationException: what to do?
            ConstructorSigPair<?> csp = SigClassUnifier.findConstructor(sig);
            if (sch.getPath() == null) {
                return opf.makeConstructor(csp.constructor,
                        new IDataOp[]{unifyWith(sch, sig.getFieldSig(0))});  //unify the primitive schema with the first field of the comp sig
            } else {
                return opf.makeConstructor(csp.constructor, 
                        new IDataOp[]{opf.makeSelectOp(unifyWith(sch, sig.getFieldSig(0)), sch.getPath())});
            }
        }
	}
    
    
    public static <T> IDataOp<T> unifyWith(PrimSchema sch, ListSig sig) {
        try{
            //System.out.println(sch.toString()+" "+ sig.getElemType().toString());
            return (IDataOp<T>) opf.makeWrapOp(unifyWith(sch,sig.getElemType()),sch.getPath()); 
        }catch(RuntimeException e){
            e.printStackTrace();
        }
        throw new RuntimeException("Unable to unify Signature of type List<Compound> with Primitive");  
    }

	
    public static <T> IDataOp<T> unifyWith(CompSchema sch, PrimSig sig) {
        HashMap<String, ISchema> fieldMap = sch.getFieldMap();
        if (fieldMap.size() == 1)
        {
            ISchema field0 = fieldMap.values().iterator().next();
            return unifyWith(field0,sig);
        } else { 
            throw new RuntimeException("Cannot unify compound data of more than one field with a primitive.");
        }
    }
    
    
    public static <T> IDataOp<T> unifyWith(CompSchema sch, CompSig<?> sig) {
        //System.err.println(sch.toString(true) + "\n" + sig);
        
        IDataOp<T>[] listOps = new IDataOp[sig.getFieldCount()];

        for(int i = 0; i < sig.getFieldCount(); i++){
            try {
                listOps[i] = sch.apply(new FieldSelectUnifier<T>(sig.getFieldName(i), sig.getFieldSig(i)));
            } catch(RuntimeException e){
                e.printStackTrace();
                throw new RuntimeException(String.format("Error unifiying the requested field \"%s\" with the data",sig.getFieldName(i)));
            }
                
            if (listOps[i] == null) {
                throw new RuntimeException(String.format("The field requested \"%s\" was not found in the data.",sig.getFieldName(i)));
            }
        }

        /* At this point we should have a list of IDataOp's to apply and build up the compound data with. */
        ConstructorSigPair<?> csp = SigClassUnifier.findConstructor(sig);
        return opf.makeConstructor(csp.constructor, listOps);
    }

    
    public static <T> IDataOp<T> unifyWith(CompSchema sch, ListSig sig) {
        //System.err.println("running ruleCompList");
        
        ISig eltSig = sig.getElemType();
        //return (IDataOp<T>) opf.makeWrapOp(unifyWith(sch,sig.getElemType()),sch.getPath()); 
        
        IDataOp<T> dop
        = eltSig.apply(new ISigVisitor<IDataOp<T>>() {
            public IDataOp<T> visit(PrimSig s) { return defaultVisit(s); }
            public IDataOp<T> visit(ListSig s) { return defaultVisit(s); }
            
            public IDataOp<T> defaultVisit(ISig s) {
                return (IDataOp<T>) opf.makeWrapOp(unifyWith(sch,sig.getElemType()),sch.getPath()); 
                /*
                IDataOp<T> dop
                    = (IDataOp<T>) opf.makeIndexAllOp(
                            opf.makeSelectOp(unifyWith(sch, sig.getElemType()),""),
                            sch.getPath());
                return dop;
                */
            }

            public IDataOp<T> visit(CompSig<?> cs) { // here the eltSig is this 'cs'
                // TODO: maybe need to handle single-field case specially? see XMLInstantiator in old version...
                String[] csFlds = new String[cs.getFieldCount()];
                for (int i = 0; i < csFlds.length; i++) csFlds[i] = cs.getFieldName(i);
                
                String commonPrefix = longestCommonPrefix(csFlds);
                if (commonPrefix == null || (csFlds.length > 1 && !commonPrefix.endsWith("/")))
                    return defaultVisit(cs);
                
                String[] pieces = commonPrefix.split("/");
                for (int i = 0; i < pieces.length; i++) {
                    // try longer and longer pieces of the prefix...
                    String prefix = StringUtils.join(ArrayUtils.subarray(pieces, 0, i+1), "/");

                    if (sch.getFieldMap().containsKey(prefix)) {
                        ISchema fldSchema = sch.getFieldMap().get(prefix);
                        CompSig<?> newSig = cs.trimPrefix(prefix + "/");
                        IDataOp<T> fld_dop = unifyWith(fldSchema, new ListSig(newSig));
                        //System.err.printf("fld_dop: %s          (fldSchema: %s)\n", fld_dop, fldSchema.toString(true));
                        if (fld_dop != null) {
                            return fld_dop;
                            
                            //IDataOp<T> dop = null;
                            // TODO: this is very messy -- need to carefully reexamine the interaction of this rule with others... 
                            //if (fldSchema instanceof ListSchema && ((ListSchema)fldSchema).getElementSchema().getPath() == null) {
                            //    dop = (IDataOp<T>)opf.makeIndexAllOp(fld_dop, fldSchema.getPath());
                            //} else {
                            //    dop = opf.makeSelectOp(fld_dop, fldSchema.getPath());
                            //}
                            // return dop;
                        }
                    }
                }
                
                return defaultVisit(cs);
            }
          });
        
        //System.err.println("Got dop: " + dop);
        return dop;
    }
    
    
    public static <T> IDataOp<T> unifyWith(ListSchema sch, PrimSig sig) {
        return opf.makeIndexOp(unifyWith(sch.getElementSchema(),sig), sch.getPath(), 0);
    }
    
    
    public static <T> IDataOp<T> unifyWith(ListSchema sch, CompSig<?> sig) {
        try{
            // (LIST-STRIP) rule
            return opf.makeIndexOp(unifyWith(sch.getElementSchema(),sig), sch.getPath(), 0);  
        }catch(RuntimeException e){
            e.printStackTrace();
            IDataOp<T>[] ops = new IDataOp[sig.getFieldCount()];
            for(int i = 0; i < ops.length; i++){
                ops[i] = opf.makeIndexOp(unifyWith(sch.getElementSchema(),sig.getFieldSig(i)),sch.getPath(),i);
            }
            ConstructorSigPair<?> csp = SigClassUnifier.findConstructor(sig);
            return opf.makeConstructor(csp.constructor, ops);
        }
    }
    
    
    public static <T> IDataOp<T> unifyWith(ListSchema sch, ListSig sig) {
        return 
                sig.getElemType().apply(new ISigVisitor<IDataOp<T>>() {
                    // in all of these, 'ss' == s.getElemType()

                    // (LIST-LIST) rule -> this is for a list of things that are all the same object
                    @SuppressWarnings("unchecked")
                    public IDataOp<T> defaultVisit(ISig ss) {
                        if (sch.getElementSchema().getPath() == null) {
                            //return (IDataOp<T>) opf.makeIndexAllOp(unifyWith(f.getElementSchema(), ss),
                            //       f.getElementSchema().getPath());
                            return (IDataOp<T>) opf.makeIndexAllOp(unifyWith(sch.getElementSchema(), ss),sch.getPath()); 
                        } else {
                            return (IDataOp<T>) opf.makeIndexAllOp(unifyWith(sch.getElementSchema(), ss),
                                    sch.getPath());
                        }
                    }
                    public IDataOp<T> visit(PrimSig ss) { return defaultVisit(ss); }
                    public IDataOp<T> visit(ListSig ss) { return defaultVisit(ss); }

                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    public IDataOp<T> visit(CompSig<?> ss) {
                        // see if all fields in the comp sig start with a common path prefix that matches
                        // f.getElementSchema().getPath

                        String pathPrefix = sch.getElementSchema().getPath(); //the path to the list base??
                        if (pathPrefix == null) {
                            return defaultVisit(ss);
                        } else {
                            // this is a new rule,  (LIST-PREFIX) this is for a list of things that are PARAMETERS OF A CONSTRUCTOR for a particular object
                            int pplen = pathPrefix.length() + 1;
                            boolean commonPrefix = true;
                            ArgSpec[] newArgs = new ArgSpec[ss.getFieldCount()];
                            for (int i = 0; i < ss.getFieldCount(); i++) {
                                String iname = ss.getFieldName(i);
                                if (!iname.startsWith(pathPrefix + "/")) {
                                    commonPrefix = false;
                                    break;
                                }
                                newArgs[i] = new ArgSpec(iname.substring(pplen), ss.getFieldSig(i));
                            }
                            if (commonPrefix) {
                                CompSig<?> newSig = new CompSig(ss.getAssociatedClass(), newArgs);
                                return (IDataOp<T>) opf.makeIndexAllOp(unifyWith(sch.getElementSchema(), newSig), pathPrefix);
                            } else {
                                return defaultVisit(ss);
                                // TODO: fix ^^^^
                            }
                        }
                    }


                });

    }
    
    
    
	/**
	 *  Builds a function that unifies data with a provided signature, by checking if the provided schema and signature
	 *  match in a valid way.
	 * @param schema - describes the type of the data, along with a path for where to find it
	 * @param sig - describes the type the user is requesting
	 * @return an object that can be applied to the actual data to find and parse the data into an object of the requested type.
	 */
	public static <T> IDataOp<T> unifyWith(ISchema schema, ISig sig){
		return 
				schema.apply(new ISchemaVisitor<IDataOp<T>>() {
					public IDataOp<T> defaultVisit(ISchema df) {
						throw new RuntimeException("Unknown schema type");
					}

					public IDataOp<T> visit(PrimSchema sch) {
						return sig.apply(new ISigVisitor<IDataOp<T>>() {
                            public IDataOp<T> visit(PrimSig sig) { return SchemaSigUnifier.unifyWith(sch, sig); }
							public IDataOp<T> visit(CompSig<?> sig) { return SchemaSigUnifier.unifyWith(sch, sig); }
							public IDataOp<T> visit(ListSig sig) { return SchemaSigUnifier.unifyWith(sch, sig); }
                            public IDataOp<T> defaultVisit(ISig sig) { throw new RuntimeException("Unknown signature case"); }
						});
					}

					public IDataOp<T> visit(CompSchema sch) {
					    return sig.apply(new ISigVisitor<IDataOp<T>>(){
					        public IDataOp<T> visit(PrimSig sig) { return SchemaSigUnifier.unifyWith(sch, sig); }
					        public IDataOp<T> visit(CompSig<?> sig) { return SchemaSigUnifier.unifyWith(sch, sig); }
					        public IDataOp<T> visit(ListSig sig) { return SchemaSigUnifier.unifyWith(sch, sig); }
					        public IDataOp<T> defaultVisit(ISig sig) { throw new RuntimeException("Unknown signature case");}
					    });
					}

					public IDataOp<T> visit(ListSchema sch) {
						return sig.apply(new ISigVisitor<IDataOp<T>>(){
							public IDataOp<T> visit(PrimSig sig) { return SchemaSigUnifier.unifyWith(sch, sig); }
							public IDataOp<T> visit(CompSig<?> sig) { return SchemaSigUnifier.unifyWith(sch, sig); }
							public IDataOp<T> visit(ListSig sig) { return SchemaSigUnifier.unifyWith(sch, sig); }
                            public IDataOp<T> defaultVisit(ISig sig) {throw new RuntimeException();}
						});
					}
				});
	}
	
	
    protected static String longestCommonPrefix(String[] strings) {
        if (strings.length <= 0) {
            return null;
        }
        
        for (int prefixLen = 0; prefixLen < strings[0].length(); prefixLen++) {
            char c = strings[0].charAt(prefixLen);
            for (int i = 1; i < strings.length; i++) {
                if ( prefixLen >= strings[i].length() ||
                     strings[i].charAt(prefixLen) != c ) {
                    // Mismatch found
                    return strings[i].substring(0, prefixLen);
                }
            }
        }
        return strings[0];
    }    
	
    
	/**
	 * Handle the unification of fields for the COMP-COMP rule
	 */
	protected static class FieldSelectUnifier<T> implements ISchemaVisitor<IDataOp<T>> {
	    String fieldName;  // corresponding to the signature
	    ISig fieldSig;

        FieldSelectUnifier(String fieldName, ISig fieldSig) {
            this.fieldName = fieldName;
            this.fieldSig = fieldSig;
        }

        public IDataOp<T> defaultVisit(ISchema s) { return null; }
        public IDataOp<T> visit(PrimSchema s) { return defaultVisit(s); }

        public IDataOp<T> visit(CompSchema s) {
            HashMap<String, ISchema> fieldMap = s.getFieldMap();
            
            // (BASE) rule
            if (fieldMap.containsKey(fieldName)) {
                ISchema theFieldSchema = fieldMap.get(fieldName);
                IDataOp<T> fieldOp = unifyWith(theFieldSchema, fieldSig);
                return opf.makeSelectOp(fieldOp, theFieldSchema.getPath());
            } else  // (PREFIX) rule
                if ( fieldName.indexOf('/') >= 0) {
                String[] pieces = fieldName.split("/");
                //System.out.println("pieces: " + pieces.length);
                for (int i = 1; i < pieces.length; i++) {
                    String prefix = StringUtils.join(ArrayUtils.subarray(pieces, 0, i), "/");
                    String rest = StringUtils.join(ArrayUtils.subarray(pieces, i, pieces.length), "/");
                    //System.out.println(prefix + "--" + rest);
                    if (fieldMap.containsKey(prefix)) {
                        ISchema theFieldSchema = fieldMap.get(prefix);
                        IDataOp<T> theOp = theFieldSchema.apply(new FieldSelectUnifier<T>(rest, fieldSig));
                        if (theOp != null) {
                            return theFieldSchema.apply(new ISchemaVisitor<IDataOp<T>>() {
                                public IDataOp<T> visit(PrimSchema s) { return defaultVisit(s); }
                                public IDataOp<T> visit(CompSchema s) { return defaultVisit(s); }
                                public IDataOp<T> defaultVisit(ISchema s) { 
                                    return opf.makeSelectOp(theOp, theFieldSchema.getPath());
                                }
                                public IDataOp<T> visit(ListSchema s) {
                                    return theOp;
                                }
                            });
                        }
                        else {
                            return null;
                        }
                    }                                       
                }
                return null;
                }

            return null;
        }

        public IDataOp<T> visit(ListSchema sch) {
            return fieldSig.apply(new ISigVisitor<IDataOp<T>>() {
                public IDataOp<T> visit(PrimSig s) { return defaultVisit(s); }
                public IDataOp<T> visit(CompSig<?> s) { return defaultVisit(s); }

                // (LIST_UNWRAP) rule
                public IDataOp<T> defaultVisit(ISig s) { 
                    ISchema eltSchema = sch.getElementSchema();
                    IDataOp<T> theOp = eltSchema.apply(FieldSelectUnifier.this);
                    if (theOp != null) {
                        return opf.makeIndexOp(theOp, sch.getPath(), 0);
                    } else {
                        return null;
                    }
                }

                // (LIST) rule
                @SuppressWarnings("unchecked")
                public IDataOp<T> visit(ListSig s) {
                    ISchema eltSchema = sch.getElementSchema();
                    ISig eltSig = s.getElemType();
                    IDataOp<T> theOp = eltSchema.apply(new FieldSelectUnifier<T>(fieldName, eltSig));
                    if (theOp != null) {
                        return (IDataOp<T>) opf.makeIndexAllOp(theOp, sch.getPath());
                    } else {
                        return null;
                    }
                }
                
            });
        }
	}
	
}