order_queue(none).

!start_chef.

+!start_chef <-
  .queue.create(Q);
  -+order_queue(Q);
  .wait(100);
  !advice_all_waiters;
  !check_order_queue.

+!check_order_queue : order_queue(Q) <-
  .length(Q, QueueSize);
  if (QueueSize > 0) {
    .queue.remove(Q, Order);
    .print("Start cooking ", Order);
    Order = order(Dish, TableId, Waiter);
    Dish = dish(Name, PrepTime);
    preparing_dish(TableId, Name);
    .wait(PrepTime * 1000);
    !call_waiter(Waiter, Dish, TableId);
  } else {
    .wait(1000);
  };
  !check_order_queue.

+!call_waiter(Waiter, Dish, TableId) <-
  .send(Waiter, achieve, dish_ready(Dish, TableId)).

+!call_waiter_again(Dish, TableId)[source(Waiter)] <-
  .print("Ok, I'll try later");
  .wait(1000);
  !call_waiter(Waiter, Dish, TableId).

+!advice_all_waiters <-
  .broadcast(tell, chef_available).

+new_order(Order)[source(Waiter)] : order_queue(Q) <-
  !add_new_order(Order, Waiter).

+!add_new_order(Order, Waiter) : order_queue(Q) <-
  Order = order(Dish, T);
  CompleteOrder = order(Dish, T, Waiter);
  .print(Order ," Arriving of waiter ", Waiter);
  .queue.add(Q, CompleteOrder).

-!add_new_order(Order, Waiter) : order_queue(Q) <-
  .wait(100);
  !add_new_order(Order, Waiter).