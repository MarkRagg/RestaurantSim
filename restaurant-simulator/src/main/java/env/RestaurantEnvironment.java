package env;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import env.interfaces.Restaurant;
import env.model.CustomerId;
import env.model.Dish;
import env.model.Menu;
import env.model.RestaurantImpl;
import env.model.Table;
import env.model.TableId;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;

public class RestaurantEnvironment extends Environment {
  public static final Literal freeTable = Literal.parseLiteral("free_table(_)");
  public static final Literal occupyTable = Literal.parseLiteral("occupy_table(_)");
  public static final Literal goToQueue = Literal.parseLiteral("go_to_queue");
  public static final Literal nextInQueue = Literal.parseLiteral("next_in_queue(_)");

  private Restaurant restaurant;
  private ReentrantLock tableLock;
  private ReentrantLock queueLock;

  @Override
  public void init(final String[] args) {
    this.restaurant = new RestaurantImpl(List.of(new Table(new TableId("Table_1")), (new Table(new TableId("Table_2")))));
    this.tableLock = new ReentrantLock();
    this.queueLock = new ReentrantLock();
    // initialize GUI if requested
    // if ((args.length == 1) && args[0].equals("gui")) {
    // this.view = new FactoryView(this.model);
    // view.setEnvironment(this);
    // }
  }

  @Override
  public boolean executeAction(String agentName, Structure action) {
    // System.out.println("[" + agentName + "] doing: " + action);
    boolean result = false;

    switch (action.getFunctor()) {
      case "free_table":
        result = executeFreeTable(agentName, action);
        informAgsEnvironmentChanged();
        break;
      case "occupy_table":
        result = executeOccupyTable(agentName, action);
        informAgsEnvironmentChanged();
        break;
      case "go_to_queue":
        result = executeGoToQueue(agentName);
        informAgsEnvironmentChanged();
        break;
      // case "next_in_queue":
      //   result = restaurant.getNextInQueue() != null;
      //   informAgsEnvironmentChanged();
      //   break;
      default:
        System.err.println("Unknown action: " + action);
        return false;
    }
    return result;
  }

  @Override
  public Collection<Literal> getPercepts(String agName) {
    Collection<Literal> percepts = new ArrayList<>();
    ListTerm dishesList = ASSyntax.createList();

    if (agName.startsWith("customer")) {
      for (Menu menuDish : Menu.values()) {
        Dish dish = menuDish.getDish();
        Literal l = ASSyntax.createLiteral("dish",
            ASSyntax.createAtom(dish.name().toString()),
            ASSyntax.createNumber(dish.preparationTime())
          );
        dishesList.add(l);
      }
    }
    Literal list = ASSyntax.createLiteral("menu", dishesList);
    percepts.add(list);

    ListTerm customerList = ASSyntax.createList();
    if (agName.startsWith("waiter")) {
      for (Table table : restaurant.getTables()) {
        Literal l = ASSyntax.createLiteral("table_status",
            ASSyntax.createAtom(table.getId().toString()),
            ASSyntax.createAtom(table.isFree() ? "free" : "occupied"));
        percepts.add(l);
      }

      List<CustomerId> queue = restaurant.getQueue();
      for (CustomerId customerId : queue) {
        Literal l = ASSyntax.createAtom(customerId.toString());
        customerList.add(l);
      }
    }
    Literal queue = ASSyntax.createLiteral("new_queue", customerList);
    percepts.add(queue);
    ListTerm order_list = ASSyntax.createList();

    if (agName.startsWith("chef")) {
      for (Menu menuDish : Menu.values()) {
        Dish dish = menuDish.getDish();
        Literal l = ASSyntax.createLiteral("dish",
            ASSyntax.createAtom(dish.name().toString()),
            ASSyntax.createNumber(dish.preparationTime())
          );
        order_list.add(l);
      }
    }
    Literal order_queue = ASSyntax.createLiteral("order_queue", order_list);
    percepts.add(order_queue);

    return percepts;
  }

  private boolean executeFreeTable(String agentName, Structure action) {
    try {
      TableId tableId = new TableId(action.getTerm(0).toString());
      Table table = restaurant.getTable(tableId);
      if (!table.isFree()) {
        table.setFree(true);
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
    if (tableLock.tryLock()) {
      try {
        TableId tableId = new TableId(action.getTerm(0).toString());
        Table table = restaurant.getTable(tableId);
        if (table.isFree()) {
          table.setFree(false);
          return true;
        } else {
          return false;
        }
      } catch (Exception e) {
        System.err.println("Error executing occupy_table action: " + e.getMessage());
        return false;
      }
    } else {
      return false;
    }
  }

  private boolean executeGoToQueue(String agentName) {
    if (queueLock.tryLock()) {
      try {
        CustomerId customerId = new CustomerId(agentName);
        restaurant.addToQueue(customerId);
        return true;
      } catch (Exception e) {
        System.err.println("Error executing go_to_queue action: " + e.getMessage());
        return false;
      }
    } else {
      return false;
    }
  }
}