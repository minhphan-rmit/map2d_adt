/**
 * Represents a 2D map for managing geographical locations where each location can offer various services.
 * This implementation uses a KD-Tree to efficiently handle spatial queries such as nearest neighbor searches.
 */
public class KDTree {
    private KDTreeNode root = null;

    public void add(Place place) {
        root = add(root, place, true);
    }

    private KDTreeNode add(KDTreeNode node, Place place, boolean vertical) {
        if (node == null) {
            return new KDTreeNode(place);
        }

        if (vertical ? place.x < node.getPlace().x : place.y < node.getPlace().y) {
            node.left = add(node.left, place, !vertical);
        } else {
            node.right = add(node.right, place, !vertical);
        }

        return balance(node, vertical);
    }

    public boolean delete(int x, int y) {
        if (findNode(root, x, y) == null) {
            return false;
        }
        root = delete(root, x, y, true);
        return true;
    }

    private KDTreeNode delete(KDTreeNode node, int x, int y, boolean vertical) {
        if (node == null) {
            return null;
        }

        if (node.getPlace().isEqual(x, y)) {
            if (node.left == null || node.right == null) {
                return node.left != null ? node.left : node.right;
            } else {
                KDTreeNode min = findMin(node.right, !vertical);
                node.getPlace().x = min.getPlace().x;
                node.getPlace().y = min.getPlace().y;
                node.getPlace().services = min.getPlace().services;
                node.right = delete(node.right, min.getPlace().x, min.getPlace().y, !vertical);
            }
        } else if (vertical ? x < node.getPlace().x : y < node.getPlace().y) {
            node.left = delete(node.left, x, y, !vertical);
        } else {
            node.right = delete(node.right, x, y, !vertical);
        }

        return balance(node, vertical);
    }

    private KDTreeNode rotateRight(KDTreeNode y) {
        KDTreeNode x = y.left;
        y.left = x.right;
        x.right = y;
        y.updateHeight();
        x.updateHeight();
        return x;
    }

    private KDTreeNode rotateLeft(KDTreeNode x) {
        KDTreeNode y = x.right;
        x.right = y.left;
        y.left = x;
        x.updateHeight();
        y.updateHeight();
        return y;
    }

    private KDTreeNode balance(KDTreeNode node, boolean vertical) {
        if (node == null) return null;
        node.updateHeight();
        int balance = node.getBalance();

        if (balance > 1) {
            if (node.left.getBalance() < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        } else if (balance < -1) {
            if (node.right.getBalance() > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }
        return node;
    }

    private KDTreeNode findMin(KDTreeNode node, boolean vertical) {
        KDTreeNode current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    public boolean addServiceToPoint(int x, int y, ServiceType service) {
        KDTreeNode node = findNode(root, x, y);
        if (node != null) {
            node.getPlace().addService(service);
            return true;
        }
        return false;
    }

    public boolean removeServiceFromPoint(int x, int y, ServiceType service) {
        KDTreeNode node = findNode(root, x, y);
        if (node != null) {
            node.getPlace().removeService(service);
            return true;
        }
        return false;
    }

    private KDTreeNode findNode(KDTreeNode node, int x, int y) {
        if (node == null) {
            return null;
        }
        if (node.getPlace().isEqual(x, y)) {
            return node;
        } else if ((x < node.getPlace().x) || (x == node.getPlace().x && y < node.getPlace().y)) {
            return findNode(node.left, x, y);
        } else {
            return findNode(node.right, x, y);
        }
    }

    // Method to find places by service within walking distance
    public ArrayList<int[]> findPlacesByService(int currentX, int currentY, double walkingDistance, ServiceType serviceType) {
        // Calculate the boundary based on walking distance
        Rectangle searchArea = new Rectangle(currentX - (int) walkingDistance, currentY - (int) walkingDistance,
                (int) (2 * walkingDistance), (int) (2 * walkingDistance));

        // Retrieve places within the boundary that offer the specified service
        ArrayList<int[]> results = new ArrayList<>();
        searchByServiceWithinBounds(root, searchArea, serviceType, results);
        return results;
    }

    // Helper method to perform the search within bounds
    private void searchByServiceWithinBounds(KDTreeNode node, Rectangle bounds, ServiceType serviceType, ArrayList<int[]> results) {
        if (node == null) return;

        int x = node.getPlace().x;
        int y = node.getPlace().y;

        // Check if the node's point is within the bounds and offers the service
        if (bounds.contains(x, y) && node.getPlace().offersService(serviceType)) {
            results.add(new int[]{x, y});
        }

        // Continue the search in the appropriate subtrees
        if (node.left != null && bounds.intersectsLeft(node.getPlace().x)) {
            searchByServiceWithinBounds(node.left, bounds, serviceType, results);
        }
        if (node.right != null && bounds.intersectsRight(node.getPlace().x)) {
            searchByServiceWithinBounds(node.right, bounds, serviceType, results);
        }
    }

    /**
     * Searches for all points within a specified rectangle that offer a given service.
     * @param bounds The search area as a Rectangle object.
     * @param serviceType The type of service to look for.
     * @param found The list where results are added.
     */
    public void searchByServiceWithinBounds(Rectangle bounds, ServiceType serviceType, List<int[]> found) {
        searchByServiceWithinBounds(root, bounds, serviceType, found);
    }

    /**
     * Helper method to recursively search for points offering a specified service within bounds.
     * @param node The current node being checked.
     * @param bounds The search area as a Rectangle object.
     * @param serviceType The type of service to look for.
     * @param found The list where results are added.
     */
    private void searchByServiceWithinBounds(KDTreeNode node, Rectangle bounds, ServiceType serviceType, List<int[]> found) {
        if (node == null) return;

        int x = node.getPlace().x;
        int y = node.getPlace().y;

        // Check if the current node is within the bounds and if it offers the service
        if (bounds.contains(x, y) && node.getPlace().offersService(serviceType)) {
            found.add(new int[]{x, y}); // Add the point to the result list
        }

        // Determine whether to search left or right subtree
        if (node.left != null && bounds.intersects(node, true)) {
            searchByServiceWithinBounds(node.left, bounds, serviceType, found);
        }
        if (node.right != null && bounds.intersects(node, false)) {
            searchByServiceWithinBounds(node.right, bounds, serviceType, found);
        }
    }
}

    /**
 * Represents a node in the KD-Tree.
 */
class KDTreeNode {
    private Place place;
    KDTreeNode left = null, right = null;
    private int height = 1;

    public KDTreeNode(Place place) {
        this.place = place;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public int getHeight() {
        return height;
    }

    public void updateHeight() {
        height = 1 + Math.max(getHeight(left), getHeight(right));
    }

    private static int getHeight(KDTreeNode node) {
        return node == null ? 0 : node.height;
    }

    public int getBalance() {
        return getHeight(left) - getHeight(right);
    }

    public boolean isEqual(int x, int y) {
        return place.x == x && place.y == y;
    }
}
