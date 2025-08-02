package model;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import env.interfaces.Restaurant;
import env.model.Position2D;
import env.model.RestaurantImpl;
import env.model.RestaurantSize;
import env.model.Table;
import env.model.TableId;

public class RestaurantImplTest {
	private Restaurant restaurant;
  private int height = 20;
  private int width = 20;

  @Before
  public void setUpClass() {
    List<Table> tables = new ArrayList<>(List.of(new Table(new TableId("table_1")), new Table(new TableId("table_2"))));
    this.restaurant = new RestaurantImpl(tables, new RestaurantSize(width, height));
  }

  @Test
  public void testTablesSize() {
    assertTrue(this.restaurant.getTables().size() == 2);
  }

  @Test
  public void testGetTable() {
    assertNotNull(this.restaurant.getTable(new TableId("table_1")));
  }

  @Test
  public void testAddFreeTable() {
    this.restaurant.getTables().forEach(table -> table.setFree(false));
    this.restaurant.getTable(new TableId("table_1")).setFree(true);
    assertTrue(this.restaurant.getFreeTables().size() == 1);
  }

  @Test
  public void testRemoveFreeTable() {
    this.restaurant.getTable(new TableId("table_1")).setFree(false);
    assertTrue(this.restaurant.getFreeTables().size() == 1);
  }

  @Test
  public void testAddWaiter() {
    String agentName = "waiter_1";
    this.restaurant.addAgent(agentName);
    assertTrue(this.restaurant.getAllAgents().size() > 0);
    assertEquals(this.restaurant.getAgentPosition(agentName), Position2D.of(width - 1, height - 1));
  }

  @Test
  public void testAddMoreWaiters() {
    String waiter1 = "waiter_1";
    String waiter2 = "waiter_2";
    String waiter3 = "waiter_3";
    this.restaurant.addAgent(waiter1);
    this.restaurant.addAgent(waiter2);
    this.restaurant.addAgent(waiter3);
    assertEquals(this.restaurant.getAgentPosition(waiter1), Position2D.of(width - 1, height - 1));
    assertEquals(this.restaurant.getAgentPosition(waiter2), Position2D.of(width - 2, height - 1));
    assertEquals(this.restaurant.getAgentPosition(waiter3), Position2D.of(width - 3, height - 1));
  }

  @Test
  public void testAddCustomers() {
    int x = 0;
    int y = 0;
    String customer1 = "customer_1";
    String customer2 = "customer_2";
    String customer3 = "customer_3";
    this.restaurant.addAgent(customer1);
    this.restaurant.addAgent(customer2);
    this.restaurant.addAgent(customer3);
    assertEquals(this.restaurant.getAgentPosition(customer1), Position2D.of(x, y));
    assertEquals(this.restaurant.getAgentPosition(customer2), Position2D.of(x + 1, y));
    assertEquals(this.restaurant.getAgentPosition(customer3), Position2D.of(x + 2, y));
  }

  @Test
  public void testAddChefs() {
    int x = 0;
    String chef1 = "chef_1";
    String chef2 = "chef_2";
    String chef3 = "chef_3";
    this.restaurant.addAgent(chef1);
    this.restaurant.addAgent(chef2);
    this.restaurant.addAgent(chef3);
    assertEquals(this.restaurant.getAgentPosition(chef1), Position2D.of(x + 1, height - 1));
    assertEquals(this.restaurant.getAgentPosition(chef2), Position2D.of(x + 3, height - 1));
    assertEquals(this.restaurant.getAgentPosition(chef3), Position2D.of(x + 5, height - 1));
  }

}