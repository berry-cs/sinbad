package core.schema;


/**
 * A visitor on ISchema objects
 *
 * @param <T> the return type of the operation represented by this visitor
 */
public interface ISchemaVisitor<T> {
    public T defaultVisit(ISchema s);
    public T visit(PrimSchema s);
    public T visit(CompSchema s);
    public T visit(ListSchema s);
}
