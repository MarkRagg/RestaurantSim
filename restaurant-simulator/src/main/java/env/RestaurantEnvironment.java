package env;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import env.interfaces.Restaurant;
import env.model.CustomerId;
import env.model.Dish;
import env.model.Logger;
import env.model.Menu;
import env.model.RestaurantImpl;
import env.model.RestaurantSize;
import env.model.Table;
import env.model.TableId;
import env.view.RestaurantGuiView;
import env.view.RestaurantView;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;

public class RestaurantEnvironment extends Environment {
  public static final Literal freeTable = Literal.parseLiteral("free_table(_)");
  public static final Literal occupyTable = Literal.parseLiteral("occupy_table(_)");
  public static final Literal eating = Literal.parseLiteral("eating(_)");
  public static final Literal preparingDish = Literal.parseLiteral("preparing_dish(_, _)");
  public static final Literal takingOrder = Literal.parseLiteral("taking_order(_, _)");
  public static final Literal goToQueue = Literal.parseLiteral("go_to_queue");
  public static final Literal removeFromQueue = Literal.parseLiteral("remove_from_queue(_)");
  public static final Literal goToDefaultPosition = Literal.parseLiteral("go_to_default_position");
  public static final Literal goToTable = Literal.parseLiteral("go_to_table(_)");
  public static final Literal goToChef = Literal.parseLiteral("go_to_chef(_, _, _)");
  public static final Literal nextInQueue = Literal.parseLiteral("next_in_queue(_)");
  public static final Literal removeAgent = Literal.parseLiteral("remove_agent");

  private Logger logger;
  private Restaurant restaurant;
  private RestaurantView view;
  private ReentrantLock tableLock;
  private ReentrantLock queueLock;

  @Override
  public void init(final String[] args) {
    this.logger = new Logger();
    List<Table> tables = new ArrayList<>(List.of(new Table(new TableId("table_1")), (new Table(new TableId("table_2"))), (new Table(new TableId("table_3")))));
    this.restaurant = new RestaurantImpl(tables, new RestaurantSize(Integer.parseInt(args[0]), Integer.parseInt(args[1])));
    RestaurantGuiView view = new RestaurantGuiView(this.restaurant, logger);
    this.view = view;
    this.tableLock = new ReentrantLock();
    this.queueLock = new ReentrantLock();
    view.setVisible(true);
  }

  @Override
  public boolean executeAction(String agentName, Structure action) {
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
      case "eating":
        result = logEating(agentName, action);
        break;
      case "preparing_dish":
        result = logPreparingDish(agentName, action);
        break;
      case "taking_order":
        result = logTakingOrder(agentName, action);
        break;
      case "go_to_queue":
        result = executeGoToQueue(agentName);
        informAgsEnvironmentChanged();
        break;
      case "remove_from_queue":
        result = executeRemoveFromQueue(agentName, action);
        informAgsEnvironmentChanged();
        break; 
      case "go_to_default_position":
        result = this.restaurant.setAgentLocationToDefault(agentName);
        informAgsEnvironmentChanged();
        break;
      case "go_to_table":
        TableId tableId = new TableId(action.getTerm(0).toString());
        result = this.restaurant.setAgentLocationToTable(agentName, tableId);
        informAgsEnvironmentChanged();
        break;
      case "go_to_chef":
        result = executeGoToChef(agentName, action);
        informAgsEnvironmentChanged();
        break;
      default:
        System.err.println("Unknown action: " + action);
        return false;
    }
    this.notifyModelChangedToView();
    return result;
  }

  @Override
  public Collection<Literal> getPercepts(String agName) {
    initializeAgentIfNeeded(agName);
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
        this.restaurant.removeAgent(agentName);
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
        restaurant.setAgentLocationToQueue(agentName);
        return true;
      } catch (Exception e) {
        System.err.println("Error executing go_to_queue action: " + e.getMessage());
        return false;
      }
    } else {
      return false;
    }
  }

  private boolean executeRemoveFromQueue(String agentName, Structure action) {
    try {
      CustomerId customerId = new CustomerId(action.getTerm(0).toString());
      return restaurant.removeFromQueue(customerId);
    } catch (Exception e) {
      System.err.println("Error executing remove_from_queue action: " + e.getMessage());
      return false;
    }
  }  

  private boolean executeGoToChef(String agentName, Structure action) {
    String chefName = action.getTerm(0).toString();
    String dishName = action.getTerm(1).toString();
    String customerName = action.getTerm(2).toString();
    boolean result = this.restaurant.setAgentLocationToChef(agentName, chefName);  
    if (result) {
      logger.appendLog(agentName + " bring " + dishName + " to " + customerName);
    }
    return result;
  }

  private boolean logEating(String agentName, Structure action) {
    String dishName = action.getTerm(0).toString();
    logger.appendLog(agentName + " is eating " + dishName);
    return true;
  }

  private boolean logPreparingDish(String agentName, Structure action) {
    String tableId = action.getTerm(0).toString();
    String dishName = action.getTerm(1).toString();
    logger.appendLog(agentName + " preparing " + dishName + " for " + tableId);
    return true;
  }

    private boolean logTakingOrder(String agentName, Structure action) {
    String dishName = action.getTerm(0).toString();
    String customerId = action.getTerm(1).toString();
    logger.appendLog(agentName + " taking order " + dishName + " from " + customerId);
    return true;
  }

  private void notifyModelChangedToView() {
    view.notifyModelChanged();
  }

  private void initializeAgentIfNeeded(String agentName) {
    if (!restaurant.containsAgent(agentName) && !restaurant.isAgentRemoved(agentName)) {
      restaurant.addAgent(agentName);
      notifyModelChangedToView();
    }
  }
}