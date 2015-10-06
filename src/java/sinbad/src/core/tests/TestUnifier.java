package core.tests;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.*;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import big.data.field.*;
import big.data.sig.*;

class Foo {
	int n;
	public Foo(int n) {
		this.n = n;
	}

	@Override
	public String toString(){
		return "n : "+n;
	}
}

class Bar{
	int x;
	int y;
	String s;
	public Bar(int x, int y, String s) {
		this.x = x;
		this.y = y;
		this.s = s;
	}

	@Override
	public String toString(){
		return "Bar : \n\tx : "+this.x+"\n\ty : "+this.y+"\n\ts : "+this.s;
	}
}

class FooBar{
	int n;
	int x;
	int y;
	String s;
	public FooBar(int n, int x, int y, String s){
		this.n = n;
		this.x = x;
		this. y = y;
		this.s = s;
	}

	public FooBar(int x, int y, String s){
		this.x = x;
		this. y = y;
		this.s = s;
	}
}

class FooToo{
	Foo foo;
	int too;
	public FooToo(Foo foo, int too) {
		this.foo = foo;
		this.too = too;
	}

	public FooToo(int n,int too){
		this.foo = new Foo(n);
		this.too = too;
	}

	@Override
	public String toString(){
		return "foo : "+this.foo.toString()+"\n\ttoo: "+this.too;
	}
}

public class TestUnifier {
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

		/*======= Test case : Prim || Prim =======*/
		IDataField fld1 = new PrimField();
		ISig sig1 = PrimSig.INT_SIG;
		RuleBuilder r = new RuleBuilder();
		IDataOp<Integer> dop = r.unifyWith(fld1, sig1);
		int i = dop.apply(new RawPrim("123"));
		System.out.println(i + "!");



		/*=======  Test case : Prim || Compound */ 
		CompSig<Foo> sig2 = new CompSig<Foo>(Foo.class);
		sig2.addField(sig1, "n");
		IDataOp<Foo> dop2 = r.unifyWith(fld1, sig2);
		Foo f = dop2.apply(new RawPrim("123"));
		System.out.println(f.n + "!!!");

		/*======= Test case : Prim || List */
		ListSig sig3 = new ListSig(PrimSig.INT_SIG);
		IDataField pfield = new PrimField("n");
		IDataOp<Stream<Integer>> dop3 = r.unifyWith(pfield, sig3);
		List<Integer> i0 = dop3.apply(new RawList("n",new IDataAccess[]
				{new RawPrim("0")})).collect(Collectors.toList());
		System.out.println(i0 + "!?!?!");


		/*=======  Test case : Compound || Prim */
		CompField fld2 = new CompField("somePath","A description");
		fld1 = new PrimField("a/path","Adescription");
		/*fld2 = (CompField)*/ fld2.addField("n", fld1);
		System.out.println("Field n: "+fld2.hasField("n"));
		IDataOp<Integer> dop4 = r.unifyWith(fld2, sig1); 
		int i1 = dop4.apply(new RawPrim("123"));
		System.out.println(i1+"!!@!");

		/* **************** Test cases for Compound || Compound **************************************/
		/*=======  subcase 1 : Compound{f0} || Compound {f0} */
		//Using sig2 CompSig(Foo.class) and fld2 CompField with 1 field named n    	
		IDataOp<Foo> dop5 = r.unifyWith(fld2, sig2);
		Foo foo1 = dop5.apply(new RawStruct(new RawStructField[]{new RawStructField("n",new RawPrim("2"))}));


		System.out.println("Foo: n: "+foo1.n+" ..!!..!!");
		/*======= subcase 2 : Compound{f0,...,fn} || Compound{f0...fm} n = m*/
		CompSig<Bar> barSig1 = new CompSig<Bar>(Bar.class);
		PrimSig barX = PrimSig.INT_SIG;
		PrimSig barY = PrimSig.INT_SIG;
		PrimSig barS = PrimSig.STRING_SIG;
		barSig1.addField(barX, "x");
		barSig1.addField(barY, "y");
		barSig1.addField(barS, "s");
		CompField barField = new CompField("BasePath","ABarDescription");
		barField.addField("x", new PrimField());
		barField.addField("y", new PrimField());
		barField.addField("s", new PrimField());
		IDataOp<Bar> dop6 = r.unifyWith(barField, barSig1);
		Bar bar1 = dop6.apply(new RawStruct(new RawStructField[]
				{new RawStructField("x",new RawPrim("10")),
				new RawStructField("y",new RawPrim("11")),
				new RawStructField("s",new RawPrim("12"))}));

