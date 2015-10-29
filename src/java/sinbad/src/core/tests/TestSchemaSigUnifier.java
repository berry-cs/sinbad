package core.tests;

import static org.junit.Assert.*;
import static core.sig.PrimSig.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import core.access.IDataAccess;
import core.access.raw.*;
import core.ops.*;
import core.schema.*;
import core.sig.*;

public class TestSchemaSigUnifier {
	SchemaSigUnifier unifier = new SchemaSigUnifier();
	static final ISig BAR_SIG = 
			new CompSig<Bar>(Bar.class, 
					new ArgSpec("x", INT_SIG),
					new ArgSpec("y", INT_SIG),
					new ArgSpec("s", STRING_SIG));
	static final CompSig<Foo> FOO_SIG = new CompSig<Foo>(Foo.class,new ArgSpec("n",INT_SIG));
	@Test
	public void testPrimPrim() {
		/*======= Test case : Prim || Prim =======*/
		IDataOp<Integer> dop = unifier.unifyWith(new PrimSchema(), INT_SIG);
		assertEquals((Integer)123, dop.apply(new RawPrim("123")));
	}

	@Test
	public void testPrimComp(){
		/*=======  Test case : Prim || Compound */ 
		ISchema pSch = new PrimSchema();
		IDataOp<Foo> dop2 = unifier.unifyWith(pSch,FOO_SIG);
		assertEquals(new Foo(123), dop2.apply(new RawPrim("123")));
	}
	@Test
	public void testPrimList(){
		/*======= Test case : Prim || List */
		ListSig lSig = new ListSig(INT_SIG);
		ISchema pSch = new PrimSchema("n");
		IDataOp<Stream<Integer>> dop = unifier.unifyWith(pSch, lSig);
		List<Integer> l = new ArrayList<Integer>();
		l.add(0);
		assertEquals(l,dop.apply(
				new RawList("n",new IDataAccess[]{new RawPrim("0")})).collect(Collectors.toList()));
	}


	@Test
	public void testCompPrim(){
		/*=======  Test case : Compound || Prim */
		CompSchema fld2 = new CompSchema("somePath","A description", new CompField("n", new PrimSchema()));
		IDataOp<Integer> dop = unifier.unifyWith(fld2, INT_SIG); 
		assertEquals((Integer) 123,dop.apply(new RawPrim("123")));
	}


	/* **************** Test cases for Compound || Compound **************************************/
	@Test
	public void testComp_0Comp_0(){
		/*=======  subcase 1 : Compound{f0} || Compound{f0} */
		CompSchema cSch = new CompSchema(new CompField("n",new PrimSchema()));
		IDataOp<Foo> dop = unifier.unifyWith(cSch, FOO_SIG);
		assertEquals(new Foo(2),dop.apply(
				new RawStruct(new RawStructField[]{new RawStructField("n",new RawPrim("2"))})));
	}

	@Test
	public void testComp_nComp_n(){
		/*======= subcase 2 : Compound{f0,...,fn} || Compound{f0...fm} n = m*/
		CompSchema barField = 
				new CompSchema(
						new CompField("x", new PrimSchema()),
						new CompField("y", new PrimSchema()),
						new CompField("s", new PrimSchema()));
		IDataOp<Bar> dop = unifier.unifyWith(barField, BAR_SIG);

		assertEquals(new Bar(10,11,"BarString"),
				dop.apply(new RawStruct(
						new RawStructField[]
								{new RawStructField("x",new RawPrim("10")),
										new RawStructField("y",new RawPrim("11")),
										new RawStructField("s",new RawPrim("BarString"))})));

	}

	@Test
	public void testComp_mComp_n_nltm(){
		/*======= subcase 3 : Compound{f0,...,fm} || Compound{f0...fn} n < m */
		CompSchema fooBarSchema = 
				new CompSchema(new CompField("n",new PrimSchema()),
						new CompField("x",new PrimSchema()),
						new CompField("y",new PrimSchema()),
						new CompField("s",new PrimSchema()));
		IDataOp<Bar> dop = unifier.unifyWith(fooBarSchema,BAR_SIG);
		assertEquals(new Bar(10,11,"AnotherString"),
				dop.apply(new RawStruct(
						new RawStructField[]
								{new RawStructField("n",new RawPrim("9")),
										new RawStructField("x",new RawPrim("10")),
										new RawStructField("y",new RawPrim("11")),
										new RawStructField("s",new RawPrim("AnotherString"))})));

	}

	@Test (expected=RuntimeException.class) 
	public void testComp_mComp_n_ngtm(){
		CompSig<FooBar> fooBarSig = new CompSig<FooBar>(FooBar.class,
				new ArgSpec("n",INT_SIG),
				new ArgSpec("x",INT_SIG),
				new ArgSpec("y",INT_SIG),
				new ArgSpec("s",STRING_SIG));
		CompSchema barField = 
				new CompSchema(
						new CompField("x", new PrimSchema()),
						new CompField("y", new PrimSchema()),
						new CompField("s", new PrimSchema()));
		unifier.unifyWith(barField,fooBarSig); // Should Produce an Exception
	}

