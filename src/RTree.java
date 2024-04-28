// Define a node in the R-tree
class RTreeNode {
    boolean isLeaf;
    Rectangle rectangle;
    List<RTreeNode> children;
    List<Object> entries;

    public RTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.rectangle = null;
        this.children = new ArrayList<>();
        this.entries = new ArrayList<>();
    }

    // Insert a new entry into this node
    void insert(Rectangle rect, Object entry) {
        if (isLeaf) {
            if (rectangle == null) rectangle = new Rectangle(rect.x, rect.y, rect.width, rect.height);
            else rectangle.expand(rect);
            entries.add(entry);
        } else {
            // Here, we should choose the best child to propagate the insertion
            RTreeNode chosen = children.get(0); // Simplified: real implementation needs better child selection
            chosen.insert(rect, entry);
            rectangle.expand(rect);
        }
    }

    // Search for entries that intersect with the given rectangle
    List<Object> search(Rectangle searchRect) {
        List<Object> results = new ArrayList<>();
        if (rectangle != null && rectangle.intersects(searchRect)) {
            if (isLeaf) {
                for (Object entry : entries) {
                    // Assume entry has a rectangle and check intersection
                    // Here we just add all entries for simplification
                    results.add(entry);
                }
            } else {
                for (RTreeNode child : children) {
                    results.addAll(child.search(searchRect));
                }
            }
        }
        return results;
    }
}

// R-Tree main class
public class RTree {
    private RTreeNode root;

    public RTree() {
        root = new RTreeNode(false); // Start with non-leaf root
    }

    public void insert(Rectangle rect, Object entry) {
        root.insert(rect, entry);
    }

    public List<Object> search(Rectangle rect) {
        return root.search(rect);
    }
}

