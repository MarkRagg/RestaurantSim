package env.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import javax.swing.text.Position;

import env.interfaces.Restaurant;
import java.util.HashSet;

public class RestaurantImpl implements Restaurant {
    private final List<Table> tables;
    private final List<CustomerId> queue;
    private final Map<String, Agent> agents;
    private final Set<String> removedAgents;
    private final RestaurantSize restaurantSize;

    public RestaurantImpl(List<Table> tables, RestaurantSize restaurantSize) {
        this.restaurantSize = restaurantSize;
        this.removedAgents = new HashSet<>();
        this.tables = tables;
        this.calculateTablePositions();
        this.queue = new ArrayList<>();
        this.agents = new HashMap<>();
    }

    @Override
    public int getWidth() {
        return this.restaurantSize.getWidth();
    }

    @Override
    public int getHeight() {
        return this.restaurantSize.getHeight();
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
    public Set<String> getAllAgents() {
        synchronized (this.agents) {
            return this.agents.keySet();
        }
    }

    @Override
    public Position2D getAgentPosition(String agentId) {
        synchronized ( this.agents ) {
            return this.agents.get(agentId).getPosition();
        }
    }

    @Override
    public List<CustomerId> getQueue() {
        return this.queue;
    }

    @Override
    public void addToQueue(CustomerId customerId) {
        synchronized (this.queue) {
            this.queue.add(customerId);
        }
    }

    @Override
    public boolean removeFromQueue(CustomerId customerId) {
        synchronized (this.queue) {
            return this.queue.remove(customerId);
        }
    }

    @Override
    public CustomerId getNextInQueue() {
        CustomerId customerId = this.queue.get(0);
        this.queue.remove(0); 
        return customerId;
    }

    @Override
    public boolean containsAgent(String agentName) {
        synchronized (this.agents) {
            return this.agents.containsKey(agentName);
        }
    }

    @Override
    public void addAgent(String name) {
        synchronized (this.agents) {
            this.agents.put(name, new Agent(name, this.calculateAgentPosition(name)));
        }
    }

    @Override
    public boolean removeAgent(String name) {
        synchronized (this) {
            if (this.agents.containsKey(name)) {
                this.agents.remove(name);
                this.removedAgents.add(name);
                return true;
            }       
            return false;
        }
    }

    @Override
    public Set<String> getRemovedAgents() {
        synchronized (this.removedAgents) {
            return new HashSet<>(this.removedAgents);
        }
    }

    @Override
    public boolean isAgentRemoved(String agentName) {
        synchronized (this.removedAgents) {
            return this.removedAgents.contains(agentName);
        }
    }

    @Override
    public boolean setAgentLocationToTable(String agentName, TableId tableId) {
        synchronized (this.agents) {
            Agent agent = this.agents.get(agentName);
            Position2D tablePosition = getTable(tableId).getPosition();
            Position2D newPosition = this.calculateAgentPositionToTargetPosition(agentName, tablePosition);
            if (this.checkValidMovement(newPosition)) {
                agent.setPosition(newPosition);
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean setAgentLocationToChef(String agentName, String chefName) {
        synchronized (this.agents) {
            Agent agent = this.agents.get(agentName);
            Position2D chefPosition = this.agents.get(chefName).getPosition();
            Position2D newPosition = this.calculateAgentPositionToTargetPosition(agentName, chefPosition);
            if (this.checkValidMovement(newPosition)) {
                agent.setPosition(newPosition);
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean setAgentLocationToQueue(String agentName) {
        synchronized (this) {
            Agent agent = this.agents.get(agentName);
            Position2D newPosition = new Position2D(getWidth() - 1, 0);
            this.updateCustomerQueuePositions();
            if (this.checkValidMovement(newPosition)) {
                agent.setPosition(newPosition);
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean setAgentLocationToDefault(String agentName) {
        synchronized (this.agents) {
            if (agentName.contains("waiter")) {
                Agent agent = this.agents.get(agentName);
                Position2D newPosition = this.calculateWaiterPosition();
                if (this.checkValidMovement(newPosition)) {
                    agent.setPosition(newPosition);
                    return true;
                }
            }
            return false;
        }
    }

    private boolean checkValidMovement(Position2D targetPosition) {
        return targetPosition.getX() < this.restaurantSize.getWidth() &&
               targetPosition.getY() < this.restaurantSize.getHeight() && 
               targetPosition.getX() >= 0 &&
               targetPosition.getY() >= 0; 
    }

    private void calculateTablePositions() {
        int halfHeight = this.restaurantSize.getHeight() / 2;
        int spaceFromEdge = 2;
        int tablesCount = this.tables.size();
        int spaceBetweenTables = (this.restaurantSize.getWidth() - (2 * spaceFromEdge)) / (tablesCount - 1);
        for (int i = 0; i < tablesCount; i++) {
            int x = spaceFromEdge + (i * spaceBetweenTables);
            int y = halfHeight;
            Table table = this.tables.get(i);
            table.setPosition(new Position2D(x, y));
        }
    }

    private void updateCustomerQueuePositions() {
        this.queue.forEach(c -> {
            Agent customer = this.agents.get(c.toString());
            Position2D oldPos = customer.getPosition();
            customer.setPosition(Position2D.of(oldPos.getX(), oldPos.getY() + 1));
        });
    }

    private Position2D calculateAgentPositionToTargetPosition(String agentName, Position2D targetPosition) {
        return agentName.startsWith("waiter") ? 
            new Position2D(targetPosition.getX(), targetPosition.getY() - 1) : 
            new Position2D(targetPosition.getX() - 1, targetPosition.getY());

    }

    private Position2D calculateChefPosition() {
    return IntStream.range(1, getWidth() - 1)
        .filter(i -> i % 2 == 1)
        .mapToObj(i -> new Position2D(i, restaurantSize.getHeight() - 1))
        .filter(pos -> this.agents.values().stream()
            .noneMatch(agent -> agent.getPosition().equals(pos)))
        .findFirst()
        .orElse(new Position2D(0, restaurantSize.getHeight() - 1));
    } 

    private Position2D calculateCustomerPosition() {
        return IntStream.range(0, getWidth() - 1)
            .mapToObj(i -> new Position2D(i, 0))
            .filter(pos -> this.agents.values().stream()
                .noneMatch(agent -> agent.getPosition().equals(pos)))
            .findFirst()
            .orElse(new Position2D(0, 0));
    }
    
    private Position2D calculateWaiterPosition() {
        return IntStream.iterate(getWidth() - 1, i -> i > 0, i -> i - 1)
            .mapToObj(i -> new Position2D(i, getHeight() - 1))
            .filter(pos -> this.agents.values().stream()
                .noneMatch(agent -> agent.getPosition().equals(pos)))
            .findFirst()
            .orElse(new Position2D(0, 0));
    }

    private Position2D calculateAgentPosition(String agentName) {
        if (agentName.startsWith("waiter")) {
            return calculateWaiterPosition();
        } else if (agentName.startsWith("chef")) {
            return this.calculateChefPosition(); 
        } else if (agentName.startsWith("customer")) {
            return calculateCustomerPosition(); 
        }
        return new Position2D(0, 0); 
    }
}