import java.util.Random;

public class Main {
    private static final int NUM_POINTS = 10_000_000; // Adjusted for practical testing
    private static final int MAX_COORDINATE = 10_000_000; // More reasonable coordinate value for testing

    public static void main(String[] args) {
        Random random = new Random();

        // Initialize the QuadTree with bounds large enough to contain all points
        QuadTree quadTree = new QuadTree(0, 0, 0, MAX_COORDINATE, MAX_COORDINATE);

        // Inserting random places
        for (int i = 0; i < NUM_POINTS; i++) {
            int x = random.nextInt(MAX_COORDINATE);
            int y = random.nextInt(MAX_COORDINATE);
            Place place = new Place(x, y);
            place.addService(ServiceType.values()[random.nextInt(ServiceType.values().length)]);
            quadTree.insert(place);
        }

        System.out.println("Initial insertion completed.");

        // Test inserting an additional place
        long startTime = System.currentTimeMillis();
        Place additionalPlace = new Place(100, 100);
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
        List<int[]> found = quadTree.findPlacesByService(5_000_000, 5_000_000, 1_000_000, ServiceType.ATM);
        endTime = System.currentTimeMillis();
        System.out.println("Query time: " + (endTime - startTime) + " ms");
        System.out.println("Number of places found: " + found.size());
    }
}