		System.out.println("Bar : \n\tx : "+bar1.x+"\n\ty : "+bar1.y+"\n\ts : "+bar1.s);

		/*======= subcase 3 : Compound{f0,...,fn} || Compound{f0...fm} n > m */
		CompField fooBarField = new CompField("foo.bar.foobar","aNiceDescription");
		fooBarField.addField("n", new PrimField());
		fooBarField.addField("x", new PrimField());
		fooBarField.addField("y", new PrimField());
		fooBarField.addField("s", new PrimField());

		IDataOp<Bar> dop7 = r.unifyWith(fooBarField,barSig1);
		Bar bar2 = dop7.apply(new RawStruct(new RawStructField[]
				{new RawStructField("n",new RawPrim("9")),
				new RawStructField("x",new RawPrim("10")),
				new RawStructField("y",new RawPrim("11")),
				new RawStructField("s",new RawPrim("12"))}));
		System.out.println(bar2.toString());

		/*======= subcase 4 : Compound{f0,...,fn} || Compound{f0...fm} n < m */

		CompSig<FooBar> fooBarSig = new CompSig<FooBar>(FooBar.class);
		fooBarSig.addField(PrimSig.INT_SIG,"n");
		fooBarSig.addField(PrimSig.INT_SIG,"x");
		fooBarSig.addField(PrimSig.INT_SIG,"y");
		fooBarSig.addField(PrimSig.STRING_SIG,"s");

		try{
			IDataOp<FooBar> dop8 = r.unifyWith(barField,fooBarSig);
		}catch(RuntimeException e){
			System.out.println("Bar 2 unable to unify like it was suppposed to :)\n");
		}

		/*======= subcase 5 : Compound with compound fields matched with itself (Nested Compound)*/

		CompSig<FooToo> fooTooSig = new CompSig<FooToo>(FooToo.class);
		CompSig<Foo> fooSig = new CompSig<Foo>(Foo.class);
		fooSig.addField(PrimSig.INT_SIG, "n");
		fooTooSig.addField(fooSig, "foo");
		fooTooSig.addField(PrimSig.INT_SIG, "too");
		CompField fooTooField = new CompField();
		CompField fooField = new CompField("foo","");
		fooField.addField("n", new PrimField());
		fooTooField.addField("foo",fooField);
		fooTooField.addField("too", new PrimField());

		IDataOp<FooToo> fooTooDop = r.unifyWith(fooTooField, fooTooSig);
		FooToo newFooToo = fooTooDop.apply(
				new RawStruct(new RawStructField[]
						{new RawStructField("foo",new RawStruct(new RawStructField("n",new RawPrim("1")))),
						new RawStructField("too",new RawPrim("17"))}));

		System.out.println(newFooToo.toString());

		/*======= Test case : Comp || List(List WRAP 2)*/
		//Use fooSig & fooField for the comp
		CompField wrapField = fooField;
		ListSig listWrapComp = new ListSig(fooSig);
		IDataOp<Stream<Foo>> dop12 = r.unifyWith(wrapField, listWrapComp);
		List<Foo> wrappedFoo = dop12.apply(
				new RawList("foo",new IDataAccess[]{(new RawStruct(new RawStructField("",
						new RawStruct(new RawStructField("n",new RawPrim("521"))))))})).collect(Collectors.toList());
		/*		
				new FailAccess(){
			public Stream<IDataAccess> getAll(String path){
				IDataAccess[] data = new IDataAccess[]{new FailAccess(){
						public IDataAccess get(String path){
							return new FailAccess(){
								public IDataAccess get(String path){
									return new FailAccess(){
										public String getContents(){
											return "521";
										}
									};
								}
							};
						}

				}};
				return Arrays.stream(data);
			}
		}).collect(Collectors.toList());
		 */

		System.out.println(wrappedFoo);

