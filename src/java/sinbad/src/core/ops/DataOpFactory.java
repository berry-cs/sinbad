package core.ops;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import core.access.IDataAccess;
import core.util.ProcessingDetector;

/**
 * An object for making new IDataOp objects, used to create functions that index, select, and parse the data as
 * instructed from the unification step between signature and schema.
 * 
 * Each of the operations produced also includes a default value for when the data piece is missing. 
 * i.e. if in the acutal data if an empty string value is present, and you requested an integer, you will parse a 0.
 */
public class DataOpFactory {

	/**
	 * Makes an IDataOp object that can parse an integer from a string
	 * @return the IDataOp created, for parsing integers from data
	 */
	public IDataOp<Integer> makeParseInt() {
		return new IDataOp<Integer>() {
			public Integer apply(IDataAccess d) {
				String s = d.getContents();
				if (s == null || s.equals("")) return 0;
				else return Integer.parseInt(s);
			}		
			
			public String toString() { return "parseInt"; }
            public final String name = "parseInt";
		};
	}
	
	/**
	 * Makes an IDataOp object that can parse a string
	 * @return the IDataOp created, for reading a string
	 */
	public IDataOp<String> makeParseString() {
		return new IDataOp<String>() {
			public String apply(IDataAccess d) {
				return d.getContents();
			}
            
            public String toString() { return "parseString"; }
            public final String name = "parseString";
		};
	}

	/**
	 * Produces an operation that when applied to the data will attempt to parse a boolean value from the string
	 * @return an IDataOp object that can be applied to an IDataAccess
	 */
	public IDataOp<Boolean> makeParseBool(){
		return new IDataOp<Boolean>(){
			public Boolean apply(IDataAccess d){
				String s = d.getContents().toLowerCase();
                boolean b = false;
				if(s == null || s.equals(""))
					return false;
				else if (s.equals("true") || s.equals("1") || s.equals("y") || s.equals("yes"))
		            b = true;
		        else if (s.equals("false") || s.equals("0") || s.equals("n") || s.equals("no"))
		            b = false;
		        else 
					b = Boolean.parseBoolean(s);
				return b;
			};

			public String toString() { return "parseBool"; }
            public final String name = "parseBool";
		};
	}

	/**
	 * Produces an operation that when applied to data will attempt to parse a byte value from a String
	 * @return an IDataOp object that can be applied to an IDataAccess object
	 */
	public IDataOp<Byte> makeParseByte() {
		return new IDataOp<Byte>() {
			public Byte apply(IDataAccess d) {
				String s = d.getContents();
				if (s == null || s.equals("")) return 0;
				else return Byte.parseByte(s);
			}

			public String toString() { return "parseByte"; }
            public final String name = "parseByte";
		};
	}

	/**
	 * Produces an operation that when applied to data will attempt to parse a character from a String of data
	 * @return an IDataOp object that can be applied to an IDataAccess object.
	 */
	public IDataOp<Character> makeParseChar() {
		return new IDataOp<Character>(){
			public Character apply(IDataAccess d){
				String s = d.getContents();
				if(s == null || s.equals("")){ 
					return ' ';//TODO
				}else
					return s.charAt(0);
			}

			public String toString() { return "parseChar"; }
            public final String name = "parseChar";
		};
	}

	/**
	 * Produces an operation that when applied to data will attempt to parse a double from a String of data
	 * @return an IDataOp object that can be applied to an IDataAccess object to parse a double from the data.
	 */
	public IDataOp<Double> makeParseDouble() {

		return new IDataOp<Double>() {
			public Double apply(IDataAccess d) {
				String s = d.getContents();
				if (s == null || s.equals("")) return 0.0;
				else return Double.parseDouble(s);
			}
			
            public String toString() { return "parseDouble"; }
            public final String name = "parseDouble";
		};
	}

	/**
	 * Produces an operation that when applied to data will attempt to parse a float from a String of data
	 * @return an IDataOp object that can be applied to an IDataAccess object to parse a float from the data.
	 */
	public IDataOp<Float> makeParseFloat() {
		return new IDataOp<Float>() {
			public Float apply(IDataAccess d) {
				String s = d.getContents();
				if (s == null || s.equals("")) return 0.0f;
				else return Float.parseFloat(s);
			}
			
            public String toString() { return "parseFloat"; }
            public final String name = "parseFloat";
		};
	}
	
	/**
	 * Produces an operation that when applied to data will attempt to parse a long from a String of data
	 * @return an IDataOp object that can be applied to an IDataAccess object to parse a long from the data.
	 */
	public IDataOp<Long> makeParseLong() {
		return new IDataOp<Long>() {
			public Long apply(IDataAccess d) {
				String s = d.getContents();
				if (s == null || s.equals("")) return 0L;
				else return Long.parseLong(s);
			}

			public String toString() { return "parseLong"; }
            public final String name = "parseLong";
		};
	}


