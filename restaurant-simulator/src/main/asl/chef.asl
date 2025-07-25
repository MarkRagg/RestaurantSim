order_queue(none).

!start_chef.

+!start_chef <-
  .queue.create(Q);
  -+order_queue(Q);
  !advice_all_waiters;
  !check_order_queue.

+!check_order_queue : order_queue(Q) <-
  .length(Q, QueueSize);
  if (QueueSize > 0) {
    .print("Start cooking..");
    .queue.remove(Q, Order);
    Order = order(Dish, TableId, Waiter);
    Dish = dish(Name, PrepTime);
    .wait(PrepTime * 1000);
    !call_waiter(Waiter, Dish, TableId);
  } else {
    .wait(1000);
  };
  !check_order_queue.

+!call_waiter(Waiter, Dish, TableId) <-
  .send(Waiter, achieve, dish_ready(Dish, TableId)).

-!call_waiter(Waiter, Dish, TableId) <-
  .wait(1000);
  !call_waiter(Waiter, Dish, TableId).

+!advice_all_waiters <-
  .broadcast(tell, chef_available).

+new_order(Order)[source(Waiter)] : order_queue(Q) <-
  Order = order(Dish, T);
  CompleteOrder = order(Dish, T, Waiter);
  .queue.add(Q, CompleteOrder).