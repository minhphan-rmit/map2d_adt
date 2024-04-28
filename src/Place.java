public class Place implements Comparable<Place> {
    int x, y;
    long services; // Bitmask for services

    public Place(int x, int y) {
        this.x = x;
        this.y = y;
        this.services = 0; // No services initially
    }

    @Override
    public int compareTo(Place other) {
        if (this.x != other.x) {
            return Integer.compare(this.x, other.x);
        }
        return Integer.compare(this.y, other.y);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

    public void addService(ServiceType service) {
        services |= (1L << service.ordinal());
    }

    public void removeService(ServiceType service) {
        services &= ~(1L << service.ordinal());
    }

    public boolean offersService(ServiceType service) {
        return (services & (1L << service.ordinal())) != 0;
    }
}