		/*======= Test case : List || Prim (List STRIP 1)*/
		PrimField listFldPrimTest = new PrimField("","");
		ListField listfld1 = new ListField("list/","n",listFldPrimTest);
		IDataOp<Integer> dop9 = r.unifyWith(listfld1, PrimSig.INT_SIG);
		int listInt = dop9.apply(new RawStruct(new RawStructField[]{new RawStructField("n",new RawPrim("1337"))}));
		/*		
				new FailAccess(){
			public IDataAccess get(String path){
				if(path.equals("n")){
					return new FailAccess(){
						public String getContents(){
							return "1337";
						}
					};
				}else
					throw new RuntimeException("Failed");
			}
		});
		 */
		System.out.println("List elem 1: "+listInt);

		/*======= Test case : List || Comp (List STRIP 2) */
		CompField listCompField = new CompField("","");
		listCompField.addField("x", listFldPrimTest);
		listCompField.addField("y", listFldPrimTest);
		listCompField.addField("s", listFldPrimTest);
		ListField listfld2 = new ListField("","thing",listCompField);
		IDataOp<Bar> dop10 = r.unifyWith(listfld2, barSig1);

		Bar barFromList = dop10.apply(new RawStruct(new RawStructField("thing",new RawStruct(new RawStructField[]
				{new RawStructField("x",new RawPrim("156")),
				new RawStructField("y",new RawPrim("4657")),
				new RawStructField("s",new RawPrim("AStringforS"))}))));		
		/*		
				new FailAccess(){
			public IDataAccess get(String path){
				if(path.equals("n"))
					return new FailAccess(){
					public IDataAccess get(String path){
						if(path.equals("x"))
							return new FailAccess(){
							public String getContents(){
								return "156";
							}
						};
						else if(path.equals("y"))
							return new FailAccess(){
							public String getContents(){
								return "4657";
							}
						};
						else if(path.equals("s"))
							return new FailAccess(){
							public String getContents(){
								return "ASTringFor S";
							}
						};
						else
							throw new RuntimeException("Field "+path+" not found.");
					}
				};
				else
					throw new RuntimeException("Test Failed.");
			}
		});
		 */
		System.out.println(barFromList.toString());

		/*======= Test Case : List[t] || List[t], && t = PrimSig */
		//Asking for a list of ints have a list of ints
		ListField listfld3 = new ListField("","",listFldPrimTest);
		ListSig listSig = new ListSig(PrimSig.INT_SIG);

		IDataOp<Stream<Integer>> listOints = r.unifyWith(listfld3, listSig);
		IDataAccess[] testData = new IDataAccess[10];
		for(int j = 0; j < testData.length; j++){
			testData[j] = new RawStruct(new RawStructField("",new RawPrim(""+j)));
		}
		List<Integer> intStream = listOints.apply(new RawList("",testData)).collect(Collectors.toList());


		/*	
				new FailAccess() {
			public Stream<IDataAccess> getAll(String path) {
				IDataAccess[] das = new IDataAccess[10];
				for (int i = 0; i < 10; i++) {
					final int n = i;
					IDataAccess da = new FailAccess() { 
						public String getContents() { return "" + n; }
						public IDataAccess get(String path){ return new FailAccess(){public String getContents(){return ""+n;}};}
					};
					das[i] = da;
				}
				return Arrays.stream(das);
			}    
		}).collect(Collectors.toList());
		 */
		System.out.println(intStream);

		/*======== Test Case : List[t] || List[t], && t = CompSig{PrimSig's only} */
		CompField foofld2 = new CompField();
		foofld2.addField("n", new PrimField());
		ListField listfld4 = new ListField("test","",foofld2);
		CompSig<Foo> fooSig2 = new CompSig<Foo>(Foo.class);
		fooSig2.addField(PrimSig.INT_SIG, "n");
		ListSig listSig2 = new ListSig(fooSig2);

		IDataOp<Stream<Foo>> listOFoos = r.unifyWith(listfld4, listSig2);
		IDataAccess[] testData2 = new IDataAccess[10];
		for(int j = 0; j < testData2.length; j++){
			testData2[j] = new RawStruct(
					new RawStructField("",new RawStruct(				//TODO Extra wrap up?...
							new RawStructField("n",new RawPrim(""+j)))));
		}
		List<Foo> foos = listOFoos.apply(new RawList("test",testData2)).collect(Collectors.toList());

