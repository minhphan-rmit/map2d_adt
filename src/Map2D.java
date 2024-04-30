import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

// Define services as an Enum for memory efficiency
enum ServiceType {
    RESTAURANT, HOTEL, SUPERMARKET, GAS_STATION, CAFE, HOSPITAL, GYM, LIBRARY, SCHOOL, BANK;
}

class Place {
    int x, y; // Coordinates
    EnumSet<ServiceType> services; // Services offered using EnumSet for memory efficiency

    public Place(int x, int y, EnumSet<ServiceType> services) {
        this.x = x;
        this.y = y;
        this.services = services;
    }
}

// Define the R-tree node
class RTreeNode {
    boolean isLeaf;
    List<RTreeNode> children;
    List<Place> places; // This should always be initialized to prevent null reference errors.
    int minX, minY, maxX, maxY;

    // Constructor for internal node
    RTreeNode() {
        this.children = new ArrayList<>();
        this.places = new ArrayList<>(); // Initialize the places list even if it might not be used.
        this.isLeaf = false;
        resetBounds();
    }

    // Constructor for leaf node
    RTreeNode(Place place) {
        this.places = new ArrayList<>();
        this.places.add(place);
        this.children = new ArrayList<>(); // Even if not used, initializing to avoid potential null references.
        this.isLeaf = true;
        updateBounds(place);
    }

    // Update bounding box of the node
    void updateBounds(Place place) {
        if (minX > place.x) minX = place.x;
        if (maxX < place.x) maxX = place.x;
        if (minY > place.y) minY = place.y;
        if (maxY < place.y) maxY = place.y;
    }

    void resetBounds() {
        minX = minY = Integer.MAX_VALUE;
        maxX = maxY = Integer.MIN_VALUE;
        if (!isLeaf) {
            for (RTreeNode child : children) {
                updateBounds(child);
            }
        } else {
            for (Place place : places) {
                updateBounds(place);
            }
        }
    }

    void updateBounds(RTreeNode node) {
        if (minX > node.minX) minX = node.minX;
        if (maxX < node.maxX) maxX = node.maxX;
        if (minY > node.minY) minY = node.minY;
        if (maxY < node.maxY) maxY = node.maxY;
    }
}

class Map2D {
    private RTreeNode root;
    static final int MAX_NODE_CAPACITY = 5; // Example max capacity

    public Map2D() {
        root = new RTreeNode();
        root.isLeaf = true; // Initially, root is a leaf with no children
    }

    // Adds a new place into the R-tree
    public void add(Place place) {
        insert(root, new RTreeNode(place));
    }

    private void insert(RTreeNode node, RTreeNode newNode) {
        if (node.isLeaf) {
            node.places.add(newNode.places.get(0));
            node.updateBounds(newNode.places.get(0));
            if (node.places.size() > MAX_NODE_CAPACITY) {
                splitLeafNode(node);
            }
        } else {
            RTreeNode bestChild = chooseBestChild(node, newNode);
            insert(bestChild, newNode);
            node.updateBounds(newNode);
            if (node.children.size() > MAX_NODE_CAPACITY) {
                splitInternalNode(node);
            }
        }
    }

    private void splitLeafNode(RTreeNode node) {
        int halfway = node.places.size() / 2;
        List<Place> firstHalf = new ArrayList<>(node.places.subList(0, halfway));
        List<Place> secondHalf = new ArrayList<>(node.places.subList(halfway, node.places.size()));

        node.places.clear();
        node.places.addAll(firstHalf);
        node.resetBounds();

        RTreeNode newNode = new RTreeNode();
        newNode.isLeaf = true;
        newNode.places.addAll(secondHalf);
        newNode.resetBounds();

        if (node == root) {
            RTreeNode newRoot = new RTreeNode();
            newRoot.isLeaf = false;
            newRoot.children.add(node);
            newRoot.children.add(newNode);
            newRoot.resetBounds();
            root = newRoot;
        } else {
            RTreeNode parent = findParent(root, node);
            parent.children.add(newNode);
            parent.resetBounds();
        }
    }

    private RTreeNode findParent(RTreeNode root, RTreeNode child) {
        if (root.isLeaf) {
            return null;  // Leaf nodes do not have children
        }
        for (RTreeNode node : root.children) {
            if (node == child || node.children.contains(child)) {
                return root;
            } else {
                RTreeNode parent = findParent(node, child);
                if (parent != null) {
                    return parent;
                }
            }
        }
        return null;
    }

