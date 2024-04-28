public class QuadTree {
    private static final int MAX_CAPACITY = 100_000_000;
    private final int level;
    private final HashMap serviceMap;  // Stores places by service type
    private final int x, y, width, height;
    private final QuadTree[] children;

    public QuadTree(int level, int x, int y, int width, int height) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.serviceMap = new HashMap(10);  // Assuming we have about 10 different service types
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
            if (serviceMap.size() >= MAX_CAPACITY) {
                throw new RuntimeException("Exceeded maximum capacity at a single point");
            }
            for (ServiceType service : ServiceType.values()) {
                if (place.offersService(service)) {
                    serviceMap.put(service, place);
                }
            }
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
        if (children[0] != null) {
            int index = getIndex(place.x, place.y);
            return children[index].delete(place);
        } else {
            boolean removed = false;
            for (ServiceType service : ServiceType.values()) {
                if (place.offersService(service)) {
                    ArrayList<Place> places = serviceMap.get(service);
                    if (places != null) {
                        removed = places.remove(place);
                    }
                }
            }
            return removed;
        }
    }

    public boolean addService(Place place, ServiceType service) {
        if (service == null) {
            throw new IllegalArgumentException("ServiceType cannot be null.");
        }
        if (findPlace(place)) {
            place.addService(service);

            ArrayList<Place> places = serviceMap.get(service);
            if (places == null) {
                serviceMap.put(service, place);
            } else {
                if (!places.get(place)) {
                    places.add(place);
                }
            }
            return true;
        }
        return false;
    }



    public boolean removeService(Place place, ServiceType service) {
        if (findPlace(place)) {
            place.removeService(service);
            ArrayList<Place> places = serviceMap.get(service);
            if (places != null) {
                places.remove(place); // Remove the place from the service list
                if (places.isEmpty()) {
                    serviceMap.remove(service); // Optionally remove the service key if no places offer it
                }
            }
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

        ArrayList<Place> places = node.serviceMap.get(target.getPrimaryService()); // Assume getPrimaryService() returns the main service type of the place
        if (places != null) {
            for (Place place : places) {
                if (place.equals(target)) {
                    return true;
                }
            }
        }

        if (node.children[0] != null) {
            int index = node.getIndex(target.x, target.y);
            return findPlaceHelper(target, node.children[index]);
        }

        return false;
    }

    public List<Place> serviceQuery(int userX, int userY, ServiceType serviceType, int k, double maxDistance) {
        Rectangle searchArea = new Rectangle(userX - (int) maxDistance, userY - (int) maxDistance, 2 * (int) maxDistance, 2 * (int) maxDistance);
        List<Place> results = new ArrayList<>();
        query(searchArea, results, serviceType);

        // Create a list to hold filtered places
        List<Place> filteredResults = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            Place place = results.get(i);
            if (distance(userX, userY, place.x, place.y) <= maxDistance) {
                filteredResults.add(place);
            }
        }

        // Sorting manually using a simple sort mechanism (e.g., Bubble Sort, Selection Sort, or Insertion Sort)
        // Here we use a simple insertion sort for demonstration; for large datasets, consider a more efficient sort.
        for (int i = 1; i < filteredResults.size(); i++) {
            Place key = filteredResults.get(i);
            int j = i - 1;

            // Move elements of arr[0..i-1], that are greater than key, to one position ahead
            // of their current position
            while (j >= 0 && distance(userX, userY, filteredResults.get(j).x, filteredResults.get(j).y) >
                    distance(userX, userY, key.x, key.y)) {
                filteredResults.set(j + 1, filteredResults.get(j));
                j = j - 1;
            }
            filteredResults.set(j + 1, key);
        }

        // Return the top k elements
        List<Place> topKResults = new ArrayList<>();
        int count = Math.min(k, filteredResults.size());
        for (int i = 0; i < count; i++) {
            topKResults.add(filteredResults.get(i));
        }

        return topKResults;
    }

    private void query(Rectangle range, List<Place> found, ServiceType serviceType) {
        if (!intersects(range.x, range.y, range.width, range.height)) {
            return;
        }

        ArrayList<Place> places = serviceMap.get(serviceType);
        if (places != null) {
            for (Place place : places) {
                if (range.contains(place.x, place.y)) {
                    found.add(place);
                }
            }
        }

        if (children[0] != null) {
            for (QuadTree child : children) {
                child.query(range, found, serviceType);
            }
        }
    }

    private double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    private boolean intersects(int otherX, int otherY, int otherWidth, int otherHeight) {
        return !(otherX > x + width || otherX + otherWidth < x || otherY > y + height || otherY + otherHeight < y);
    }
}
