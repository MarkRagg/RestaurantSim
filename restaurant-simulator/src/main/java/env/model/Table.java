package env.model;

public class Table {
    private final TableId id;
    private boolean free;
    private Position2D position;

    public Table(TableId id) {
        this.id = id;
        this.free = true;
        this.position = new Position2D(0, 0);
    }

    public TableId getId() {
        return id;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public Position2D getPosition() {
        return position;
    }
    
    public void setPosition(Position2D position) {
        this.position = position;
    }
}