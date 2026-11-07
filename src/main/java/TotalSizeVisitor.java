public final class TotalSizeVisitor implements Visitor {
    private long totalSize;

    @Override
    public void visit(FileNode file) {
        totalSize = Math.addExact(totalSize, file.getSize());
    }

    @Override
    public void visit(DirectoryNode directory) {
    }

    @Override
    public void reset() {
        totalSize = 0;
    }

    @Override
    public void setLocation(String path, int depth) {
    }

    public long getTotalSize() {
        return totalSize;
    }
}
