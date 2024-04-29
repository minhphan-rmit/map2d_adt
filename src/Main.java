import java.util.Random;

public class Main {
    private static final int NUM_POINTS = 100_000_000; // Adjusted for practical testing
    private static final int MAX_COORDINATE = 10_000_000; // More reasonable coordinate value for testing

    public static void main(String[] args) {
        // Initialize the QuadTree with bounds large enough to contain all points
        QuadTree quadTree = new QuadTree(0, 0, 0, MAX_COORDINATE, MAX_COORDINATE, 7);
        System.out.println("QuadTree initialized");

        // Systematically generate places
        long systematicInsertionStart = System.currentTimeMillis();
        logMemoryUsage();
        int pointsPerLine = (int) Math.sqrt(NUM_POINTS);
        for (int i = 10; i < pointsPerLine; i++) {
            for (int j = 10; j < pointsPerLine; j++) {
                int x = (MAX_COORDINATE / pointsPerLine) * i;
                int y = (MAX_COORDINATE / pointsPerLine) * j;
                Place place = new Place(x, y);
                place.addService(ServiceType.values()[(i + j) % ServiceType.values().length]); // Cycle through service types
                quadTree.insert(place);

                if ((i * pointsPerLine + j) % 1_000_000 == 0) {  // Log every millionth insertion
                    logMemoryUsage();
                    System.out.println("Inserted " + (i * pointsPerLine + j) + " points");
                }
            }
        }
        logMemoryUsage();
        long systematicInsertionEnd = System.currentTimeMillis();
        System.out.println("Systematic insertion time: " + (systematicInsertionEnd - systematicInsertionStart) + " ms");

        // Test inserting an additional place
        logMemoryUsage();
        long startTime = System.currentTimeMillis();
        Place additionalPlace = new Place(9, 9);
        additionalPlace.addService(ServiceType.ATM);
        quadTree.insert(additionalPlace);
        long endTime = System.currentTimeMillis();
        System.out.println("Insert additional place time: " + (endTime - startTime) + " ms");

        // Test inserting an additional place
        long startTime2 = System.currentTimeMillis();
        Place additionalPlace2 = new Place(9_999_999, 9_999_999);
        additionalPlace.addService(ServiceType.ATM);
        quadTree.insert(additionalPlace2);
        long endTime2 = System.currentTimeMillis();
        System.out.println("Insert additional place time: " + (endTime2 - startTime2) + " ms");

        // Test deleting a place
        startTime = System.currentTimeMillis();
        boolean deletionResult = quadTree.delete(additionalPlace);
        endTime = System.currentTimeMillis();
        System.out.println("Deletion time: " + (endTime - startTime) + " ms");
        System.out.println("Deletion was " + (deletionResult ? "successful" : "unsuccessful"));

        // Test adding a service
        startTime = System.currentTimeMillis();
        quadTree.addService(additionalPlace.x, additionalPlace.y, ServiceType.BANK);
        endTime = System.currentTimeMillis();
        System.out.println("Add service time: " + (endTime - startTime) + " ms");

        // Test removing a service
        startTime = System.currentTimeMillis();
        quadTree.removeService(additionalPlace.x, additionalPlace.y, ServiceType.BANK);
        endTime = System.currentTimeMillis();
        System.out.println("Remove service time: " + (endTime - startTime) + " ms");

        // Test querying
        startTime = System.currentTimeMillis();
        List<int[]> found = quadTree.findPlacesByService(5_000_000, 5_000_000, 100_000, ServiceType.ATM);
        endTime = System.currentTimeMillis();
        System.out.println("Query time: " + (endTime - startTime) + " ms");
        System.out.println("Number of places found: " + found.size());
    }

    public static void logMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used memory in megabytes: " + bytesToMegabytes(memory));
    }

    private static long bytesToMegabytes(long bytes) {
        final long MEGABYTE = 1024L * 1024L;
        return bytes / MEGABYTE;
    }
}


