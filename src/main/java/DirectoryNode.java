import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public final class DirectoryNode extends Node {
    private final Map<String, Node> children = new TreeMap<>();

    public DirectoryNode(String name) {
        super(name);
    }

    public void addChild(Node child) {
        if (child == null) {
            throw new IllegalArgumentException("Child cannot be null");
        }
        if (children.containsKey(child.getName())) {
            throw new IllegalArgumentException("Duplicate sibling name: " + child.getName());
        }
        child.attach(this);
        children.put(child.getName(), child);
    }

    public Collection<Node> getChildren() {
        return Collections.unmodifiableCollection(children.values());
    }

    @Override
    public Iterable<? extends Element> children() {
        return getChildren();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
