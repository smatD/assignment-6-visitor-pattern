import java.util.Objects;

public final class TreeWalker {
    public void walk(Element root, Visitor visitor) {
        Objects.requireNonNull(root);
        Objects.requireNonNull(visitor);
        visitor.reset();
        walkNode(root, visitor, "", 0);
    }

    private void walkNode(Element node, Visitor visitor, String path, int depth) {
        visitor.setLocation(path, depth);
        node.accept(visitor);
        for (Element child : node.children()) {
            String childPath = path.isEmpty() ? child.getName() : path + "/" + child.getName();
            walkNode(child, visitor, childPath, depth + 1);
        }
    }
}
