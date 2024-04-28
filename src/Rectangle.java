public class Rectangle {
    int x, y, width, height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean intersects(Rectangle other) {
        return this.x < other.x + other.width && this.x + this.width > other.x &&
                this.y < other.y + other.height && this.y + this.height > other.y;
    }

    public boolean contains(int px, int py) {
        return px >= x && py >= y && px < x + width && py < y + height;
    }

    public void expand(Rectangle other) {
        int newX = Math.min(this.x, other.x);
        int newY = Math.min(this.y, other.y);
        int newWidth = Math.max(this.x + this.width, other.x + other.width) - newX;
        int newHeight = Math.max(this.y + this.height, other.y + other.height) - newY;
        this.x = newX;
        this.y = newY;
        this.width = newWidth;
        this.height = newHeight;
    }
}