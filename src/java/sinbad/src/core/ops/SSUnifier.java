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


public class SSUnifier {
	/*
	 * If Sig type matches Schema type.... do
	 *  DataOpFactory.makeParse<T>(Schema){}
	 * 
	 */
	public <T> IDataOp<T> unifyWith(ISchema schema ,ISig sig){
		DataOpFactory opf = new DataOpFactory();

		return 
				schema.apply(new ISchemaVisitor<IDataOp<T>>() {
					public IDataOp<T> defaultVisit(ISchema df) {
						throw new RuntimeException("Unknown schema type");
					}

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
										//TODO fix findConstructor
									if (f.getPath() == null) {
										return opf.makeConstructor(sig.findConstructor(), new IDataOp[]{unifyWith(schema, sig.getFieldSig(0))});
									} else {
										return opf.makeConstructor(sig.findConstructor(), new IDataOp[]{opf.makeSelectOp(unifyWith(schema, sig.getFieldSig(0)),
												f.getPath())});
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
								
								HashMap<String, ISchema> fieldMap = f.getFieldMap(); //TODO
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

								return opf.makeConstructor(s.findConstructor(), (IDataOp<T>[]) listOps.toArray(new IDataOp[]{}));
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
								return opf.makeSelectOp(unifyWith(f.getElementSchema(),s), f.getPath());
								}catch(RuntimeException e){
									e.printStackTrace();
									IDataOp<T>[] ops = new IDataOp[s.getFieldCount()+1];
									for(int i = 0; i < ops.length; i++){
										ops[i] = unifyWith(f.getElementSchema(),s.getFieldSig(i));
									}
									return opf.makeConstructor(s.findConstructor(),ops);
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