package core.ops;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import core.schema.CompSchema;
import core.schema.ISchema;
import core.schema.ISchemaVisitor;
import core.schema.ListSchema;
import core.schema.PrimSchema;
import core.sig.CompSig;
import core.sig.ISig;
import core.sig.ISigVisitor;
import core.sig.ListSig;
import core.sig.PrimSig;


public class SchemaSigUnifier {
	/*
	 * If Sig type matches Schema type.... do
	 *  DataOpFactory.makeParse<T>(Schema){}
	 * 
	 */
	/**
	 *  Builds a function that unifies data with a provided signature, by checking if the provided schema and signature
	 *  match in a valid way.
	 * @param schema - describes the type of the data, along with a path for where to find it
	 * @param sig - describes the type the user is requesting
	 * @return an object that can be applied to the actual data to find and parse the data into an object of the requested type.
	 */
	public <T> IDataOp<T> unifyWith(ISchema schema ,ISig sig){
		DataOpFactory opf = new DataOpFactory();

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
								else if(s == PrimSig.WILDCARD_SIG)
									return (IDataOp<T>) opf.makeParseWildCard();
								else if(s == PrimSig.STRING_SIG)
									return (IDataOp<T>)opf.makeParseString();
								else
									throw new RuntimeException("Unknown Primitive Signature Type");

							}

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
							public IDataOp<T> visit(CompSig<?> s) {
								System.out.println("Attempting to unify comp schema with compSig");
								//	int max = s.getFieldCount();
								ArrayList<IDataOp<T>> listOps = new ArrayList<IDataOp<T>>();
								for(int i = 0; i < s.getFieldCount(); i++){
									String fieldName = s.getFieldName(i);
									ISchema theField;
									HashMap<String, ISchema> fieldMap = f.getFieldMap(); //TODO
									if(fieldMap.containsKey(fieldName)){
										theField = fieldMap.get(fieldName);
										System.out.println("Found field "+fieldName);
										try{
											IDataOp<T> thisOp = unifyWith(theField,s.getFieldSig(i));
											listOps.add(opf.makeSelectOp(thisOp,fieldName));
										}catch(RuntimeException e){
											e.printStackTrace();
											throw new RuntimeException(String.format("Error unifiying the requested field \"%s\" with the data",fieldName));
										}
									}else
										throw new RuntimeException(String.format("The field requested \"%s\" was not found in the data.",fieldName));
								}

								/* At this point we should have a list of IDataOp's to apply and build up the compound data with. */

								ConstructorSigPair<?> csp = SigClassUnifier.findConstructor(s);
								return opf.makeConstructor(csp.constructor, (IDataOp<T>[]) listOps.toArray(new IDataOp[]{}));
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
								return opf.makeSelectOp(unifyWith(f.getElementSchema(),s), f.getPath());
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
								return (IDataOp<T>) opf.makeIndexAllOp(opf.makeSelectOp(unifyWith(f.getElementSchema(), s.getElemType()),f.getPath()),null);
							}});
					}

				});
	}
}