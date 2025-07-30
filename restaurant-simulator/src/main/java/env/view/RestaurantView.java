package env.view;

import env.interfaces.Restaurant;

public interface RestaurantView {
    Restaurant getModel();
    void notifyModelChanged();
}