waiter_state(free).
chefs_available([]).
customer_queue([]).

!start_waiter.

+!start_waiter <-
  .queue.create(Q);
  -+customer_queue(Q).

+!called_for_a_table[source(Customer)] : waiter_state(free) & table_status(T, free) <-
  -+waiter_state(busy);
  .wait(2000);
  !try_occupy_table(T, Customer);
  !bring_customer_to_table(Customer);
  -+waiter_state(free).

-!called_for_a_table[source(Customer)] : .findall(T, table_status(T, free), []) <-
  -+waiter_state(busy);
  .wait(2000);
  .send(Customer, achieve, sent_to_queue);
  -+waiter_state(free);
  .print("All tables are occupied, customer ", Customer, " sent to queue").

-!called_for_a_table[source(Customer)] : waiter_state(busy) & table_status(_, free) <-
  .send(Customer, achieve, wait_a_moment).

-!called_for_a_table[source(Customer)] : true <-
  .print("Wait a moment");
  -+waiter_state(free);
  .send(Customer, achieve, wait_a_moment).

+!dish_ready(D, T)[source(Chef)] : waiter_state(free) & table_person(T, Customer) <-
  -+waiter_state(busy);
  go_to_chef(Chef);
  .wait(2000);
  go_to_table(T);
  .wait(2000);
  .send(Customer, tell, dish_arrived(D));
  -table_person(T, Customer);
  -+waiter_state(free).

-!dish_ready(D, T)[source(Chef)] : waiter_state(busy) <-
  .print("I'm busy now");
  .send(Chef, achieve, call_waiter_again(D, T)).

+!try_occupy_table(T, Customer) <-
  occupy_table(T);
  .print("Waiter: table ", T, " is occupied by ", Customer);
  +table_person(T, Customer).

-!try_occupy_table(T, Customer) : table_status(Id, free) <-
  // .print("Attempt failed, retrying..");
  !try_occupy_table(Id, Customer).

-!try_occupy_table(T, Customer) : .findall(T, table_status(T, free), []) <-
  .fail.
  // .send(Customer, achieve, sent_to_queue);
  // -+waiter_state(free).
  // .print("All tables are occupied, customer ", Customer, " sent to queue").

+!bring_customer_to_table(Customer) : table_person(Id, Customer) <-
  // .print("Bringing customer ", Customer, " to table ", Id);
  .send(Customer, achieve, assign_table(Id)).

+?waiter_state[source(Customer)] : waiter_state(free) <-
  .print("I send the disponibility");
  .send(Customer, tell, waiter_available).

+?waiter_state[source(Customer)] : waiter_state(busy) <-
  .print("I'm busy");
  .wait({ +waiter_state(free)});
  .send(Customer, tell, waiter_available).

+!take_order(Dish)[source(Customer)] : waiter_state(free) & chefs_available(Chefs) & table_person(Id, Customer) <-
  -+waiter_state(busy);
  go_to_table(Id);
  .wait(5000);
  .random(Chefs, Chef);
  .send(Chef, tell, new_order(order(Dish, Id)));
  -+waiter_state(free).

+!take_order(Dish)[source(Customer)] : waiter_state(busy) <-
  .send(Customer, achieve, try_later(Dish)).

+chef_available[source(Chef)] : chefs_available(List) <-
  -+chefs_available([Chef | List]).

+table_status(T, Status) : table_status(T, free) & customer_queue(Q) <-
  .length(Q, QueueSize);
  if (QueueSize > 0) {
    .queue.remove(Q, Customer);
    .print("Table ", T, " is free");
    .send(Customer, tell, your_turn);
  }.

+new_queue(Queue) <-
  .queue.create(Q);
  -+customer_queue(Q);
  .queue.add_all(Q, Queue);
  .print("New queue: ", Queue).