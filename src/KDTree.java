/**
 * Represents a 2D map for managing geographical locations with spatial queries support using KD-Tree structure.
 */
public class KDTree {
    private KDTreeNode root;

    public void add(Place place) {
        root = add(root, place, true);
    }

    private KDTreeNode add(KDTreeNode node, Place place, boolean vertical) {
        if (node == null) {
            return new KDTreeNode(place);
        }
        int cmp = vertical ? Integer.compare(place.x, node.place.x) : Integer.compare(place.y, node.place.y);
        if (cmp < 0) {
            node.left = add(node.left, place, !vertical);
        } else {
            node.right = add(node.right, place, !vertical);
        }
        return balance(node);
    }

    public boolean delete(int x, int y) {
        if (findNode(root, x, y) == null) {
            return false;
        }
        root = delete(root, x, y, true);
        return true;
    }

    private KDTreeNode delete(KDTreeNode node, int x, int y, boolean vertical) {
        if (node == null) return null;

        if (node.place.isEqual(x, y)) {
            if (node.left == null || node.right == null) {
                return (node.left != null) ? node.left : node.right;
            }
            KDTreeNode min = findMin(node.right, !vertical);
            node.place = min.place;
            node.right = delete(node.right, min.place.x, min.place.y, !vertical);
        } else {
            int cmp = vertical ? Integer.compare(x, node.place.x) : Integer.compare(y, node.place.y);
            if (cmp < 0) {
                node.left = delete(node.left, x, y, !vertical);
            } else {
                node.right = delete(node.right, x, y, !vertical);
            }
        }
        return balance(node);
    }

    private KDTreeNode findMin(KDTreeNode node, boolean vertical) {
        if (node == null) return null;
        if (vertical) {
            return (node.left != null) ? findMin(node.left, vertical) : node;
        } else {
            return (node.right != null) ? findMin(node.right, vertical) : node;
        }
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

    private KDTreeNode rotateRight(KDTreeNode y) {
        KDTreeNode x = y.left;
        y.left = x.right;
        x.right = y;
        x.updateHeight();
        y.updateHeight();
        return x;
    }

    private KDTreeNode rotateLeft(KDTreeNode y) {
        KDTreeNode x = y.right;
        y.right = x.left;
        x.left = y;
        x.updateHeight();
        y.updateHeight();
        return x;
    }

    private KDTreeNode balance(KDTreeNode node) {
        if (node == null) return null;
        node.updateHeight();
        int balanceFactor = node.getBalance();

        if (balanceFactor > 1) {
            if (node.left.getBalance() < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        } else if (balanceFactor < -1) {
            if (node.right.getBalance() > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }
        return node;
    }

    private KDTreeNode findNode(KDTreeNode node, int x, int y) {
        while (node != null) {
            int cmpX = Integer.compare(x, node.place.x);
            int cmpY = Integer.compare(y, node.place.y);
            if (node.place.isEqual(x, y)) {
                return node;
            }
            if ((cmpX < 0) || (cmpX == 0 && cmpY < 0)) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return null;
    }

    public void searchByServiceWithinBounds(Rectangle bounds, ServiceType serviceType, List<int[]> found) {
        searchByServiceWithinBounds(root, bounds, serviceType, found);
    }

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

class KDTreeNode {
    Place place;
    KDTreeNode left, right;
    private int height = 1;

    public KDTreeNode(Place place) {
        this.place = place;
    }

    public int getHeight(KDTreeNode node) {
        return (node == null) ? 0 : node.height;
    }

    public void updateHeight() {
        this.height = 1 + Math.max(getHeight(left), getHeight(right));
    }

    public int getBalance() {
        return getHeight(left) - getHeight(right);
    }

    public Place getPlace() {
        return place;
    }
}
