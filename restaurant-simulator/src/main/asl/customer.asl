customer_state(free).
assigned_table(none).

!ask_for_a_waiter.

// +!search_for_a_waiter : true <-
//   .broadcast(askAll, customer_state(free));

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
  .print("Looking the menu.. ");
  .wait(10000);
  .print("Ask and eat..");
  .wait(50000);
  .print("Finish the meal");
  .wait(2000);
  free_table(T);
  .print("Finish").

+!wait_random_time <-
  .random(Delay);
  .wait(Delay*1000).