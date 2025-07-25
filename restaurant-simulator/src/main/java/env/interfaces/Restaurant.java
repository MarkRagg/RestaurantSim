package env.interfaces;

import java.util.List;

import env.model.CustomerId;
import env.model.Table;
import env.model.TableId;

public interface Restaurant {

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
}