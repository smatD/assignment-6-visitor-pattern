public interface Visitor {
    void visit(FileNode file);
    void visit(DirectoryNode directory);
    void reset();
    void setLocation(String path, int depth);
}