		/*	
				new FailAccess() {
			public Stream<IDataAccess> getAll(String path) {
				IDataAccess[] das = new IDataAccess[10];
				for (int i = 0; i < 10; i++) {
					final int n = i;
					IDataAccess da = new FailAccess() { 
						public IDataAccess get(String path){ 
							return new FailAccess(){
								public IDataAccess get(String path){
									return new FailAccess(){public String getContents(){return ""+n;}};
								}
							};
						}
					};
					das[i] = da;
				}
				return Arrays.stream(das);
			}    
		}).collect(Collectors.toList());
		 */
		System.out.println(foos);


		/*======= Test Case : List[t] || List[t], && t = ListSig (Recursive call list) */
		listFldPrimTest = new PrimField("atest","");
		listfld3 = new ListField("test1","atest",listFldPrimTest);
		ListField listoListFld = new ListField("test3","btest",listfld3);
		ListSig listoListSig = new ListSig(new ListSig(PrimSig.INT_SIG));

		IDataOp<Stream<Stream<Integer>>> dop11 = r.unifyWith(listoListFld, listoListSig);
		IDataAccess[] testDataLol = new IDataAccess[10];
		for(int j = 0; j < testDataLol.length; j++){
			IDataAccess[] testData3 = new IDataAccess[10];
			for(int k = 0; k < testData3.length; k++){
				testData3[k] = new RawStruct(
						new RawStructField("atest",new RawPrim(""+(j+k))));
			}
			testDataLol[j] = new RawList("btest",testData3);
		}


		List<List<Integer>> lol = 
				dop11.apply(new RawList("test3",testDataLol)).map(item -> 
												item.collect(Collectors.toList())).collect(Collectors.toList());
		/*

				new FailAccess(){
					public Stream<IDataAccess> getAll(String path){
						IDataAccess[] dad = new IDataAccess[5];
						for(int i = 0; i < dad.length; i++)
							dad[i] = new FailAccess(){

							public IDataAccess get(String path){
								return new FailAccess(){
									public Stream<IDataAccess> getAll(String path){
										IDataAccess[] da = new IDataAccess[3];
										for(int i = 0; i < da.length; i++){
											final int n = i;
											da[i] = new FailAccess(){
												public IDataAccess get(String path){
													return new FailAccess(){
														public String getContents(){
															return ""+n;
														}
													};
												}
											};
										}
										return Arrays.stream(da);
									}
								};
							}

						};
						return Arrays.stream(dad);
					}
				}).map(item -> item.collect(Collectors.toList())).collect(Collectors.toList());
				*/
		System.out.println(lol);


		/* Test Case : List || Comp (WEIRD) */
		//TODO

		/*
        CsvParser r = new CsvParser(new CsvParserSettings());
        List<String[]> allRows = 
                r.parseAll(new InputStreamReader(new FileInputStream("src/example.csv")));
        String[] header = allRows.remove(0);
        IDataAccess csvAcc = new CSVAccess(allRows, header);

        DataOpFactory f = new DataOpFactory();
        IDataOp<String> dop
            = f.makeIndexOp(f.makeSelectOp(f.makeParseString(), "Model"), null, 1);
        IDataOp<Stream<Integer>> dop2
            = f.makeIndexAllOp(f.makeSelectOp(f.makeParseInt(), "Year"), null);
        Stream<Integer> y = dop2.apply(csvAcc);
        System.out.println(dop.apply(csvAcc) + "\n" + y.collect(Collectors.toList()));

        // another one ========================================================
        long t1 = System.currentTimeMillis();
        CsvParserSettings sts = new CsvParserSettings();
        sts.setLineSeparatorDetectionEnabled(true);
        r = new CsvParser(sts);
        allRows = 
               r.parseAll(new InputStreamReader(new FileInputStream("src/Fielding.csv")));
        header = allRows.remove(0);
        csvAcc = new CSVAccess(allRows, header);
        IDataOp<Stream<String>> dop3
            = f.makeIndexAllOp(f.makeSelectOp(f.makeParseString(), "playerID"), null);
        List<String> cs = dop3.apply(csvAcc).collect(Collectors.toList());
        System.out.println(cs.size() + "\n" + cs.get(0));
        long t2 = System.currentTimeMillis();
        System.out.println("-- " + (t2 - t1));

        IDataOp<Stream<Stream<Integer>>> dopErr
            = f.makeIndexAllOp(f.makeIndexAllOp(f.makeParseInt(), null), null);
       // Stream<Stream<Integer>> res = dopErr.apply(csvAcc); //.collect(Collectors.toList());

        // xml ========================================================
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc_one = builder.parse(new File("src/cd_catalog.xml"));
        IDataAccess xmlAcc = new XMLAccess(doc_one.getDocumentElement());
        dop = f.makeIndexOp(f.makeSelectOp(f.makeParseString(), "ARTIST"),
                               "CD", 1);
        String h = dop.apply(xmlAcc);
        System.out.println(h);

        dop2 = f.makeIndexAllOp(f.makeSelectOp(f.makeParseInt(), "YEAR"), "CD");
        y = dop2.apply(xmlAcc);
        System.out.println(y.collect(Collectors.toList()));
		 */


	}
}

