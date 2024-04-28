import java.util.Random;

public class Main {
    private static final int NUM_POINTS = 50_000_000; // Adjusted for practical testing
    private static final int MAX_COORDINATE = 10_000_000; // More reasonable coordinate value for testing

    public static void main(String[] args) {
        // Initialize the QuadTree with bounds large enough to contain all points
        QuadTree quadTree = new QuadTree(0, 0, 0, MAX_COORDINATE, MAX_COORDINATE);

        // Systematically generate places
        long systematicInsertionStart = System.currentTimeMillis();
        int pointsPerLine = (int) Math.sqrt(NUM_POINTS);
        for (int i = 10; i < pointsPerLine; i++) {
            for (int j = 10; j < pointsPerLine; j++) {
                int x = (MAX_COORDINATE / pointsPerLine) * i;
                int y = (MAX_COORDINATE / pointsPerLine) * j;
                Place place = new Place(x, y);
                place.addService(ServiceType.values()[(i + j) % ServiceType.values().length]); // Cycle through service types
                quadTree.insert(place);
            }
        }
        long systematicInsertionEnd = System.currentTimeMillis();
        System.out.println("Systematic insertion time: " + (systematicInsertionEnd - systematicInsertionStart) + " ms");

        // Test inserting an additional place
        long startTime = System.currentTimeMillis();
        Place additionalPlace = new Place(9, 9);
        additionalPlace.addService(ServiceType.ATM);
        quadTree.insert(additionalPlace);
        long endTime = System.currentTimeMillis();
        System.out.println("Insert additional place time: " + (endTime - startTime) + " ms");

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
}
