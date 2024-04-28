public class BinaryTreeNode<T extends Comparable<T>> {
    T data;
    BinaryTreeNode<T> parent, left, right;
    private int subtreeNodes;
    private int height;

    public BinaryTreeNode(BinaryTreeNode<T> parent, T data) {
        this.parent = parent;
        this.data = data;
        this.subtreeNodes = 1; // Initialize with the node itself being the only node in the subtree
        this.height = 1; // Initialize with height 1, assuming this node is a leaf when created
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public BinaryTreeNode<T> getParent() {
        return parent;
    }

    public void setParent(BinaryTreeNode<T> parent) {
        this.parent = parent;
    }

    public BinaryTreeNode<T> getLeft() {
        return left;
    }

    public void setLeft(BinaryTreeNode<T> left) {
        this.left = left;
        updateHeight(); // Automatically update height when children change
    }

    public BinaryTreeNode<T> getRight() {
        return right;
    }

    public void setRight(BinaryTreeNode<T> right) {
        this.right = right;
        updateHeight(); // Automatically update height when children change
    }

    public int getHeight() {
        return height;
    }

    public int updateHeight() {
        int leftHeight = (left != null) ? left.getHeight() : 0;
        int rightHeight = (right != null) ? right.getHeight() : 0;
        height = 1 + Math.max(leftHeight, rightHeight);
        return height;
    }

    public int getBalanceFactor() {
        int leftHeight = (left != null) ? left.getHeight() : 0;
        int rightHeight = (right != null) ? right.getHeight() : 0;
        return rightHeight - leftHeight;
    }
}
