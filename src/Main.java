import java.util.Random;

public class Main {
    private static final int NUM_POINTS = 100_000_000;  // Number of points to insert
    private static final int MAX_COORDINATE = 10_000_000; // Maximum coordinate value
    public static Random random = new Random();

    public static void main(String[] args) {
        // Initialize the QuadTree with bounds large enough to contain all points
        QuadTree quadTree = new QuadTree(0, 0, 0, MAX_COORDINATE, MAX_COORDINATE);

        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();

        // Perform a garbage collection before starting the test
        runtime.gc();

        // Memory usage before the operations
        long startMemoryUse = runtime.totalMemory() - runtime.freeMemory();

        // Generate and insert places
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < NUM_POINTS; i++) {
            int x = 10 + random.nextInt(MAX_COORDINATE - 10);
            int y = 10 + random.nextInt(MAX_COORDINATE - 10);
            Place place = new Place(x, y);
            place.addService(generateRandomService());  // Add one random service to each place
            try {
                quadTree.insert(place);
            } catch (IllegalArgumentException e) {
                System.err.println("Failed to insert place: " + e.getMessage());
            }
        }
        long endTime = System.currentTimeMillis();
        long endMemoryUse = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Insertion of " + NUM_POINTS + " places completed in " + (endTime - startTime) + " ms");
        System.out.println("Memory used for insertion: " + ((endMemoryUse - startMemoryUse) / 1024 / 1024) + " MB");
    }

    private static ServiceType generateRandomService() {
        return ServiceType.values()[random.nextInt(ServiceType.values().length)];
    }
}
