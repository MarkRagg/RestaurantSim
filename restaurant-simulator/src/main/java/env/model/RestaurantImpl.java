package env.model;

import java.util.List;
import java.util.Queue;

import env.interfaces.Restaurant;

public class RestaurantImpl implements Restaurant {
    private final List<Table> tables;
    private final List<CustomerId> queue;

    public RestaurantImpl(List<Table> tables) {
        this.tables = tables;
        this.queue = List.of();
    }

    @Override
    public List<Table> getTables() {
        return this.tables;
    }

    @Override
    public Table getTable(TableId id) {
        Table table = this.tables.stream()
            .filter(t -> t.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Table not found: " + id));
        return table;
    }

    @Override
    public List<Table> getFreeTables() {
        return this.tables.stream()
            .filter(Table::isFree)
            .toList();
    }

    @Override
    public void addToQueue(CustomerId customerId) {
        this.queue.add(customerId);
    }

    @Override
    public CustomerId getNextInQueue() {
        CustomerId customerId = this.queue.get(0);
        this.queue.remove(0); 
        return customerId;
    }
}