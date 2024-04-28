public class BST<T extends Comparable<T>> {
    protected BinaryTreeNode<T> root;
    protected int size;

    public BST() {
        root = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public BinaryTreeNode<T> add(T value) {
        if (root == null) {
            root = new BinaryTreeNode<>(null, value);
            size++;
            return root;
        }
        return addRecursive(root, value);
    }

    private BinaryTreeNode<T> addRecursive(BinaryTreeNode<T> node, T value) {
        if (value.compareTo(node.data) < 0) {
            if (node.left == null) {
                node.left = new BinaryTreeNode<>(node, value);
                size++;
                return node.left;
            }
            return addRecursive(node.left, value);
        } else if (value.compareTo(node.data) > 0) {
            if (node.right == null) {
                node.right = new BinaryTreeNode<>(node, value);
                size++;
                return node.right;
            }
            return addRecursive(node.right, value);
        } else {
            return null; // Value already exists
        }
    }

    public BinaryTreeNode<T> remove(T value) {
        BinaryTreeNode<T> node = search(value);
        if (node == null) return null;

        return removeNode(node);
    }

    BinaryTreeNode<T> removeNode(BinaryTreeNode<T> node) {
        if (node.left == null || node.right == null) {
            BinaryTreeNode<T> newChild = (node.left != null) ? node.left : node.right;
            if (node == root) {
                root = newChild;
            } else {
                BinaryTreeNode<T> parent = node.parent;
                if (parent.left == node) {
                    parent.left = newChild;
                } else {
                    parent.right = newChild;
                }
                if (newChild != null) {
                    newChild.parent = parent;
                }
                size--;
                return parent;
            }
        } else {
            BinaryTreeNode<T> successor = findMin(node.right);
            node.data = successor.data;
            return removeNode(successor);
        }
        size--;
        return null; // root was removed
    }

    private BinaryTreeNode<T> findMin(BinaryTreeNode<T> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public BinaryTreeNode<T> search(T value) {
        BinaryTreeNode<T> current = root;
        while (current != null) {
            int cmp = value.compareTo(current.data);
            if (cmp == 0) return current;
            current = (cmp < 0) ? current.left : current.right;
        }
        return null;
    }
}