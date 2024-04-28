public class QuadTree {
    private static final int MAX_CAPACITY = 100_000_000;
    private final int level;
    private final List<Place> places;
    private final int x, y, width, height;
    private final QuadTree[] children;

    public QuadTree(int level, int x, int y, int width, int height) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.places = new ArrayList<>();
        this.children = new QuadTree[4];
        init();
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
            throw new IllegalArgumentException("Place is out of the bounds of the quad tree");
        }
        if (children[0] != null) {
            int index = getIndex(place.x, place.y);
            children[index].insert(place);
        } else {
            if (places.size() >= MAX_CAPACITY) {
                throw new RuntimeException("Exceeded maximum capacity at a single point");
            }
            places.add(place);
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
}