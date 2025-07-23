state(free).

!ask_for_a_table.

// +!search_for_a_waiter

+!ask_for_a_table : state(free) <- 
  .send(waiter_1, achieve, called_for_a_table(self));
  .print("Customer asks for a table").

+!go_to_queue(_)[source(Waiter)] : true <-
  go_to_queue(self);
  .print("I'm going to queue");
  .wait({ +turn[source(Waiter)] }).  