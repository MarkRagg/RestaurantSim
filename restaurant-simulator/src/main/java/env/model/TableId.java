package env.model;

public record TableId(String id) {
    @Override
    public String toString() {
        return id;
    }
 }