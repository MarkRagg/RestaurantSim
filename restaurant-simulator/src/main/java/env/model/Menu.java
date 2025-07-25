package env.model;

public enum Menu {
  PIZZA(new Dish("Pizza", 10)),
  PASTA(new Dish("Pasta", 8)),
  PAPPARDELLE(new Dish("Pappardelle al cinghiale", 12)),
  RISOTTO(new Dish("Risotto ai funghi", 11));

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