	@Test
	public void testCompComp_nested(){
		/*======= subcase 5 : Compound with compound fields matched with itself (Nested Compound)*/

		CompSig<FooToo> fooTooSig = new CompSig<FooToo>(FooToo.class,
				new ArgSpec("foo",FOO_SIG),
				new ArgSpec("too",INT_SIG));
		CompSchema fooTooField = new CompSchema(new CompField("too",new PrimSchema("n","")),
				new CompField("foo",new CompSchema("foo","",new CompField("n", new PrimSchema("n",null)))));

		IDataOp<FooToo> dop = unifier.unifyWith(fooTooField, fooTooSig);
		assertEquals(new FooToo(new Foo(1),17),dop.apply(
				new RawStruct(new RawStructField[]{
						new RawStructField("foo",
								new RawStruct(new RawStructField("n",new RawPrim("1")))),
						new RawStructField("too",new RawPrim("17"))})));


	}

	@Test
	public void testCompList_Comp(){
		/*======= Test case : Comp || List(List WRAP Comp)*/
		CompSchema wrapField = new CompSchema("foo","Dec",new CompField("n",new PrimSchema()));
		ListSig listWrapComp = new ListSig(FOO_SIG);
		IDataOp<Stream<Foo>> dop = unifier.unifyWith(wrapField, listWrapComp);
		List<Foo> l = new ArrayList<Foo>();
		l.add(new Foo(521));
		assertEquals(l,dop.apply(
				new RawList("foo",new RawStruct(new RawStructField("",
						new RawStruct(new RawStructField("n",new RawPrim("521"))))))).collect(Collectors.toList()));
	}

	@Test
	public void testListPrimStrip(){
		/*======= Test case : List || Prim (List STRIP)*/
		PrimSchema listFldPrimTest = new PrimSchema("","");
		ListSchema listfld1 = new ListSchema("list","n",listFldPrimTest);
		IDataOp<Integer> dop = unifier.unifyWith(listfld1, INT_SIG);
		assertEquals((Integer) 1337,dop.apply(
				new RawStruct(new RawStructField("list",new RawPrim("1337")))));
	}

	@Test
	public void testListCompStrip(){
		/*======= Test case : List || Comp (List STRIP)*/
		CompSchema listCompSchema = 
				new CompSchema("","",new CompField("x",new PrimSchema()),
						new CompField("y",new PrimSchema()),new CompField("s",new PrimSchema()));
		ListSchema listfld2 = new ListSchema("","",listCompSchema);
		IDataOp<Bar> dop = unifier.unifyWith(listfld2, BAR_SIG);
		assertEquals(new Bar(156,4657,"AStringforS"),dop.apply(new RawStruct(
				new RawStructField("",new RawStruct(new RawStructField[]
						{new RawStructField("x",new RawPrim("156")),
								new RawStructField("y",new RawPrim("4657")),
								new RawStructField("s",new RawPrim("AStringforS"))})))));		
	}
	
	@Test
	public void testList_PrimList_Prim(){
	/*======= Test Case : List[t] || List[t], && t = Prim */
	//Asking for a list of ints have a list of ints
	ListSchema listfld3 = new ListSchema("","",new PrimSchema());
	ListSig listSig = new ListSig(INT_SIG);
	IDataOp<Stream<Integer>> listOints = unifier.unifyWith(listfld3, listSig);
	IDataAccess[] testData = new IDataAccess[10];
	List<Integer> l = new ArrayList<Integer>();
	for(int j = 0; j < testData.length; j++){
		testData[j] = new RawStruct(new RawStructField("",new RawPrim(""+j)));
		l.add(j);
	}
	assertEquals(l,listOints.apply(new RawList("",testData)).collect(Collectors.toList()));
	}
	
	@Test
	public void testList_CompList_Comp(){
		/*======== Test Case : List[t] || List[t], && t = Comp{Prim's only} */
		CompSchema foo = new CompSchema(new CompField("n",new PrimSchema()));
		ListSchema list = new ListSchema("","",foo);
		ListSig listSig2 = new ListSig(FOO_SIG);
		IDataOp<Stream<Foo>> listOFoos = unifier.unifyWith(list, listSig2);
		IDataAccess[] testData2 = new IDataAccess[10];
		List<Foo> l = new ArrayList<Foo>();
		for(int j = 0; j < testData2.length; j++){
			l.add(new Foo(j));
			testData2[j] = new RawStruct(
					new RawStructField("",new RawStruct(				//TODO Extra wrap up?...
							new RawStructField("n",new RawPrim(""+j)))));
		}
		assertEquals(l,listOFoos.apply(new RawList("",testData2)).collect(Collectors.toList()));
	}


	static class Foo {
		int n;
		public Foo(int n) {
			this.n = n;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + n;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Foo other = (Foo) obj;
			if (n != other.n)
				return false;
			return true;
		}

	}

	static class FooBar{
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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + n;
			result = prime * result + ((s == null) ? 0 : s.hashCode());
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FooBar other = (FooBar) obj;
			if (n != other.n)
				return false;
			if (s == null) {
				if (other.s != null)
					return false;
			} else if (!s.equals(other.s))
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

	}

	static class Bar{
		int x,y;
		String s;
		public Bar(int x, int y, String s) {
			this.x = x;
			this.y = y;
			this.s = s;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((s == null) ? 0 : s.hashCode());
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Bar other = (Bar) obj;
			if (s == null) {
				if (other.s != null)
					return false;
			} else if (!s.equals(other.s))
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		@Override
		public String toString(){
			return "Bar : \n\tx : "+this.x+"\n\ty : "+this.y+"\n\ts : "+this.s;
		}
	}
	static class FooToo{
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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((foo == null) ? 0 : foo.hashCode());
			result = prime * result + too;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FooToo other = (FooToo) obj;
			if (foo == null) {
				if (other.foo != null)
					return false;
			} else if (!foo.equals(other.foo))
				return false;
			if (too != other.too)
				return false;
			return true;
		}
	}
}
