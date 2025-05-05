package springball.components;

import javafx.scene.Node;
import springball.core.Constants;
import springball.core.GameComponent;
import springball.core.Vector2D;

public class Draggable extends GameComponent {

    private Node view;
    private Rigidbody rb;
    private boolean dragging = false;

    private boolean wasKinematic;

    private Vector2D dragStartGameObjectPos = new Vector2D();
    private Vector2D dragStartMouseScenePos = new Vector2D();

    @Override
    public void start() {
        this.view = gameObject.getView();
        this.rb = gameObject.getComponent(Rigidbody.class).orElse(null);

        if (view == null) {
            System.err.println("Draggable component requires a View (Node) on the GameObject!");
            return;
        }

        view.setOnMousePressed(event -> {
            if (!event.getButton().toString().equals(Constants.leftMouseButtonName)) return;
            dragging = true;

            dragStartGameObjectPos.x = gameObject.position.x;
            dragStartGameObjectPos.y = gameObject.position.y;
            dragStartMouseScenePos.x = event.getSceneX();
            dragStartMouseScenePos.y = event.getSceneY();

            if (rb != null) {
                wasKinematic = rb.isKinematic;
                rb.isKinematic = true;
                rb.velocity = new Vector2D();
            }
            event.consume();
        });

        view.setOnMouseDragged(event -> {
            if (!dragging || !event.isPrimaryButtonDown()) return;

            var mouseDeltaX = event.getSceneX() - dragStartMouseScenePos.x;
            var mouseDeltaY = event.getSceneY() - dragStartMouseScenePos.y;
            gameObject.position.x = dragStartGameObjectPos.x + mouseDeltaX;
            gameObject.position.y = dragStartGameObjectPos.y + mouseDeltaY;

            if (rb != null) {
                rb.velocity = new Vector2D();
            }
            event.consume();
        });

        view.setOnMouseReleased(event -> {
            if (!dragging || !event.getButton().toString().equals(Constants.leftMouseButtonName)) return;
            if (dragging) {
                dragging = false;
                if (rb != null) {
                    rb.isKinematic = wasKinematic;
                }
                event.consume();
            }
        });
    }

    @Override
    public void update(double deltaTime) {}
}