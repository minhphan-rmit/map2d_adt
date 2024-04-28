public class QuadTree {
    private static final int MAX_CAPACITY = 50_000_000;
    private final int level;
    private final KDTree places; // Using KDTree instead of List<Place>
    private final int x, y, width, height;
    private final QuadTree[] children;

    public QuadTree(int level, int x, int y, int width, int height) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.places = new KDTree(); // Initialize KDTree here
        this.children = new QuadTree[4];
        init();
    }

    private void init() {
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
            throw new IllegalArgumentException("Place is out of the bounds of the quad tree");
        }
        if (children[0] != null) {
            int index = getIndex(place.x, place.y);
            children[index].insert(place);
        } else {
            places.add(place); // Adding to KDTree
        }
    }

    public boolean delete(Place place) {
        if (!contains(place.x, place.y)) {
            return false;
        }

        if (children[0] != null) {
            int index = getIndex(place.x, place.y);
            return children[index].delete(place);
        } else {
            return places.delete(place.x, place.y); // Using KDTree for deletion
        }
    }

    public boolean addService(int x, int y, ServiceType service) {
        if (!contains(x, y)) {
            return false;
        }
        if (children[0] != null) {
            int index = getIndex(x, y);
            return children[index].addService(x, y, service);
        } else {
            return places.addServiceToPoint(x, y, service);
        }
    }

    public boolean removeService(int x, int y, ServiceType service) {
        if (!contains(x, y)) {
            return false;
        }
        if (children[0] != null) {
            int index = getIndex(x, y);
            return children[index].removeService(x, y, service);
        } else {
            return places.removeServiceFromPoint(x, y, service);
        }
    }

    public List<int[]> findPlacesByService(int currentX, int currentY, double walkingDistance, ServiceType serviceType) {
        Rectangle searchArea = new Rectangle(currentX - (int) walkingDistance, currentY - (int) walkingDistance,
                (int) (2 * walkingDistance), (int) (2 * walkingDistance));
        ArrayList<int[]> found = new ArrayList<>();
        query(searchArea, serviceType, found);
        return found;
    }

    private void query(Rectangle range, ServiceType serviceType, List<int[]> found) {
        if (!intersects(range)) {
            return;
        }

        if (children[0] == null) {
            // If this is a leaf node, use the KDTree to perform a refined search
            places.searchByServiceWithinBounds(range, serviceType, found);
        } else {
            // Otherwise, recursively query the appropriate children
            for (QuadTree child : children) {
                child.query(range, serviceType, found);
            }
        }
    }

    private boolean contains(int px, int py) {
        return px >= x && py >= y && px < x + width && py < y + height;
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

    private boolean intersects(Rectangle searchArea) {
        return !(searchArea.x + searchArea.width < this.x ||
                searchArea.x > this.x + this.width ||
                searchArea.y + searchArea.height < this.y ||
                searchArea.y > this.y + this.height);
    }
}