    private void splitInternalNode(RTreeNode node) {
        int halfway = node.children.size() / 2;
        List<RTreeNode> firstHalf = new ArrayList<>(node.children.subList(0, halfway));
        List<RTreeNode> secondHalf = new ArrayList<>(node.children.subList(halfway, node.children.size()));

        node.children.clear();
        node.children.addAll(firstHalf);
        node.resetBounds();

        RTreeNode newNode = new RTreeNode();
        newNode.children.addAll(secondHalf);
        newNode.resetBounds();

        if (node == root) {
            RTreeNode newRoot = new RTreeNode();
            newRoot.isLeaf = false;
            newRoot.children.add(node);
            newRoot.children.add(newNode);
            newRoot.resetBounds();
            root = newRoot;
        } else {
            RTreeNode parent = findParent(root, node);
            parent.children.add(newNode);
            parent.resetBounds();
        }
    }

    private RTreeNode chooseBestChild(RTreeNode node, RTreeNode newNode) {
        RTreeNode bestChild = null;
        int bestIncrease = Integer.MAX_VALUE;
        for (RTreeNode child : node.children) {
            int areaIncrease = requiredExpansion(child, newNode.places.get(0));
            if (areaIncrease < bestIncrease) {
                bestIncrease = areaIncrease;
                bestChild = child;
            }
        }
        return bestChild;
    }

    private int requiredExpansion(RTreeNode node, Place place) {
        int areaBefore = (node.maxX - node.minX) * (node.maxY - node.minY);
        int minX = Math.min(node.minX, place.x);
        int maxX = Math.max(node.maxX, place.x);
        int minY = Math.min(node.minY, place.y);
        int maxY = Math.max(node.maxY, place.y);
        int areaAfter = (maxX - minX) * (maxY - minY);
        return areaAfter - areaBefore;
    }

    // Search places in a bounding rectangle with a specific service
    public List<Place> search(int minX, int minY, int maxX, int maxY, String serviceType) {
        return searchInBounds(root, minX, minY, maxX, maxY, serviceType);
    }

    private List<Place> searchInBounds(RTreeNode node, int minX, int minY, int maxX, int maxY, String serviceType) {
        List<Place> results = new ArrayList<>();
        if (!node.isLeaf) {
            for (RTreeNode child : node.children) {
                if (child.maxX >= minX && child.minX <= maxX && child.maxY >= minY && child.minY <= maxY) {
                    results.addAll(searchInBounds(child, minX, minY, maxX, maxY, serviceType));
                }
            }
        } else {
            for (Place place : node.places) {
                if (place.x >= minX && place.x <= maxX && place.y >= minY && place.y <= maxY) {
                    if (place.services.contains(serviceType)) {
                        results.add(place);
                    }
                }
            }
        }
        return results;
    }

    public void edit(int x, int y, EnumSet<ServiceType> newServices) {
        Place place = findPlace(root, x, y);
        if (place != null) {
            place.services = newServices; // Directly replace with the new EnumSet
        }
    }

    private Place findPlace(RTreeNode node, int x, int y) {
        if (node.isLeaf) {
            for (Place place : node.places) {
                if (place.x == x && place.y == y) {
                    return place;
                }
            }
        } else {
            for (RTreeNode child : node.children) {
                if (x >= child.minX && x <= child.maxX && y >= child.minY && y <= child.maxY) {
                    Place found = findPlace(child, x, y);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }
        return null;
    }

    // Remove a place and return true if the place was removed successfully
    public boolean remove(int x, int y) {
        return removePlace(root, x, y);
    }

    private boolean removePlace(RTreeNode node, int x, int y) {
        if (node.isLeaf) {
            // Check if any place was removed and return the result
            boolean isRemoved = node.places.removeIf(place -> place.x == x && place.y == y);
            if (isRemoved && node.places.isEmpty()) {
                // Optionally handle the case where a leaf becomes empty
            }
            return isRemoved;
        } else {
            boolean removed = false;
            // Iterate through children to find and remove the place
            for (RTreeNode child : node.children) {
                if (x >= child.minX && x <= child.maxX && y >= child.minY && y <= child.maxY) {
                    removed = removePlace(child, x, y);
                    if (removed) {
                        break;
                    }
                }
            }

            // Optionally handle the case where an internal node's child becomes empty
            if (removed) {
                node.children.removeIf(child -> child.isLeaf && child.places.isEmpty());
            }

            return removed;
        }
    }
}



