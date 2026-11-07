public final class Main {
    public static void main(String[] args) {
        DirectoryNode project = new DirectoryNode("project");
        DirectoryNode src = new DirectoryNode("src");
        DirectoryNode docs = new DirectoryNode("docs");
        project.addChild(src);
        project.addChild(docs);
        project.addChild(new FileNode("README.md", 120));
        src.addChild(new FileNode("App.java", 240));
        src.addChild(new FileNode("Utils.java", 80));
        docs.addChild(new FileNode("guide.md", 150));

        TreeWalker walker = new TreeWalker();
        TotalSizeVisitor size = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(name -> name.endsWith(".java"), 100);
        StatisticsVisitor stats = new StatisticsVisitor();
        walker.walk(project, size);
        walker.walk(project, search);
        walker.walk(project, stats);
        System.out.println("Total bytes: " + size.getTotalSize());
        System.out.println("Java files >= 100 bytes: " + search.getPaths());
        System.out.println("Files: " + stats.getFileCount());
        System.out.println("Directories: " + stats.getDirectoryCount());
        System.out.println("Maximum depth: " + stats.getMaximumDepth());
    }
}
