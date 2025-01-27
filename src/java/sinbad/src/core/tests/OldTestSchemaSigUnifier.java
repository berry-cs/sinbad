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

import core.access.*;
//import core.access.raw.*;
import core.ops.*;
import core.schema.*;
import core.sig.*;



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
	int x,y;
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

class WeirdClass{
	int x,y,z;
	public WeirdClass(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString(){
		return "x: "+this.x+" y: "+this.y+" z: "+this.z;
	}
}

class FooBar{
	int n,x,y;
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

public class OldTestSchemaSigUnifier {
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

		/*======= Test case : Prim || Prim =======*/
		ISchema fld1 = new PrimSchema();
		ISig sig1 = PrimSig.INT_SIG;
		SchemaSigUnifier unifier = new SchemaSigUnifier();
		IDataOp<?> dop = unifier.unifyWith(fld1, sig1);
		int i = (Integer) dop.apply(new RawPrim("123"));
		System.out.println(i + "!");



		/*=======  Test case : Prim || Compound */ 
		CompSig<Foo> sig2 = new CompSig<Foo>(Foo.class, new ArgSpec("n", sig1));
		IDataOp<Foo> dop2 = unifier.unifyWith(fld1, sig2);
		Foo f = dop2.apply(new RawPrim("123"));
		System.out.println(f.n + "!!!");

		/*======= Test case : Prim || List */
		ListSig sig3 = new ListSig(PrimSig.INT_SIG);
		ISchema pfield = new PrimSchema("n");
		IDataOp<Stream<Integer>> dop3 = unifier.unifyWith(pfield, sig3);
		List<Integer> i0 = dop3.apply(new RawList("n",new IDataAccess[]
				{new RawPrim("0")})).collect(Collectors.toList());
		System.out.println(i0 + "!?!?!");


		/*=======  Test case : Compound || Prim */
		fld1 = new PrimSchema("a/path","Adescription");
        CompSchema fld2 = new CompSchema("somePath","A description", new CompField("n", fld1));
		System.out.println("Field n: "+fld2.getFieldMap().get("n"));
		IDataOp<Integer> dop4 = unifier.unifyWith(fld2, sig1); 
		int i1 = dop4.apply(new RawPrim("123"));
		System.out.println(i1+"!!@!");

		/* **************** Test cases for Compound || Compound **************************************/
		/*=======  subcase 1 : Compound{f0} || Compound {f0} */
		//Using sig2 CompSig(Foo.class) and fld2 CompSchema with 1 field named n    	
		IDataOp<Foo> dop5 = unifier.unifyWith(fld2, sig2);
		Foo foo1 = dop5.apply(new RawStruct(new RawStructField[]{new RawStructField("n",new RawPrim("2"))}));


		System.out.println("Foo: n: "+foo1.n+" ..!!..!!");
		/*======= subcase 2 : Compound{f0,...,fn} || Compound{f0...fm} n = m*/
		PrimSig barX = PrimSig.INT_SIG;
		PrimSig barY = PrimSig.INT_SIG;
		PrimSig barS = PrimSig.STRING_SIG;
        CompSig<Bar> barSig1 = new CompSig<Bar>(Bar.class, new ArgSpec("x", barX),
                                                            new ArgSpec("y", barY),
                                                             new ArgSpec("s", barS));
        CompSchema barField = new CompSchema("BasePath","ABarDescription", 
                                new CompField("x", new PrimSchema()),
                                new CompField("y", new PrimSchema()),
                                new CompField("s", new PrimSchema()));
		IDataOp<Bar> dop6 = unifier.unifyWith(barField, barSig1);
		Bar bar1 = dop6.apply(new RawStruct(new RawStructField[]
				{new RawStructField("x",new RawPrim("10")),
				new RawStructField("y",new RawPrim("11")),
				new RawStructField("s",new RawPrim("12"))}));

		System.out.println("Bar : \n\tx : "+bar1.x+"\n\ty : "+bar1.y+"\n\ts : "+bar1.s);

		/*======= subcase 3 : Compound{f0,...,fn} || Compound{f0...fm} n > m */
		CompSchema fooBarField = new CompSchema("foo.bar.foobar","aNiceDescription");
		fooBarField.addField("n", new PrimSchema());
		fooBarField.addField("x", new PrimSchema());
		fooBarField.addField("y", new PrimSchema());
		fooBarField.addField("s", new PrimSchema());

		IDataOp<Bar> dop7 = unifier.unifyWith(fooBarField,barSig1);
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
			IDataOp<FooBar> dop8 = unifier.unifyWith(barField,fooBarSig);
		}catch(RuntimeException e){
			System.out.println("Bar 2 unable to unify like it was suppposed to :)\n");
		}

		/*======= subcase 5 : Compound with compound fields matched with itself (Nested Compound)*/

