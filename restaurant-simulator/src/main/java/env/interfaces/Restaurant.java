package env.interfaces;

import java.util.List;
import java.util.Set;

import env.model.CustomerId;
import env.model.Position2D;
import env.model.Table;
import env.model.TableId;

public interface Restaurant {

  int getWidth();

  int getHeight();

  /**
   * Returns the list of tables in the restaurant.
   * @return a list of tables
   */
  List<Table> getTables();


  /**
   * Returns a list of free tables in the restaurant.
   * @return a list of free tables
   */
  List<Table> getFreeTables();

  /**
   * Returns the table with the specified ID.
   * @param id
   * @return
   */
  Table getTable(TableId id);

  /**
   * Returns the set of all agents in the restaurant.
   * @return a set of agent names
   */
  Set<String> getAllAgents();

  /**
   * 
   * @param customerId
   */
  Position2D getAgentPosition(String agentId);

  /**
   * Adds a customer to the queue.
   * @param customerId
   */
  void addToQueue(CustomerId customerId);

  /**
   * Returns the queue of customers waiting for a table.
   * @return a list of customer IDs in the queue
   */
  List<CustomerId> getQueue();

  /**
   * Removes a customer from the queue.
   * @param customerId
   */
  CustomerId getNextInQueue();

  /**
   * Returns true if the restaurant contains an agent with the specified name.
   * @param agentName
   * @return true if the agent exists, false otherwise
   */
  boolean containsAgent(String agentName);

  /**
   * Returns the agent with the specified name.
   * @param name
   * @return the agent
   */
  void addAgent(String name);

  boolean removeAgent(String name);

  Set<String> getRemovedAgents();

  boolean isAgentRemoved(String agentName);

  boolean setAgentLocationToTable(String agent, TableId tableId);

  boolean setAgentLocationToChef(String agent, String chefName);
  
  boolean setAgentLocationToQueue(String agent);

  boolean setAgentLocationToDefault(String agent);
}