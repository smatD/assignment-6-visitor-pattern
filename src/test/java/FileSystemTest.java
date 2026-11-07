import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class FileSystemTest {
    private DirectoryNode tree() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode alpha = new DirectoryNode("alpha");
        DirectoryNode nested = new DirectoryNode("nested");
        root.addChild(new FileNode("top.txt", 10));
        root.addChild(alpha);
        alpha.addChild(new FileNode("a.txt", 20));
        alpha.addChild(new FileNode("b.log", 30));
        alpha.addChild(nested);
        nested.addChild(new FileNode("z.txt", 40));
        return root;
    }

    @Test
    void doubleDispatchSelectsCorrectOverload() {
        List<String> seen = new ArrayList<>();
        Visitor visitor = new Visitor() {
            public void visit(FileNode file) { seen.add("file:" + file.getName()); }
            public void visit(DirectoryNode directory) { seen.add("directory:" + directory.getName()); }
            public void reset() { seen.clear(); }
            public void setLocation(String path, int depth) { }
        };
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new FileNode("data", 1));
        new TreeWalker().walk(root, visitor);
        assertEquals(List.of("directory:root", "file:data"), seen);
    }

    @Test
    void totalSizeCountsEachFileAtEveryLevelOnce() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        new TreeWalker().walk(tree(), visitor);
        assertEquals(100, visitor.getTotalSize());
    }

    @Test
    void searchCombinesNameAndInclusiveMinimum() {
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".txt"), 20);
        new TreeWalker().walk(tree(), visitor);
        assertEquals(List.of("alpha/a.txt", "alpha/nested/z.txt"), visitor.getPaths());
    }

    @Test
    void pathsAreSortedRelativeAndUseForwardSlashes() {
        SearchVisitor visitor = new SearchVisitor(name -> true, 0);
        new TreeWalker().walk(tree(), visitor);
        assertEquals(List.of("alpha/a.txt", "alpha/b.log", "alpha/nested/z.txt", "top.txt"), visitor.getPaths());
    }

    @Test
    void statisticsIncludeRootAndDeepestElement() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        new TreeWalker().walk(tree(), visitor);
        assertEquals(4, visitor.getFileCount());
        assertEquals(3, visitor.getDirectoryCount());
        assertEquals(3, visitor.getMaximumDepth());
    }

    @Test
    void emptyDirectoryHasZeroSizeAndRootDepth() {
        DirectoryNode empty = new DirectoryNode("empty");
        TotalSizeVisitor size = new TotalSizeVisitor();
        StatisticsVisitor stats = new StatisticsVisitor();
        new TreeWalker().walk(empty, size);
        new TreeWalker().walk(empty, stats);
        assertEquals(0, size.getTotalSize());
        assertEquals(0, stats.getFileCount());
        assertEquals(1, stats.getDirectoryCount());
        assertEquals(0, stats.getMaximumDepth());
    }

    @Test
    void noMatchAndSingleFileRootWork() {
        SearchVisitor search = new SearchVisitor(name -> name.endsWith(".pdf"), 0);
        new TreeWalker().walk(tree(), search);
        assertTrue(search.getPaths().isEmpty());
        new TreeWalker().walk(new FileNode("one.pdf", 0), search);
        assertEquals(List.of(""), search.getPaths());
    }

    @Test
    void reusedVisitorsResetBetweenRuns() {
        TreeWalker walker = new TreeWalker();
        TotalSizeVisitor size = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(name -> true, 0);
        StatisticsVisitor stats = new StatisticsVisitor();
        DirectoryNode root = tree();
        for (int i = 0; i < 2; i++) {
            walker.walk(root, size);
            walker.walk(root, search);
            walker.walk(root, stats);
            assertEquals(100, size.getTotalSize());
            assertEquals(4, search.getPaths().size());
            assertEquals(4, stats.getFileCount());
        }
    }

    @Test
    void invalidMutationsAreRejectedAndValidNestingWorks() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode child = new DirectoryNode("child");
        root.addChild(child);
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad", -1));
        assertThrows(IllegalArgumentException.class, () -> new FileNode("a/b", 1));
        assertThrows(IllegalArgumentException.class, () -> root.addChild(new FileNode("child", 1)));
        assertThrows(IllegalArgumentException.class, () -> child.addChild(root));
        assertThrows(IllegalArgumentException.class, () -> new DirectoryNode("other").addChild(child));
        assertThrows(IllegalArgumentException.class, () -> root.addChild(root));
        child.addChild(new FileNode("valid", 5));
        assertEquals(1, child.getChildren().size());
        assertThrows(UnsupportedOperationException.class, () -> child.getChildren().clear());
    }

    @Test
    void visitorResultDoesNotDependOnOperationOrder() {
        DirectoryNode root = tree();
        TreeWalker walker = new TreeWalker();
        StatisticsVisitor stats = new StatisticsVisitor();
        TotalSizeVisitor size = new TotalSizeVisitor();
        walker.walk(root, stats);
        walker.walk(root, size);
        assertEquals(100, size.getTotalSize());
        assertEquals(3, stats.getMaximumDepth());
    }
}
