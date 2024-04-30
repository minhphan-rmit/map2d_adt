import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class Main {
    private static final Random random = new Random();
    private static final int MAP_SIZE = 10_000_000;
    private static final int NUM_PLACES = 100_000_000; // Number of random places

    public static void main(String[] args) {
        Map2D map = new Map2D();
        ServiceType[] serviceTypes = ServiceType.values(); // Using the enum values directly
        long startTime, endTime;
        Runtime runtime = Runtime.getRuntime();

        try {
            System.out.println("Starting to add random places...");
            startTime = System.currentTimeMillis();
            runtime.gc();
            long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();

            for (int i = 0; i < NUM_PLACES; i++) {
                int x = random.nextInt(MAP_SIZE);
                int y = random.nextInt(MAP_SIZE);
                EnumSet<ServiceType> services = EnumSet.of(serviceTypes[random.nextInt(serviceTypes.length)]);
                Place place = new Place(x, y, services);
                map.add(place);
            }

            // Adding a controlled test place within search bounds
            Place controlledPlace = new Place(3000, 3000, EnumSet.of(ServiceType.CAFE)); // Use EnumSet for specific services
            map.add(controlledPlace);

            long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
            endTime = System.currentTimeMillis();
            System.out.println("Places added. Total time: " + (endTime - startTime) + " ms");
            System.out.println("Memory used: " + (usedMemoryAfter - usedMemoryBefore) / 1024 / 1024 + " MB");

            // Perform a sample search
            System.out.println("Starting search operation...");
            startTime = System.currentTimeMillis();
            int minX = 2000, minY = 2000, maxX = 500_000, maxY = 500_000;
            List<Place> foundPlaces = map.search(minX, minY, maxX, maxY, String.valueOf(ServiceType.CAFE));
            endTime = System.currentTimeMillis();
            System.out.println("Search results: Found " + foundPlaces.size() + " places in " + (endTime - startTime) + " ms");

            System.out.println("Editing a found place...");
            startTime = System.currentTimeMillis();
            map.edit(controlledPlace.x, controlledPlace.y, EnumSet.of(ServiceType.RESTAURANT, ServiceType.CAFE));
            endTime = System.currentTimeMillis();
            System.out.println("Edit operation time: " + (endTime - startTime) + " ms");

            System.out.println("Removing a found place...");
            startTime = System.currentTimeMillis();
            boolean removed = map.remove(controlledPlace.x, controlledPlace.y);
            endTime = System.currentTimeMillis();
            System.out.println("Remove operation was " + (removed ? "successful" : "unsuccessful") + " and took " + (endTime - startTime) + " ms");

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

