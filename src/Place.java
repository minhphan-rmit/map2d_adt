public class Place {
    int x, y;
    long services; // Bitmask for services

    public Place(int x, int y) {
        this.x = x;
        this.y = y;
        this.services = 0; // No services initially
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