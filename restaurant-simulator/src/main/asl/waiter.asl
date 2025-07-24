waiter_state(free).

+!called_for_a_table[source(Customer)] : waiter_state(free) & table_status(T, free) <-
  -+waiter_state(busy);
  !try_occupy_table(T);
  .print("Waiter: table ", T, " is occupied by ", Customer);
  .wait(500);
  .send(Customer, achieve, assign_table(T));
  -+waiter_state(free).

-!called_for_a_table[source(Customer)] : .findall(T, table_status(T, free), []) <-
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

+table_status(TableId, Status) : Status == free <-
  .print("Perceived table ", TableId, "is ", Status).

+?waiter_state[source(Customer)] : waiter_state(free) <-
  .print("Mando la disponibilita");
  .send(Customer, tell, waiter_available).

+?waiter_state[source(Customer)] : waiter_state(busy) <-
  .print("Sono occupato"). 

+!try_occupy_table(T) <-
  occupy_table(T).

-!try_occupy_table(T) : table_status(Id, free) <-
  .print("Primo tentativo fallito, provo con un altro tavolo");
  !try_occupy_table(Id).