class Place {
    int x, y;
    long services = 0; // Bitmask for services

    public Place(int x, int y) {
        this.x = x;
        this.y = y;
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

    // Ensure correct equality check for deletion and service manipulation
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Place place = (Place) obj;
        return x == place.x && y == place.y;
    }

    public boolean isEqual(int x, int y) {
        return this.x == x && this.y == y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}