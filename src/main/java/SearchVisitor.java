import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class SearchVisitor implements Visitor {
    private final Predicate<String> filenameCriterion;
    private final long minimumSize;
    private final List<String> paths = new ArrayList<>();
    private String currentPath;

    public SearchVisitor(Predicate<String> filenameCriterion, long minimumSize) {
        this.filenameCriterion = Objects.requireNonNull(filenameCriterion);
        if (minimumSize < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }
        this.minimumSize = minimumSize;
    }

    @Override
    public void visit(FileNode file) {
        if (filenameCriterion.test(file.getName()) && file.getSize() >= minimumSize) {
            paths.add(currentPath);
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
    }

    @Override
    public void reset() {
        paths.clear();
        currentPath = "";
    }

    @Override
    public void setLocation(String path, int depth) {
        currentPath = path;
    }

    public List<String> getPaths() {
        return paths.stream().sorted().toList();
    }
}
