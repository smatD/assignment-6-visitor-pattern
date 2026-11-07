import java.util.Objects;
import java.util.List;

public abstract class Node implements Element {
    private final String name;
    private DirectoryNode parent;

    protected Node(String name) {
        if (name == null || name.isBlank() || name.equals(".") || name.equals("..") || name.contains("/")) {
            throw new IllegalArgumentException("Invalid node name");
        }
        this.name = name;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public Iterable<? extends Element> children() {
        return List.of();
    }

    final DirectoryNode getParent() {
        return parent;
    }

    final void attach(DirectoryNode directory) {
        Objects.requireNonNull(directory);
        if (parent != null) {
            throw new IllegalArgumentException("Node already has a parent");
        }
        for (DirectoryNode ancestor = directory; ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor == this) {
                throw new IllegalArgumentException("Cycle is not allowed");
            }
        }
        parent = directory;
    }
}
