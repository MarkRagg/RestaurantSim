waiter_state(free).
chefs_available([]).

+!called_for_a_table[source(Customer)] : waiter_state(free) & table_status(T, free) <-
  -+waiter_state(busy);
  !try_occupy_table(T);
  +table_person(T, Customer);
  .print("Waiter: table ", T, " is occupied by ", Customer);
  .wait(2000);
  .send(Customer, achieve, assign_table(T));
  -+waiter_state(free).

-!called_for_a_table[source(Customer)] : .findall(T, table_status(T, free), []) <-
  -+waiter_state(busy);
  .wait(200);
  .send(Customer, achieve, sent_to_queue);
  -+waiter_state(free);
  .print("All tables are occupied, customer ", Customer, " sent to queue").

-!called_for_a_table[source(Customer)] : waiter_state(busy) & table_status(_, free) <-
  .print("Wait a moment");
  .send(Customer, achieve, wait_a_moment).

-!called_for_a_table[source(Customer)] : true <-
  .print("Wait a moment");
  -+waiter_state(free);
  .send(Customer, achieve, wait_a_moment).

+!dish_ready(D, T)[source(Chef)] : waiter_state(free) & table_person(T, Customer) <-
  -+waiter_state(busy);
  .send(Customer, tell, dish_arrived(D));
  -table_person(T, Customer);
  -+waiter_state(free).

+!try_occupy_table(T) <-
  occupy_table(T).

-!try_occupy_table(T) : table_status(Id, free) <-
  .print("Primo tentativo fallito, riprovo");
  !try_occupy_table(Id).

+?waiter_state[source(Customer)] : waiter_state(free) <-
  .print("Mando la disponibilita");
  .send(Customer, tell, waiter_available).

+?waiter_state[source(Customer)] : waiter_state(busy) <-
  .print("Sono occupato");
  .wait({ +waiter_state(free)});
  .send(Customer, tell, waiter_available).

+!take_order(Dish, T)[source(Customer)] : waiter_state(free) & chefs_available(Chefs) <-
  -+waiter_state(busy);
  .random(Chefs, Chef);
  .send(Chef, tell, new_order(order(Dish, T)));
  -+waiter_state(free).
 
+!take_order(Dish, T)[source(Customer)] : waiter_state(busy) <-
  .send(Customer, achieve, try_later(Dish)).

+chef_available[source(Chef)] : chefs_available(List) <-
  -+chefs_available([Chef | List]).

+table_status(TableId, Status) : Status == free <-
  .print("Perceived table ", TableId, "is ", Status).