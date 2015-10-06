package core.ops;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

import core.access.IDataAccess;

class DataOpFactory {

	IDataOp<Integer> makeParseInt() {
		return new IDataOp<Integer>() {
			public Integer apply(IDataAccess d) {
				String s = d.getContents();
				if (s == null || s.equals("")) return 0;
				else return Integer.parseInt(s);
			}
		};
	}

	IDataOp<String> makeParseString() {
		return new IDataOp<String>() {
			public String apply(IDataAccess d) {
				return d.getContents();
			}
		};
	}

	IDataOp<Boolean> makeParseBool(){
		return new IDataOp<Boolean>(){
			public Boolean apply(IDataAccess d){
				return false;
			};
		};
	}

	IDataOp<Byte> makeParseByte() {
		return new IDataOp<Byte>() {
			public Byte apply(IDataAccess d) {
				String s = d.getContents();
				if (s == null || s.equals("")) return 0;
				else return Byte.parseByte(s);
			}
		};
	}

	IDataOp<Character> makeParseChar() {
		return new IDataOp<Character>(){
		public Character apply(IDataAccess d){
			String s = d.getContents();
			if(s == null || s.equals("")){
				System.out.println("TOBEFINISHED");
				//return '';//WHATEVER THE DEFAULT CHAR IS
				return null;
			}else
				return s.charAt(0);
			}
		};
	}

	IDataOp<Double> makeParseDouble() {

		return new IDataOp<Double>() {
			public Double apply(IDataAccess d) {
				String s = d.getContents();
				if (s == null || s.equals("")) return 0.0;
				else return Double.parseDouble(s);
			}
		};
	}

	IDataOp<Float> makeParseFloat() {
		return new IDataOp<Float>() {
			public Float apply(IDataAccess d) {
				String s = d.getContents();
				if (s == null || s.equals("")) return 0.0f;
				else return Float.parseFloat(s);
			}
		};
	}

	IDataOp<Long> makeParseLong() {
		return new IDataOp<Long>() {
			public Long apply(IDataAccess d) {
				String s = d.getContents();
				if (s == null || s.equals("")) return 0L;
				else return Long.parseLong(s);
			}
		};
	}

	<T> IDataOp<T> makeParseWildCard() {
		// TODO Auto-generated method stub
		return null;
	}


	<T> IDataOp<T> makeConstructor(Constructor<?> cons,IDataOp<T>[] newO){
		return new IDataOp<T>(){

			@Override
			public T apply(IDataAccess d) {
				Object[] args = new Object[newO.length];
				for(int i = 0; i < args.length; i++) {
					args[i] = newO[i].apply(d);
				}
				try {
					return (T) cons.newInstance(args);
				} catch (InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
				throw new RuntimeException("failed to construct object from data");
			}};
	}

	<T> IDataOp<T> makeIndexOp(IDataOp<T> s, String path, int i) {
		try{
			return new IDataOp<T>() {
				public T apply(IDataAccess d) {
					return s.apply(d.get(path, i));
				}
			};
		}catch(RuntimeException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	<T> IDataOp<T> makeSelectOp(IDataOp<T> s, String path) {
		return new IDataOp<T>() {
			public T apply(IDataAccess d) {
				return s.apply(d.get(path));
			}
		};
	}

	<T> IDataOp<Stream<T>> makeIndexAllOp(IDataOp<T> s, String path) {
		return new IDataOp<Stream<T>>() {
			public Stream<T> apply(IDataAccess d) {
				return d.getAll(path).map(d0 -> s.apply(d0)); //.collect(Collectors.toList());
			}
		};
	}

}
