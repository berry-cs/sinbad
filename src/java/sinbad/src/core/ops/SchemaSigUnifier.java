package core.ops;

import java.util.*;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import core.schema.*;
import core.sig.*;


public class SchemaSigUnifier {
	/*
	 * If Sig type matches Schema type.... do
	 *  DataOpFactory.makeParse<T>(Schema){}
	 * 
	 */
    
    private static DataOpFactory opf = new DataOpFactory();
    
	/**
	 *  Builds a function that unifies data with a provided signature, by checking if the provided schema and signature
	 *  match in a valid way.
	 * @param schema - describes the type of the data, along with a path for where to find it
	 * @param sig - describes the type the user is requesting
	 * @return an object that can be applied to the actual data to find and parse the data into an object of the requested type.
	 */
	public static <T> IDataOp<T> unifyWith(ISchema schema ,ISig sig){
		return 
				schema.apply(new ISchemaVisitor<IDataOp<T>>() {
					public IDataOp<T> defaultVisit(ISchema df) {
						throw new RuntimeException("Unknown schema type");
					}

					/** Attempt to visit a schema of Primitive type, and unify it with the sig parameter.
					 * Builds
					 * @param f the schema to visit
					 * @return an IDataOp of a type <T> that will be applied to the data to fetch and parse it.
					 */
					@Override
					public IDataOp<T> visit(PrimSchema f) {
						return sig.apply(new ISigVisitor<IDataOp<T>>() {

							public IDataOp<T> defaultVisit(ISig s) {
								throw new RuntimeException("Unknown signature case");
							}

							@SuppressWarnings("unchecked") 
							@Override
							public IDataOp<T> visit(PrimSig s) {
								System.out.println("Attempting to unify prim schema with primSig");
								if(s == PrimSig.BOOLEAN_SIG)
									return (IDataOp<T>) opf.makeParseBool();
								else if(s == PrimSig.BYTE_SIG)
									return (IDataOp<T>) opf.makeParseByte();
								else if(s == PrimSig.CHAR_SIG)
									return (IDataOp<T>) opf.makeParseChar();
								else if(s == PrimSig.DOUBLE_SIG)
									return (IDataOp<T>) opf.makeParseDouble();
								else if(s == PrimSig.FLOAT_SIG)
									return (IDataOp<T>) opf.makeParseFloat();
								else if (s == PrimSig.INT_SIG) 
									return (IDataOp<T>)opf.makeParseInt();
								else if(s == PrimSig.LONG_SIG)
									return (IDataOp<T>) opf.makeParseLong();
								//else if(s == PrimSig.WILDCARD_SIG)
								//	return (IDataOp<T>) opf.makeParseWildCard();
								else if(s == PrimSig.STRING_SIG)
									return (IDataOp<T>)opf.makeParseString();
								else
									throw new RuntimeException("Unknown Primitive Signature Type");

							}

							@SuppressWarnings("unchecked")
                            @Override
							public IDataOp<T> visit(CompSig<?> sig) {

								System.out.println("Attempting to unify prim schema with compSig");
								if(sig.getFieldCount() != 1)
									throw new RuntimeException("Cannot unify a Primitive with a compound of more than 1 field.");
								else 
								{
									// TODO: findConstructor - could throw a SignatureUnificationException: what to do?
                                    ConstructorSigPair<?> csp = SigClassUnifier.findConstructor(sig);
									if (f.getPath() == null) {
										return opf.makeConstructor(csp.constructor,
												new IDataOp[]{unifyWith(schema, sig.getFieldSig(0))});
									} else {
										return opf.makeConstructor(csp.constructor, 
												new IDataOp[]{opf.makeSelectOp(unifyWith(schema, sig.getFieldSig(0)),f.getPath())});
									}
								}

							}

							@SuppressWarnings("unchecked")
                            @Override
							public IDataOp<T> visit(ListSig s) {
								try{
									System.out.println(schema.toString()+" "+ s.getElemType().toString());
									return (IDataOp<T>) opf.makeIndexAllOp(unifyWith(schema,s.getElemType()),f.getPath()); 
								}catch(RuntimeException e){
									e.printStackTrace();
								}
								throw new RuntimeException("Unable to unify Signature of type List<Compound> with Primitive");	
							}
						});

					}
					
					@Override
					public IDataOp<T> visit(CompSchema f) {
						return sig.apply(new ISigVisitor<IDataOp<T>>(){

							@Override
							public IDataOp<T> defaultVisit(ISig arg0) {
								throw new RuntimeException("Unknown signature case");
							}

							@Override
							public IDataOp<T> visit(PrimSig s) {
								System.out.println("Unifying Compound Schema with a prim sig");

								HashMap<String, ISchema> fieldMap = f.getFieldMap();
								if(fieldMap.size() == 1)
								{
									ISchema field0 = fieldMap.values().iterator().next();
									return unifyWith(field0,s);
								}else if(fieldMap.size() != 1)
									throw new RuntimeException("Cannont unify compound data of more than one field with a primitive.");
								else
									throw new RuntimeException("Unknown error occured unifying "+ f.toString() + " with " + s.toString());


								/*
								if(fieldMap.size() != 1){
									throw new RuntimeException("Cannot unify compound data of more than one field with a primitive");
								}/*else if(!fieldMap.containsKey(s.getName())){
									throw new RuntimeException(String.format("Name mismatch while attempting to unify %s with the primitive: %s",
																					fieldMap.values().iterator().next().getDescription(),s.getName()));
								}
								else if(true /*Field 0 schema matches Prim sig s )
									return opf.makeSelectOp( /*parse the primitive null, basePath + s.getName());
								else
									throw new RuntimeException("Unkown error occured while unifying a Compound with a primitive");
								 */
							}

                            @Override
							@SuppressWarnings("unchecked")
							public IDataOp<T> visit(CompSig<?> s) {
								System.out.println("Attempting to unify comp schema with compSig");
								return ruleCompComp(f, s);
							}
							
							

							@Override
							public IDataOp<T> visit(ListSig ls) {

								return (IDataOp<T>) opf.makeIndexAllOp(opf.makeSelectOp(unifyWith(schema,ls.getElemType()),""),f.getPath());  
							}});
					}

					@Override
					public IDataOp<T> visit(ListSchema f) {
						return sig.apply(new ISigVisitor<IDataOp<T>>(){

							@Override
							public IDataOp<T> defaultVisit(ISig arg0) {
								throw new RuntimeException();
							}

							@Override
							public IDataOp<T> visit(PrimSig s) {
                                // (LIST-STRIP) rule (???)
								return opf.makeIndexOp(unifyWith(f.getElementSchema(),s), f.getElementSchema().getPath(), 0);
							}

							@Override
							public IDataOp<T> visit(CompSig<?> s) {
								try{
								    // (LIST-STRIP) rule
									return opf.makeIndexOp(unifyWith(f.getElementSchema(),s), f.getPath(), 0);  
								}catch(RuntimeException e){
									e.printStackTrace();
									IDataOp<T>[] ops = new IDataOp[s.getFieldCount()];
									for(int i = 0; i < ops.length; i++){
										ops[i] = opf.makeIndexOp(unifyWith(f.getElementSchema(),s.getFieldSig(i)),f.getPath(),i);
									}
	                                ConstructorSigPair<?> csp = SigClassUnifier.findConstructor(s);
									return opf.makeConstructor(csp.constructor, ops);
								}
							}

							@Override
							public IDataOp<T> visit(ListSig s) {
							    return 
							    s.getElemType().apply(new ISigVisitor<IDataOp<T>>() {
							        // in all of these, 'ss' == s.getElemType()
							        
                                    // (LIST-LIST) rule
                                    public IDataOp<T> defaultVisit(ISig ss) {
                                        return (IDataOp<T>) opf.makeIndexAllOp(unifyWith(f.getElementSchema(), ss),
                                                f.getElementSchema().getPath());
                                    }
                                    public IDataOp<T> visit(PrimSig ss) { return defaultVisit(ss); }
                                    public IDataOp<T> visit(ListSig ss) { return defaultVisit(ss); }

                                    @SuppressWarnings({ "unchecked", "rawtypes" })
                                    public IDataOp<T> visit(CompSig<?> ss) {
                                        // see if all fields in the comp sig start with a common path prefix that matches
                                        // f.getElementSchema().getPath
                                        
                                        String pathPrefix = f.getElementSchema().getPath();
                                        if (pathPrefix == null) {
                                            return defaultVisit(ss);
                                        } else {
                                            // this is a new rule,  (LIST-PREFIX)
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
                                                return (IDataOp<T>) opf.makeIndexAllOp(unifyWith(f.getElementSchema(), newSig), pathPrefix);
                                            } else {
                                                throw new RuntimeException("What happened here?");
                                                // TODO: fix ^^^^
                                            }
                                        }
                                    }

							        
							    });
							    
								
							}});
					}

				});
	}
	
	
	@SuppressWarnings("unchecked")
	/**
	 * Implementation of the COMP-COMP rule
	 */
    protected static <T> IDataOp<T> ruleCompComp(CompSchema f, CompSig<?> s) {
	    IDataOp<T>[] listOps = new IDataOp[s.getFieldCount()];
        HashMap<String, ISchema> fieldMap = f.getFieldMap(); 

        for(int i = 0; i < s.getFieldCount(); i++){
            try {
                listOps[i] = unwrapAndUnify(fieldMap, s.getFieldName(i), s.getFieldSig(i));
            } catch(RuntimeException e){
                e.printStackTrace();
                throw new RuntimeException(String.format("Error unifiying the requested field \"%s\" with the data",s.getFieldName(i)));
            }
                
            if (listOps[i] == null) {
                throw new RuntimeException(String.format("The field requested \"%s\" was not found in the data.",s.getFieldName(i)));
            }
        }

        /* At this point we should have a list of IDataOp's to apply and build up the compound data with. */
        ConstructorSigPair<?> csp = SigClassUnifier.findConstructor(s);
        return opf.makeConstructor(csp.constructor, listOps);
	}
	
	/**
	 * Handle the unification of fields for the COMP-COMP rule
	 */
	protected static <T> IDataOp<T> unwrapAndUnify(HashMap<String, ISchema> fieldMap, String fieldName, ISig fieldSig) {
        // normal (COMP-COMP) rule behavior
        if (fieldMap.containsKey(fieldName)) {
            ISchema theFieldSchema = fieldMap.get(fieldName);
            IDataOp<T> fieldOp = unifyWith(theFieldSchema, fieldSig);
            return opf.makeSelectOp(fieldOp, theFieldSchema.getPath());
        } 
        // a new rule (COMP-FLATTEN) --- handle paths to nested structures
        else if ( fieldName.indexOf('/') >= 0) {
            String[] pieces = fieldName.split("/");
            System.out.println("pieces: " + pieces.length);
            for (int i = 1; i < pieces.length; i++) {
                String prefix = StringUtils.join(ArrayUtils.subarray(pieces, 0, i), "/");
                String rest = StringUtils.join(ArrayUtils.subarray(pieces, i, pieces.length), "/");
                System.out.println(prefix + "--" + rest);
                if (fieldMap.containsKey(prefix)) {
                    ISchema theFieldSchema = fieldMap.get(prefix);
                    IDataOp<T> theOp =
                            theFieldSchema.apply(new ISchemaVisitor<IDataOp<T>>() {
                                public IDataOp<T> defaultVisit(ISchema s) { return null; }
                                public IDataOp<T> visit(PrimSchema s) { return null; }
                                public IDataOp<T> visit(ListSchema s) { return null; }
                                public IDataOp<T> visit(CompSchema s) {
                                    return unwrapAndUnify(s.getFieldMap(), rest, fieldSig);
                                }
                            });
                    if (theOp != null) return opf.makeSelectOp(theOp, theFieldSchema.getPath());
                }                                       
            }
        }
        
        return null;    // means COMP-COMP failed
    }
}