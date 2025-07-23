package env.model;

public class Table {
    private final TableId id;
    private boolean free;

    public Table(TableId id) {
        this.id = id;
        this.free = true;
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
}