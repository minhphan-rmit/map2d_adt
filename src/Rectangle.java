
public class Rectangle {
    int x, y, width, height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains(int px, int py) {
        return px >= x && py >= y && px < x + width && py < y + height;
    }

    public int centerX() {
        return x + width / 2;
    }

    public int centerY() {
        return y + height / 2;
    }

    public boolean intersectsLeft(int x) {
        return x < this.x + width / 2;
    }

    public boolean intersectsRight(int x) {
        return x >= this.x + width / 2;
    }

    /**
     * Determines if a KDTreeNode should be checked for intersection based on its position.
     * @param node The KDTreeNode being checked.
     * @param checkLeft Whether to check the left (true) or right (false) side based on the vertical/horizontal split.
     * @return true if the node's subtree could intersect with the rectangle, false otherwise.
     */
    public boolean intersects(KDTreeNode node, boolean checkLeft) {
        if (checkLeft) {
            // Vertical split: Check if the rectangle intersects the left of the x coordinate
            return x <= node.getPlace().x;
        } else {
            // Vertical split: Check if the rectangle intersects the right of the x coordinate
            return x + width > node.getPlace().x;
        }
    }
}