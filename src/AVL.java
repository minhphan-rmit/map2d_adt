public class AVL<T extends Comparable<T>> extends BST<T> {

    @Override
    public BinaryTreeNode<T> add(T value) {
        BinaryTreeNode<T> newNode = super.add(value);
        rebalance(newNode);
        return newNode;
    }

    @Override
    public BinaryTreeNode<T> remove(T value) {
        BinaryTreeNode<T> node = search(value);
        if (node == null) return null;

        BinaryTreeNode<T> parent = node.parent;
        BinaryTreeNode<T> replaceNode = removeNode(node);
        rebalance(parent != null ? parent : replaceNode);
        return replaceNode;
    }

    private void rebalance(BinaryTreeNode<T> node) {
        while (node != null) {
            updateHeight(node);
            node = balance(node);
            node = node.parent;
        }
    }

    private BinaryTreeNode<T> balance(BinaryTreeNode<T> node) {
        if (node == null) return null;

        int balanceFactor = getBalanceFactor(node);
        if (balanceFactor > 1) {
            if (getBalanceFactor(node.right) < 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        } else if (balanceFactor < -1) {
            if (getBalanceFactor(node.left) > 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }
        return node;
    }

    private BinaryTreeNode<T> rotateRight(BinaryTreeNode<T> y) {
        BinaryTreeNode<T> x = y.left;
        BinaryTreeNode<T> T2 = x.right;

        x.right = y;
        y.left = T2;

        if (T2 != null) {
            T2.parent = y;
        }

        x.parent = y.parent;
        y.parent = x;

        if (x.parent == null) {
            root = x;
        } else if (x.parent.right == y) {
            x.parent.right = x;
        } else if (x.parent.left == y) {
            x.parent.left = x;
        }

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    private BinaryTreeNode<T> rotateLeft(BinaryTreeNode<T> x) {
        BinaryTreeNode<T> y = x.right;
        BinaryTreeNode<T> T2 = y.left;

        y.left = x;
        x.right = T2;

        if (T2 != null) {
            T2.parent = x;
        }

        y.parent = x.parent;
        x.parent = y;

        if (y.parent == null) {
            root = y;
        } else if (y.parent.right == x) {
            y.parent.right = y;
        } else if (y.parent.left == x) {
            y.parent.left = y;
        }

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    private int getBalanceFactor(BinaryTreeNode<T> node) {
        if (node == null) return 0;
        return height(node.right) - height(node.left);
    }

    private int height(BinaryTreeNode<T> node) {
        return node == null ? 0 : node.getHeight();
    }

    private void updateHeight(BinaryTreeNode<T> node) {
        node.updateHeight();
    }

    // Assuming Rectangle and Place types are defined elsewhere
    public void inOrderCollect(Rectangle range, List<Place> found) {
        inOrderCollectPlaces(root, range, found);
    }

    private void inOrderCollectPlaces(BinaryTreeNode<T> node, Rectangle range, List<Place> found) {
        if (node == null) return;
        inOrderCollectPlaces(node.left, range, found);
        if (node.data instanceof Place) {
            Place place = (Place) node.data;
            if (range.contains(place.x, place.y)) {
                found.add(place);
            }
        }
        inOrderCollectPlaces(node.right, range, found);
    }
}
