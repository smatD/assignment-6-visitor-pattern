# ASSIGNMENT 06 Visitor Pattern – Advanced File System

Java 17, Maven, JUnit 5.

## How to run

From this folder:

```
mvn test
mvn compile exec:java
```

## Design

FileNode stores a name and size. DirectoryNode can have other files and directories inside it. It rejects duplicate names, multiple parents and cycles. File sizes cannot be negative.

The Element interface has accept, and FileNode and DirectoryNode call the matching visit method in Visitor. This is double dispatch. There are no type-checking chains.

TreeWalker goes through the tree once per operation. It starts with the root at depth 0 and then visits children in alphabetical order. The root path is empty, and paths inside it use / between names. Search results are sorted alphabetically too.

I chose to put traversal in TreeWalker so the visitors only do their own jobs:

- TotalSizeVisitor adds file sizes.
- SearchVisitor finds files using a filename condition and minimum size.
- StatisticsVisitor counts files, directories and the maximum depth.

TreeWalker resets the visitor every time, so a visitor can be used again without keeping old results. Adding StatisticsVisitor did not require editing the file and directory classes. Adding a new element type would be harder because all visitors would need a new visit method.

## Output

```
Total bytes: 590
Java files >= 100 bytes: [src/App.java]
Files: 4
Directories: 3
Maximum depth: 2
```

## Tests

FileSystemTest has 10 tests. They check double dispatch, size, search filters and paths, statistics, empty directories, no matches, visitor reuse, invalid changes and operation order.

## Sources

- Refactoring.Guru, Visitor: https://refactoring.guru/design-patterns/visitor
- Refactoring.Guru, Visitor and Double Dispatch: https://refactoring.guru/design-patterns/visitor-double-dispatch
- Apache Maven, Targeting a Java Version: https://maven.apache.org/guides/mini/guide-java-target.html
- JUnit 5 User Guide: https://docs.junit.org/5.11.4/user-guide/