interface IDataAccess {
	String getContents();
	IDataAccess get(String path, int i);
	IDataAccess get(String path);
	Stream<IDataAccess> getAll(String path);
}

class FailAccess implements IDataAccess {
	public String getContents() {
		throw new RuntimeException("could not access contents as a primitive (string)"); 
	}

	public IDataAccess get(String path, int i) {
		throw new RuntimeException("could not access index " + i + " (not a list?)"); 
	}

	public IDataAccess get(String path) {
		throw new RuntimeException("could not access field " + path + " (not a record?)"); 
	}

	public Stream<IDataAccess> getAll(String path) {
		throw new RuntimeException("not a list"); 
	}
}

interface IDataOp<T> {
	T apply(IDataAccess d);
}

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

class CSVAccess extends FailAccess {
	String[] header;
	List<String[]> lines;

	public CSVAccess(List<String[]> lines, String[] header) throws IOException {
		this.header = header;
		this.lines = lines;
	}

	@Override
	public IDataAccess get(String path, int i) {
		return new RowAccess(i);
	}

	@Override
	public Stream<IDataAccess> getAll(String path) {
		/*IDataAccess[] das = new IDataAccess[lines.size()];
        for (int i = 0; i < das.length; i++) {
            das[i] = new RowAccess(i);
        }
        return Arrays.stream(das);
		 */
		return IntStream.range(0, lines.size()).mapToObj(i -> new RowAccess(i));
	}

	class CellAccess extends FailAccess {
		int i, j;

		public CellAccess(int i, int j) {
			this.i = i;
			this.j = j;
		}

		public String getContents() {
			return lines.get(i)[j];
		}
	}

	class RowAccess extends FailAccess {
		int i;

		public RowAccess(int i) {
			this.i = i;
		}

		public IDataAccess get(String path) {
			int j = indexOfColumn(path);
			if (j < 0) { throw new RuntimeException("No field named " + path); }
			return new CellAccess(i, j);
		}

		int indexOfColumn(String label) {
			for (int j=0; j<header.length; j++) {
				if (header[j].equals(label)) return j;
			}
			return -1;
		}
	}
}


class XMLAccess implements IDataAccess {
	Element e;

	public XMLAccess(Element e) {
		this.e = e;
	}

	@Override
	public String getContents() {
		return e.getTextContent();
	}

	@Override
	public IDataAccess get(String path, int i) {
		NodeList nl = e.getElementsByTagName(path);
		int count = 0;
		for (int j = 0; j < nl.getLength(); j++) {
			Node nd = nl.item(i);
			if (nd.getNodeType() == Node.ELEMENT_NODE) {
				if (count == i) 
					return new XMLAccess((Element)nd);
				else
					count++;
			}
		}
		throw new RuntimeException("not found: " + path);
	}

	@Override
	public IDataAccess get(String path) {
		return new XMLAccess((Element)e.getElementsByTagName(path).item(0));
	}

	@Override
	public Stream<IDataAccess> getAll(String path) {
		NodeList nl = e.getElementsByTagName(path);
		return IntStream.range(0, nl.getLength()).mapToObj(
				i -> new XMLAccess((Element)nl.item(i)));
	}



}
