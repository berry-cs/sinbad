package core.sig;

/**
 * A visitor on ISig objects
 *
 * @param <T> the return type of the operation represented by this visitor
 */
public interface ISigVisitor<T> {
		public T defaultVisit(ISig s);
		public T visit(PrimSig s);
		public T visit(CompSig<?> s);
		public T visit(ListSig s);
}
