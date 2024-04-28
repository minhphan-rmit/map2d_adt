import java.util.Random;

public class Main {
    private static final int NUM_POINTS = 50_000_000; // Adjust the number for practical testing
    private static final int MAX_COORDINATE = 10_000_000; // Maximum coordinate value
    public static Random random = new Random();

    public static void main(String[] args) {
        // Initialize the QuadTree with bounds large enough to contain all points
        QuadTree quadTree = new QuadTree(0, 0, 0, MAX_COORDINATE, MAX_COORDINATE);

        // Inserting places
        for (int i = 0; i < NUM_POINTS - 1; i++) {
            int x = 10 + random.nextInt(MAX_COORDINATE - 10);
            int y = 10 + random.nextInt(MAX_COORDINATE - 10);
            Place place = new Place(x, y);
            place.addService(ServiceType.values()[random.nextInt(ServiceType.values().length)]);
            quadTree.insert(place);
        }

        // Memory and runtime measurement setup
        Runtime runtime = Runtime.getRuntime();
        long startMemoryUse, endMemoryUse;
        long startTime, endTime;

        // Testing add additional place
        runtime.gc();
        startTime = System.currentTimeMillis();
        startMemoryUse = runtime.totalMemory() - runtime.freeMemory();
        Place additionalPlace = new Place(100, 100);
        quadTree.insert(additionalPlace);
        endTime = System.currentTimeMillis();
        endMemoryUse = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Insert additional place time: " + (endTime - startTime) + " ms");
        System.out.println("Memory used for inserting additional place: " + ((endMemoryUse - startMemoryUse) / 1024) + " KB");

        // Testing delete
        Place placeToDelete = additionalPlace;
        runtime.gc();
        startMemoryUse = runtime.totalMemory() - runtime.freeMemory();
        startTime = System.currentTimeMillis();

        quadTree.delete(placeToDelete);

        endTime = System.currentTimeMillis();
        endMemoryUse = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Deletion time: " + (endTime - startTime) + " ms");
        System.out.println("Memory used for deletion: " + ((endMemoryUse - startMemoryUse) / 1024) + " KB");

        // Testing addService
        Place testPlace = new Place(100, 100);
        quadTree.insert(additionalPlace);
        Place placeToAddService = testPlace;
        runtime.gc();
        startMemoryUse = runtime.totalMemory() - runtime.freeMemory();
        startTime = System.currentTimeMillis();

        quadTree.addService(placeToAddService, ServiceType.ATM);

        endTime = System.currentTimeMillis();
        endMemoryUse = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Add service time: " + (endTime - startTime) + " ms");
        System.out.println("Memory used for adding service: " + ((endMemoryUse - startMemoryUse) / 1024) + " KB");

        // Testing removeService
        Place placeToRemoveService = testPlace;
        runtime.gc();
        startMemoryUse = runtime.totalMemory() - runtime.freeMemory();
        startTime = System.currentTimeMillis();

        quadTree.removeService(placeToRemoveService, ServiceType.ATM);

        endTime = System.currentTimeMillis();
        endMemoryUse = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Remove service time: " + (endTime - startTime) + " ms");
        System.out.println("Memory used for removing service: " + ((endMemoryUse - startMemoryUse) / 1024) + " KB");

        // Testing query
        Rectangle queryRectangle = new Rectangle(0, 0, 5000, 5000);
        runtime.gc();
        startMemoryUse = runtime.totalMemory() - runtime.freeMemory();
        startTime = System.currentTimeMillis();

        List<Place> foundPlaces = quadTree.query(queryRectangle, new ArrayList<>());

        endTime = System.currentTimeMillis();
        endMemoryUse = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Query time: " + (endTime - startTime) + " ms");
        System.out.println("Memory used for query: " + ((endMemoryUse - startMemoryUse) / 1024) + " KB");
        System.out.println("Number of places found: " + foundPlaces.size());
    }
}
