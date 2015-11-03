package core.tests;

import static org.junit.Assert.*;

import java.util.stream.Stream;

import org.junit.Test;

import core.access.*;
import core.ops.*;
import core.sig.*;
import data.raw.*;
import core.schema.*;

public class TestRaw {

    static class Book {
        String title, author;
        Book(String title, String author) { this.title = title; this.author = author; }
        public String toString() { return String.format("%s: %s.", title, author); }
        public boolean equals(Object o) { Book that = (Book) o; return this.title.equals(that.title) && this.author.equals(that.author); } 
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testSSunifyNested() {
        IRawAccess MT = new RawStruct(new RawStructField("first", new RawPrim("Mark")),
                                     new RawStructField("last", new RawPrim("Twain")));
        IRawAccess JRT = new RawStruct(new RawStructField("first", new RawPrim("JRR")),
                                      new RawStructField("last", new RawPrim("Tolkein")));
        IRawAccess da = new RawList("book", 
                new RawStruct(new RawStructField("author", new RawStruct(new RawStructField("name", MT),
                        new RawStructField("yob", new RawPrim("1864")))),
                        new RawStructField("title", new RawPrim("Adventures of TS"))),
                new RawStruct(new RawStructField("author", new RawStruct(new RawStructField("name", JRT),
                        new RawStructField("yob", new RawPrim("1950")))),
                        new RawStructField("title", new RawPrim("The Hobbit"))));
        //new ListSig(new CompSig<String>(String.class, new ArgSpec("author/name", PrimSig.STRING_SIG))));
        ISig sig = new ListSig(
                new CompSig<Book>(Book.class, new ArgSpec("book/title", PrimSig.STRING_SIG),
                                              new ArgSpec("book/author/name/last", PrimSig.STRING_SIG)));
                        //new ArgSpec("book/author", new CompSig<String>(String.class, new ArgSpec("name", PrimSig.STRING_SIG)))));
        System.out.println(da);
        System.out.println(da.getSchema().toString(true));
        System.out.println(sig);
        
        IDataOp<?> dop = SchemaSigUnifier.unifyWith(da.getSchema(), sig);
        System.out.println(dop);
        Stream<IDataOp> ops = (Stream<IDataOp>) dop.apply(da);
        
        ISchema nameSchema = new CompSchema("name", new CompField("first", new PrimSchema("first")), new CompField("last", new PrimSchema("last")));
        ISchema authorSchema = new CompSchema("author", new CompField("name", nameSchema), new CompField("yob", new PrimSchema("yob")));
        ISchema bookListSchema = new ListSchema(new CompSchema("book", new CompField("author", authorSchema),
                                            new CompField("title", new PrimSchema("title"))));
        ISchema daSchema = da.getSchema();
        
        assertEquals( bookListSchema.toString(true), daSchema.toString(true) );
        
        assertArrayEquals( new Book[] { new Book("Adventures of TS", "Twain"), 
                                        new Book("The Hobbit", "Tolkein") }, 
                           ops.toArray(n -> new Book[n]) );
        //for (Book b : ops.toArray(n -> new Book[n])) {
        //    System.out.println(" " + b);
        //}

    }
    
    // NOTE: the point of this test is that some of the field names are *different*
    //       than the actual paths to the data. The IDataOp's that are generated
    //       should use the actual paths, not the field names in the CompField pairs.
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testSSunifyFieldPathDifferentNames() {
        IRawAccess MT = new RawStruct(new RawStructField("first", new RawPrim("Mark")),
                                     new RawStructField("last", new RawPrim("Twain")));
        IRawAccess JRT = new RawStruct(new RawStructField("first", new RawPrim("JRR")),
                                      new RawStructField("last", new RawPrim("Tolkein")));
        IRawAccess da = new RawList("book", 
                new RawStruct(new RawStructField("author", new RawStruct(new RawStructField("name", MT),
                        new RawStructField("yob", new RawPrim("1864")))),
                        new RawStructField("title", new RawPrim("Adventures of TS"))),
                new RawStruct(new RawStructField("author", new RawStruct(new RawStructField("name", JRT),
                        new RawStructField("yob", new RawPrim("1950")))),
                        new RawStructField("title", new RawPrim("The Hobbit"))));

        ISchema nameSchema = new CompSchema("name", new CompField("first-name", new PrimSchema("first")), new CompField("last-name", new PrimSchema("last")));
        ISchema authorSchema = new CompSchema("author", new CompField("name", nameSchema), new CompField("year-of-birth", new PrimSchema("yob")));
        ISchema bookListSchema = new ListSchema(new CompSchema("book", new CompField("author-info", authorSchema),
                                                                       new CompField("book-title", new PrimSchema("title"))));
        
        ISig sig = new ListSig(
                new CompSig<Book>(Book.class, new ArgSpec("book/book-title", PrimSig.STRING_SIG),
                                              new ArgSpec("book/author-info/name/last-name", PrimSig.STRING_SIG)));

        IDataOp<?> dop = SchemaSigUnifier.unifyWith(bookListSchema, sig);
        Stream<IDataOp> ops = (Stream<IDataOp>) dop.apply(da);
        
        assertArrayEquals( new Book[] { new Book("Adventures of TS", "Twain"), 
                                        new Book("The Hobbit", "Tolkein") }, 
                           ops.toArray(n -> new Book[n]) );
    }

}
