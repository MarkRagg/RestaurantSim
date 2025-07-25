package env.model;

public record CustomerId (String id) {
  @Override
  public String toString() {
      return id;
  }
}
