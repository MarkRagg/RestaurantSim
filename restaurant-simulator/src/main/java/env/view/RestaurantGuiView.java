package env.view;
import javax.swing.*;

import env.interfaces.Restaurant;
import env.model.Position2D;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class RestaurantGuiView extends JFrame implements RestaurantView {

    private static final Random RAND = new Random();

    private static Color calculateColor(String agent) {
        if (agent.contains("chef")) {
            return smoothColor(Color.RED);
        } else if (agent.contains("waiter")) {
            return smoothColor(Color.YELLOW);
        } else if (agent.contains("customer")) {
            return smoothColor(Color.GREEN);
        }
        return new Color(RAND.nextInt(256), RAND.nextInt(256), RAND.nextInt(256));
    }

    private static Color getTableColor() {
      Color brown = new Color(165, 42, 42);
      return smoothColor(brown);
    }

    private static Color smoothColor(Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 2);
    }

    private final Restaurant model;
    private final Map<Position2D, JButton> buttonsGrid = new HashMap<>();
    private final Map<String, Color> agentColors = new HashMap<>();

    public RestaurantGuiView(Restaurant model) {
        this.model = Objects.requireNonNull(model);

        JPanel contentPane = new JPanel(new BorderLayout());
        JPanel grid = new JPanel(new GridLayout(model.getHeight(), model.getWidth()));
        for (int y = 0; y < model.getHeight(); y++) {
            for (int x = 0; x < model.getWidth(); x++) {
                JButton b = new JButton("   ");
                grid.add(b);
                buttonsGrid.put(Position2D.of(x, y), b);
            }
        }
        contentPane.add(grid, BorderLayout.CENTER);
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 1, 60, (int) 60);
        contentPane.add(slider, BorderLayout.SOUTH);
        // slider.addChangeListener(e -> model.setFPS(slider.getValue()));
        setContentPane(contentPane);
        pack();
    }

    private Color getColorForAgent(String agent) {
        if (!agentColors.containsKey(agent)) {
            agentColors.put(agent, calculateColor(agent));
        }
        return agentColors.get(agent);
    }

    @Override
    public Restaurant getModel() {
        return model;
    }

    private void updateView() {
        buttonsGrid.values().forEach(b -> {
            b.setText(" ");
            b.setBackground(Color.WHITE);
            b.setForeground(UIManager.getColor("Button.foreground"));
            b.setEnabled(true);
        });
        model.getTables().forEach(table -> {
            Position2D pos = table.getPosition();
            JButton b = buttonsGrid.get(pos);
            Color c = getTableColor();
            b.setBackground(c);
            b.setForeground(Color.BLACK);
            b.setText(table.getId().toString().replace("table_", "T"));
            b.setFont(new Font("Arial", Font.PLAIN, 8));
        });
        model.getAllAgents().forEach(a -> {
            Position2D pos = model.getAgentPosition(a);
            JButton b = buttonsGrid.get(pos);
            Color c = getColorForAgent(a);
            b.setBackground(c);
            b.setForeground(Color.BLACK);
            String label = a.replace("customer_", "C")
                .replace("waiter_", "W")
                .replace("chef_", "CH");
            b.setText(label);
            b.setFont(new Font("Arial", Font.PLAIN, 8));
        });
        repaint();
    }

    @Override
    public void notifyModelChanged() {
        try {
            SwingUtilities.invokeAndWait(this::updateView);
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}