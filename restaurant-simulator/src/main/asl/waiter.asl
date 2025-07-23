state(free).
served_person(none).

+!called_for_a_table(_)[source(Customer)] : state(free) & table_status(T, free) <-
  -+state(busy);
  .wait(500);
  occupy_table(T);
  .print("Waiter: table ", T, " is occupied by ", Customer);
  -+state(free).

-!called_for_a_table(_)[source(Customer)] : .findall(T, table_status(T, free), []) <-
  .send(Customer, tell, turn);
  -+state(free);
  .print("All tables are occupied, customer ", Customer, " sent to queue").

-!called_for_a_table(_)[source(Customer)] : state(busy) & table_status(_, free) <-
  .print("Waiter is busy, customer ", Customer, " will wait");
  !called_for_a_table(_)[source(Customer)].

+table_status(TableId, Status) : true <-
  .print("Perceived table ", TableId, "is ", Status).