customer_state(free).
assigned_table(none).
waiter_to_call(none).

!ask_for_a_waiter.

+!ask_for_a_waiter : customer_state(free) <-
  !wait_random_time;
  .print("Chiedo a qualcuno");
  .broadcast(askOne, waiter_state);
  .wait({ +waiter_available[source(Waiter)] });  // aspetta la prima risposta
  .print("Chiamo il waiter ", Waiter);
  !ask_for_a_table(Waiter).

+!ask_for_a_table(Waiter) <- 
  .send(Waiter, achieve, called_for_a_table).

+!wait_a_moment[source(Waiter)] <-
  .wait(1000);
  !ask_for_a_waiter.

+!sent_to_queue[source(Waiter)] : true <-
  go_to_queue;
  .wait({ +turn[source(Waiter)] }).

+!assign_table(T)[source(Waiter)] : true <-
  -+assigned_table(T);
  -+customer_state(sitting);
  -+waiter_to_call(Waiter);
  !choose_dish;
  .wait({ +dish_arrived(D)} );
  .print("Eating dish ", D);
  .wait(10000);
  .print("Finish the meal");
  .wait(2000);
  free_table(T);
  .print("Finish").

-!assign_table(T)[source(Waiter)] <-
  .print("Something went wrong").

+!wait_random_time <-
  .random(Delay);
  .wait(Delay*1000).

+!choose_dish : menu(Dishes) & customer_state(sitting) <-
  .random(Dishes, Dish);
  !order_dish(Dish).

+!order_dish(Dish) : Dish = dish(Name, PrepTime) & waiter_to_call(Waiter) & customer_state(sitting) <-
  .print("Order dish ", Name, " with prep time ", PrepTime, " at waiter ", Waiter);
  .send(Waiter, achieve, take_order(Dish, T)).

+!try_later(Dish)[source(Waiter)] <-
  .print("Waiter is busy, try later");
  !wait_random_time;
  !order_dish(Dish).
  