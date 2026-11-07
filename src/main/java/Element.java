public interface Element {
    String getName();
    void accept(Visitor visitor);
    Iterable<? extends Element> children();
}