	/**
	 * Produces an operation that when applied to data will attempt to make a new Java object with the given constructor,
	 * after trying to parse each of the parameters from the data
	 * @param cons - the constructor of the object being created
	 * @param consParamOps - an Array of operations that can be applied to data to parse the parameters for the constructor
	 * @return the IDataOp object that can be applied to data to create a new java object.
	 */
	public <T> IDataOp<T> makeConstructor(Constructor<?> cons,IDataOp<T>[] consParamOps){
		return new IDataOp<T>(){
			@SuppressWarnings("unchecked")
            @Override
			public T apply(IDataAccess d) {
				Object[] args;
				int start;
				if (ProcessingDetector.inProcessing() 
				        && cons.getParameterTypes().length == consParamOps.length + 1) {
				    args = new Object[consParamOps.length + 1];
				    args[0] = ProcessingDetector.getPapplet();
				    start = 1;
				} else {				
				    args = new Object[consParamOps.length];
				    start = 0;
				}
				
				for(int i = start; i < args.length; i++) {
					args[i] = consParamOps[i-start].apply(d);
				}
				try {
				    cons.setAccessible(true);
					return (T) cons.newInstance(args);
				} catch (InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
				throw new RuntimeException("failed to construct object from data");
			}
			
			public String toString() {
			    return String.format("new %s(%s)", cons.getDeclaringClass().getSimpleName(), StringUtils.join(consParamOps, ", "));
			}

            public final String name = "newObject";
		};
	}

	/**
	 * Produces an operation that when applied to data will attempt to index a piece of data at the given path to a given index (i)
	 * of a List, Array, or Collection of some type, and use the operation provided to parse the object from the data
	 * 
	 * This allows for applying different operations to each piece of a collection/list, whereas an operation created with
	 *  makeIndexAll can only apply a single operation to all pieces of a collection;
	 *  
	 * @param op - the operation used to parse piece of data being placed in the collection
	 * @param path - the location of the data to apply the op to
	 * @param i - the index of the collection you intended to place the parsed data at
	 * @return - an IDataOp object that can be applied to an IDataAccess object to parse a piece of a collection/list
	 */
	public <T> IDataOp<T> makeIndexOp(IDataOp<T> op, String path, int i) {
	    return new IDataOp<T>() {
	        public T apply(IDataAccess d) {
	            return op.apply(d.get(path, i));
	        }

	        public String toString() {
	            return String.format("index(%s, %d) ==> %s", path, i, op);
	        }
	        
            public final String name = "indexI";
	    };
	}
	
	/**
	 * Produces an operation that when applied to data will attempt to place all pieces of that data into a collection/list
	 * and parse its pieces using the operation provided
	 * 
	 * Different from makeIndexOp, because this function produces an operation that applies a single parsing
	 * operation to ALL pieces of the collection/list it it building.
	 * 
	 * @param op - the operation used to parse every piece of the data going into the collection/list
	 * @param path - the location of piece of data/base location of the list
	 * @return an IDataOp object that can be applied to an IDataAccess object to build a list, 
	 * where every piece is parsed with one operation
	 */
	public <T> IDataOp<Stream<T>> makeIndexAllOp(IDataOp<T> op, String path) {
		return new IDataOp<Stream<T>>() {
			public Stream<T> apply(IDataAccess d) {
				return d.getAll(path).map(d0 -> op.apply(d0)); 
			}
			
			public String toString() {
			    return String.format("indexall(%s) ==> %s", path, op);
			}

			public final String name = "indexAll";
		};
	}
	
	
   public <T> IDataOp<Stream<T>> makeIndexAllFlattenOp(IDataOp<Stream<T>> op, String path) {
        return new IDataOp<Stream<T>>() {
            public Stream<T> apply(IDataAccess d) {
                //System.out.println("FLATTENING ON: " + op);
                return d.getAll(path).flatMap(d0 -> op.apply(d0)); 
            }
            
            public String toString() {
                return String.format("indexallflat(%s) ==> %s", path, op);
            }

            public final String name = "indexAllFlat";
        };
    }
	   
	
	
	/**
	 * Produces an operation that when applied to data will expect to produce a single result,
	 * but wraps it up as a stream (i.e. sequence/list) of data results
	 * 
	 * @param op
	 * @param path
	 * @return
	 */
	public <T> IDataOp<Stream<T>> makeWrapOp(IDataOp<T> op, String path){
		return new IDataOp<Stream<T>>(){
			public Stream<T> apply(IDataAccess d){
				return Stream.of(op.apply(d));
			}
			
			public String toString() {
			    return String.format("wrapop(%s, %s)", op, path);
			}
        
			public final String name = "wrap";
		};
	}
	
	/**
	 * Produces an operation that when applied to data will attempt to select a piece of data at the given path,
	 *  and parse it using the provided operation
	 *  
	 *  Used for selecting fields of data
	 * @param  op - the operation used to parse the data being selected
	 * @param path - the location of the data to select
	 * @return an IDataOp object that can be applied to an IDataAccess object to select data from the given path and parse it.
	 */
	public <T> IDataOp<T> makeSelectOp(IDataOp<T> op, String path) {
	    if (path == null || path.equals("")) {   // this is a no-op
	        return op;
	    }else
		return new IDataOp<T>() {
			public T apply(IDataAccess d) {
				return op.apply(d.get(path));
			}

			public String toString() {
			    return String.format("select(\"%s\") ==> [%s]", path, op);
			}

            public final String name = "selectField";
		};
	}

}
