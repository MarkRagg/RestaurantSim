package env.model;

public enum Menu {
  PIZZA(new Dish("Pizza", 10)),
  PASTA(new Dish("Pasta", 8)),
  TIRAMISU(new Dish("Tiramisu", 12)),
  COZZE(new Dish("Cozze", 11));

  private final Dish dish;

  Menu(Dish dish) {
    this.dish = dish;
  }

  public Dish getDish() {
    return dish;
  }

  public static Dish getRandomDish() {
    Menu[] values = Menu.values();
    int idx = (int) (Math.random() * values.length);
    return values[idx].getDish();
  }
}