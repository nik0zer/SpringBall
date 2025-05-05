package springball.core;

import javafx.scene.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameObject {
    public Vector2D position;
    public Vector2D size;
    public double rotation;
    private List<GameComponent> components = new ArrayList<>();
    private Node view;

    public GameObject(Vector2D position, Vector2D size, double rotation) {
        this.position = position;
        this.rotation = rotation;
        this.size = size;
    }

    public GameObject(Vector2D position, Vector2D size) {
        this(position, size, 0.0);
    }

    public GameObject() {
        this(new Vector2D(), new Vector2D(), 0.0);
    }

    public void addComponent(GameComponent component) {
        components.add(component);
        component.setGameObject(this);
        component.start();
    }

    @SuppressWarnings("unchecked")
    public <T extends GameComponent> Optional<T> getComponent(Class<T> componentClass) {
        for (GameComponent component : components) {
            if (componentClass.isInstance(component)) {
                return Optional.of((T) component);
            }
        }
        return Optional.empty();
    }

    public void update(double deltaTime) {
        for (GameComponent component : components) {
            component.update(deltaTime);
        }
    }

    public void lateUpdate(double deltaTime) {
        for (GameComponent component : components) {
            component.lateUpdate(deltaTime);
        }
    }

    public void setView(Node view) {
        this.view = view;
    }

    public Node getView() {
        return view;
    }
}