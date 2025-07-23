package env;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import env.interfaces.Restaurant;
import env.model.CustomerId;
import env.model.RestaurantImpl;
import env.model.Table;
import env.model.TableId;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;

public class RestaurantEnvironment extends Environment{
  public static final Literal freeTable = Literal.parseLiteral("free_table(_)");
  public static final Literal occupyTable = Literal.parseLiteral("occupy_table(_)");
  public static final Literal goToQueue = Literal.parseLiteral("go_to_queue(_)");
  public static final Literal nextInQueue = Literal.parseLiteral("next_in_queue(_)");



  private Restaurant restaurant;

  @Override
  public void init(final String[] args) {
      this.restaurant = new RestaurantImpl(List.of(new Table(new TableId("Table_1")), (new Table(new TableId("Table_2")))));

      // initialize GUI if requested
      // if ((args.length == 1) && args[0].equals("gui")) {
      //     this.view = new FactoryView(this.model);
      //     view.setEnvironment(this);
      // }
  }

  @Override
  public boolean executeAction(String agentName, Structure action) {
    System.out.println("[" + agentName + "] doing: " + action);
    boolean result = false;

    switch (action.getFunctor()) {
      case "free_table":
          result = executeFreeTable(agentName, action);
          break;
      case "occupy_table":
          result = executeOccupyTable(agentName, action);
          break;
      case "go_to_queue":
          result = executeGoToQueue(agentName, action);
          break;
      case "next_in_queue":
          result = restaurant.getNextInQueue() != null;
          break;
      default:
          System.err.println("Unknown action: " + action);
          return false;
    }
    return result;
  }

  @Override
public Collection<Literal> getPercepts(String agName) {
    Collection<Literal> percepts = new ArrayList<>();

    if (agName.startsWith("waiter")) {
        for (Table table : restaurant.getTables()) {
            String status = table.isFree() ? "free" : "occupied";
            Literal l = Literal.parseLiteral("table_status(" + table.getId() + "," + status + ")");
            percepts.add(l);
        }
    }
    // TODO: add other type of percepts

    return percepts;
}

  private boolean executeFreeTable(String agentName, Structure action) {
    try {
      TableId tableId = new TableId(action.getTerm(0).toString());
      Table table = restaurant.getTable(tableId);
      if (!table.isFree()) {
        table.setFree(true);
        System.out.println("Table " + tableId + " is now free.");
        return true;
      } else {
        System.out.println("Table " + tableId + " is already free.");
        return false;
      }
    } catch (Exception e) {
      System.err.println("Error executing free_table action: " + e.getMessage());
      return false;
    }
  }

  
  private boolean executeOccupyTable(String agentName, Structure action) {
    try {
      TableId tableId = new TableId(action.getTerm(0).toString());
      Table table = restaurant.getTable(tableId);
      if (table.isFree()) {
        table.setFree(false);
        System.out.println("Table " + tableId + " is now occupied.");
        return true;
      } else {
        System.out.println("Table " + tableId + " is already occupied.");
        return false;
      }
    } catch (Exception e) {
      System.err.println("Error executing occupy_table action: " + e.getMessage());
      return false;
    }
  }

  private boolean executeGoToQueue(String agentName, Structure action) {
    try {
      CustomerId customerId = new CustomerId(agentName);
      restaurant.addToQueue(customerId);
      System.out.println("Customer " + customerId + " added to the queue.");
      return true;
    } catch (Exception e) {
      System.err.println("Error executing go_to_queue action: " + e.getMessage());
      return false;
    }
  }

}