public class QuadTree {
    private static final int MAX_CAPACITY = 50_000_000;
    private final int level;
    private final int x, y, width, height;
    private final QuadTree[] children;
    private AVL<Place> places;

    public QuadTree(int level, int x, int y, int width, int height) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.places = new AVL<>();
        this.children = new QuadTree[4];
        init(); // Ensure this handles the recursive splitting logic if needed
    }

    private void init() {
        // Recursively split until the potential place count per area becomes manageable
        if (width * height / MAX_CAPACITY > 1 && width > 1 && height > 1) {
            split();
            for (QuadTree child : children) {
                child.init(); // Initialize each child recursively
            }
        }
    }

    private void split() {
        int subWidth = width / 2;
        int subHeight = height / 2;

        children[0] = new QuadTree(level + 1, x + subWidth, y, subWidth, subHeight);
        children[1] = new QuadTree(level + 1, x, y, subWidth, subHeight);
        children[2] = new QuadTree(level + 1, x, y + subHeight, subWidth, subHeight);
        children[3] = new QuadTree(level + 1, x + subWidth, y + subHeight, subWidth, subHeight);
    }

    public void insert(Place place) {
        if (!contains(place.x, place.y)) {
            throw new IllegalArgumentException("Place is out of bounds of the quad tree");
        }
        if (children[0] == null) { // No children, insert here
            places.add(place);
        } else { // Otherwise, find the correct child to insert into
            int index = getIndex(place.x, place.y);
            children[index].insert(place);
        }
    }

    private int getIndex(int px, int py) {
        double verticalMidpoint = x + width / 2.0;
        double horizontalMidpoint = y + height / 2.0;
        boolean topQuadrant = (py < horizontalMidpoint);
        boolean leftQuadrant = (px < verticalMidpoint);

        if (leftQuadrant) {
            return topQuadrant ? 1 : 2;
        } else {
            return topQuadrant ? 0 : 3;
        }
    }

    private boolean contains(int px, int py) {
        return px >= x && py >= y && px < x + width && py < y + height;
    }

    public boolean delete(Place place) {
        if (!contains(place.x, place.y)) {
            return false;
        }
        if (children[0] == null) {
            // Use the AVL remove method
            return places.remove(place) != null;
        } else {
            int index = getIndex(place.x, place.y);
            return children[index].delete(place);
        }
    }

    public boolean addService(Place place, ServiceType service) {
        if (findPlace(place)) {
            place.addService(service);
            return true;
        }
        return false;
    }

    public boolean removeService(Place place, ServiceType service) {
        if (findPlace(place)) {
            place.removeService(service);
            return true;
        }
        return false;
    }

    private boolean findPlace(Place target) {
        return findPlaceHelper(target, this);
    }

    private boolean findPlaceHelper(Place target, QuadTree node) {
        if (node == null) {
            return false;
        }

        // Check the current node's AVL tree
        if (node.places.search(target) != null) {
            return true; // Found the target in the current node's AVL
        }

        // Otherwise, move on to the correct child node
        if (node.children[0] != null) {
            int index = node.getIndex(target.x, target.y);
            return findPlaceHelper(target, node.children[index]);
        }

        return false;
    }

    public List<Place> query(Rectangle range, List<Place> found) {
        if (!intersects(range.x, range.y, range.width, range.height)) {
            return found;
        }
        // Collect places from the current node's AVL tree
        places.inOrderCollect(range, found);

        // Recursively collect from children if they exist
        if (children[0] != null) {
            for (QuadTree child : children) {
                child.query(range, found);
            }
        }
        return found;
    }


    private boolean intersects(int otherX, int otherY, int otherWidth, int otherHeight) {
        return !(otherX > x + width || otherX + otherWidth < x || otherY > y + height || otherY + otherHeight < y);
    }
}
