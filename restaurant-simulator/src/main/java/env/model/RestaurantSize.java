package env.model;

public class RestaurantSize {
    private final int width;
    private final int height;

    public RestaurantSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "RestaurantSize{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