		CompSig<FooToo> fooTooSig = new CompSig<FooToo>(FooToo.class);
		CompSig<Foo> fooSig = new CompSig<Foo>(Foo.class);
		fooSig.addField(PrimSig.INT_SIG, "n");
		fooTooSig.addField(fooSig, "foo");
		fooTooSig.addField(PrimSig.INT_SIG, "too");
		CompSchema fooTooField = new CompSchema();
		CompSchema fooField = new CompSchema("foo","");
		fooField.addField("n", new PrimSchema());
		fooTooField.addField("foo",fooField);
		fooTooField.addField("too", new PrimSchema());

		IDataOp<FooToo> fooTooDop = unifier.unifyWith(fooTooField, fooTooSig);
		FooToo newFooToo = fooTooDop.apply(
				new RawStruct(new RawStructField[]
						{new RawStructField("foo",new RawStruct(new RawStructField("n",new RawPrim("1")))),
						new RawStructField("too",new RawPrim("17"))}));

		System.out.println(newFooToo.toString());

		/*======= Test case : Comp || List(List WRAP 2)*/
		//Use fooSig & fooField for the comp
		CompSchema wrapField = fooField;
		ListSig listWrapComp = new ListSig(fooSig);
		IDataOp<Stream<Foo>> dop12 = unifier.unifyWith(wrapField, listWrapComp);
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
		PrimSchema listFldPrimTest = new PrimSchema("","");
		ListSchema listfld1 = new ListSchema("list/","n",listFldPrimTest);
		IDataOp<Integer> dop9 = unifier.unifyWith(listfld1, PrimSig.INT_SIG);
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
		CompSchema listCompSchema = new CompSchema("","");
		listCompSchema.addField("x", listFldPrimTest);
		listCompSchema.addField("y", listFldPrimTest);
		listCompSchema.addField("s", listFldPrimTest);
		ListSchema listfld2 = new ListSchema("","thing",listCompSchema);
		IDataOp<Bar> dop10 = unifier.unifyWith(listfld2, barSig1);

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
		ListSchema listfld3 = new ListSchema("","",listFldPrimTest);
		ListSig listSig = new ListSig(PrimSig.INT_SIG);

		IDataOp<Stream<Integer>> listOints = unifier.unifyWith(listfld3, listSig);
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
		CompSchema foofld2 = new CompSchema();
		foofld2.addField("n", new PrimSchema());
		ListSchema listfld4 = new ListSchema("test","",foofld2);
		CompSig<Foo> fooSig2 = new CompSig<Foo>(Foo.class);
		fooSig2.addField(PrimSig.INT_SIG, "n");
		ListSig listSig2 = new ListSig(fooSig2);

		IDataOp<Stream<Foo>> listOFoos = unifier.unifyWith(listfld4, listSig2);
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
		listFldPrimTest = new PrimSchema("ctest","");
		listfld3 = new ListSchema("baseB","btest",listFldPrimTest);
		ListSchema listoListFld = new ListSchema("baseA","Atest",listfld3);
		ListSig listoListSig = new ListSig(new ListSig(PrimSig.INT_SIG));

		IDataOp<Stream<Stream<Integer>>> dop11 = unifier.unifyWith(listoListFld, listoListSig);
		IDataAccess[] testDataLol = new IDataAccess[10];
		for(int j = 0; j < testDataLol.length; j++){
			IDataAccess[] testData3 = new IDataAccess[10];
			for(int k = 0; k < testData3.length; k++){
				testData3[k] = new RawStruct(
						new RawStructField("btest",new RawPrim(""+(j+k))));
			}
			testDataLol[j] = new RawStruct(new RawStructField("Atest",new RawList("baseB",testData3)));
		}
		List<List<Integer>> lol = 
				dop11.apply(new RawList("baseA",testDataLol)).map(item -> 
												item.collect(Collectors.toList())).collect(Collectors.toList());
		System.out.println(lol);
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


		/* Test Case : List || Comp (WEIRD) */
		ListSchema lField = new ListSchema("weirdBase","weirdElt",new PrimSchema("int"));
		CompSig<WeirdClass> weirdSig = new CompSig<WeirdClass>(WeirdClass.class);
		weirdSig.addField(PrimSig.INT_SIG, "x");
		weirdSig.addField(PrimSig.INT_SIG, "y");
		weirdSig.addField(PrimSig.INT_SIG, "z");
		
		RawList weirdList = new RawList("weirdElt",new IDataAccess[]{
				new RawPrim("1"),new RawPrim("2"),new RawPrim("3")
		});
		IDataOp<WeirdClass> weirdOp = unifier.unifyWith(lField, weirdSig);
		WeirdClass w = weirdOp.apply(weirdList);
		System.out.println(w);

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
