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
							@Override
							public IDataOp<T> defaultVisit(ISig s) {throw new RuntimeException("Unknown signature case");}

							@Override
							public IDataOp<T> visit(PrimSig sig) {return rulePrimPrim(sig);}

							@Override
							public IDataOp<T> visit(CompSig<?> sig) {return rulePrimComp(f,sig);}

							@Override
							public IDataOp<T> visit(ListSig sig) {return rulePrimList(f,sig);}
						});
					}

					@Override
					public IDataOp<T> visit(CompSchema f) {
						return sig.apply(new ISigVisitor<IDataOp<T>>(){
							@Override
							public IDataOp<T> defaultVisit(ISig arg0) {throw new RuntimeException("Unknown signature case");}

							@Override
							public IDataOp<T> visit(PrimSig s) {return ruleCompPrim(f,s);}

							@Override
							public IDataOp<T> visit(CompSig<?> s) {return ruleCompComp(f,s);}

							@Override
							public IDataOp<T> visit(ListSig ls) {return ruleCompList(f,ls);}
						});
					}

					@Override
					public IDataOp<T> visit(ListSchema f) {
						return sig.apply(new ISigVisitor<IDataOp<T>>(){
							@Override
							public IDataOp<T> defaultVisit(ISig arg0) {throw new RuntimeException();}

							@Override
							public IDataOp<T> visit(PrimSig s) {return ruleListStrip(f,s);}

							@Override
							public IDataOp<T> visit(CompSig<?> s) {return ruleListStrip(f,s);}

							@Override
							public IDataOp<T> visit(ListSig s) {return ruleListList(f,s);}

						});
					}
				});
	}


	/** Implementation of the Prim || Prim Rule
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected static <T> IDataOp<T> rulePrimPrim(PrimSig s) {
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

	/**
	 * Implementation of the Prim || Comp Rule
	 * @param schema
	 * @param sig
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static <T> IDataOp<T> rulePrimComp(PrimSchema schema, CompSig<?> sig) {
		if(sig.getFieldCount() != 1)
			throw new RuntimeException("Cannot unify a Primitive with a compound of more than 1 field.");
		else 
		{
			// TODO: findConstructor - could throw a SignatureUnificationException: what to do?
			ConstructorSigPair<?> csp = SigClassUnifier.findConstructor(sig);
			if (schema.getPath() == null) {
				return opf.makeConstructor(csp.constructor,
						new IDataOp[]{unifyWith(schema, sig.getFieldSig(0))});  //unify the primitive schema with the first field of the comp sig
			} else {
				return opf.makeConstructor(csp.constructor, 
						new IDataOp[]{opf.makeSelectOp(unifyWith(schema, sig.getFieldSig(0)),schema.getPath())});
			}
		}
	}

	/**
	 * Implementation of the Prim || List Rule
	 * @param schema
	 * @param sig
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static <T> IDataOp<T> rulePrimList(PrimSchema schema, ListSig sig) {
		try{
			System.out.println(schema.toString()+" "+ sig.getElemType().toString());
			return (IDataOp<T>) opf.makeIndexAllOp(unifyWith(schema,sig.getElemType()),schema.getPath()); 
		}catch(RuntimeException e){
			e.printStackTrace();
		}
		throw new RuntimeException("Unable to unify Signature of type List<Compound> with Primitive");	
	}

	/**
	 * Implementation of the Comp || Prim Rule
	 * @param schema
	 * @param s
	 * @return
	 */
	protected static <T> IDataOp<T> ruleCompPrim(CompSchema schema, PrimSig s) {
		HashMap<String, ISchema> fieldMap = schema.getFieldMap();
		if(fieldMap.size() == 1)
		{
			ISchema field0 = fieldMap.values().iterator().next();
			return unifyWith(field0,s);
		}else if(fieldMap.size() != 1)
			throw new RuntimeException("Cannont unify compound data of more than one field with a primitive.");
		else // TODO :: I don't think this else can happen, fieldMap will only ever be one of the first two cases
			throw new RuntimeException("Unknown error occured unifying "+ schema.toString() + " with " + s.toString());

	}

	/**
	 * Implementation of the Comp || Comp Rule
	 */
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
	 * Handle the implementation of Comp || List rule
	 */
	@SuppressWarnings("unchecked")
	protected static <T> IDataOp<T> ruleCompList(CompSchema schema,ListSig ls) {
		return (IDataOp<T>) opf.makeIndexAllOp(opf.makeSelectOp(unifyWith(schema,ls.getElemType()),""),schema.getPath());
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

	/**
	 * Implementation of the ListStrip rule for Primitives
	 * @param f
	 * @param s
	 * @return
	 */
	protected static <T> IDataOp<T> ruleListStrip(ListSchema f, PrimSig s) {
		return opf.makeSelectOp(opf.makeSelectOp(unifyWith(f.getElementSchema(),s),f.getElementSchema().getPath()), f.getPath());
	}

	/**
	 * Implementation of the ListStrip rule for Primitives
	 * @param f
	 * @param s
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static <T> IDataOp<T> ruleListStrip(ListSchema f, CompSig<?> s) {
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
	/**
	 * Implementation of the List || List Rule
	 */
	protected static <T> IDataOp<T> ruleListList(ListSchema f, ListSig s) {
		return 
				s.getElemType().apply(new ISigVisitor<IDataOp<T>>() {
					// in all of these, 'ss' == s.getElemType()

					// (LIST-LIST) rule -> this is for a list of things that are all the same object
					@SuppressWarnings("unchecked")
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

						String pathPrefix = f.getPath(); //the path to the list base??
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
								return (IDataOp<T>) opf.makeIndexAllOp(unifyWith(f.getElementSchema(), newSig), pathPrefix);
							} else {
								throw new RuntimeException("What happened here?");
								// TODO: fix ^^^^
							}
						}
					}


				});

	}
}