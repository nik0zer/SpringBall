package springball;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import springball.components.Draggable;
import springball.components.Rigidbody;
import springball.components.Spring;
import springball.components.Sprite;
import springball.components.Viscosity;
import springball.core.Constants;
import springball.core.GameObject;
import springball.core.Vector2D;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;

public class SpringBallApp extends Application {
    private List<GameObject> gameObjects = new ArrayList<>();
    private long lastNanoTime;
    private Pane simulationPane;

    private Rigidbody ballRb;
    private Spring springComponent;
    private Viscosity viscosityComponent;

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane mainLayout = new BorderPane();
        simulationPane = new Pane();
        mainLayout.setCenter(simulationPane);

        var anchor = new GameObject(new Vector2D(400, 200), new Vector2D(30, 30));
        anchor.addComponent(new Sprite("/anchorText.png"));
        anchor.addComponent(new Rigidbody(1.0, true));
        anchor.addComponent(new Draggable());

        var ball = new GameObject(new Vector2D(400, 400), new Vector2D(60, 60));
        ball.addComponent(new Sprite("/ballText.png"));
        ballRb = new Rigidbody(1.0, false);
        ball.addComponent(ballRb);
        ball.addComponent(new Draggable());
        viscosityComponent = new Viscosity(1.0);
        ball.addComponent(viscosityComponent);

        var spring = new GameObject(new Vector2D(0, 0), new Vector2D(10, 10));
        springComponent = new Spring(anchor, ball, 10.0, 100.0, 10.0);
        spring.addComponent(springComponent);
        spring.addComponent(new Sprite("/spring.png"));

        gameObjects.add(spring);
        gameObjects.add(anchor);
        gameObjects.add(ball);


        for (var gameObject : gameObjects) {
            Node view = gameObject.getView();
            if (view != null) {
                simulationPane.getChildren().add(view);
            } else {
                System.err.println("Warning: GameObject view is null: " + gameObject);
            }
        }

        HBox controlPanel = createControlPanel();
        mainLayout.setBottom(controlPanel);
        Scene scene = new Scene(mainLayout, 800, 650);

        lastNanoTime = System.nanoTime();
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                double deltaTime = (currentNanoTime - lastNanoTime) / 1000000000.0;
                lastNanoTime = currentNanoTime;
                deltaTime = Math.min(deltaTime, Constants.MAX_DELTA_TIME);

                for (GameObject go : gameObjects) {
                    go.update(deltaTime);
                }

                for (GameObject go : gameObjects) {
                    go.lateUpdate(deltaTime);
                }
            }
        };
        gameLoop.start();

        primaryStage.setTitle("SpringBall Simulation with Controls");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createControlPanel() {
        HBox controls = new HBox(15);
        controls.setPadding(new Insets(10));
        controls.setAlignment(Pos.CENTER);

        controls.getChildren().add(createSliderControl(
                "Вязкость:", Constants.MIN_VISCOSITY, Constants.MAX_VISCOSITY, viscosityComponent.dragCoefficient,
                (value) -> viscosityComponent.dragCoefficient = value
        ));
        controls.getChildren().add(createSliderControl(
                "Жесткость:", Constants.MIN_STIFNESS, Constants.MAX_STIFNESS, springComponent.getStiffness(),
                (value) -> springComponent.setStiffness(value)
        ));
        controls.getChildren().add(createSliderControl(
                "Длина покоя:", Constants.MIN_RESTLENGTH, Constants.MAX_RESTLENGTH, springComponent.getRestLength(),
                (value) -> springComponent.setRestLength(value)
        ));
        controls.getChildren().add(createSliderControl(
                "Масса шара:", Constants.MIN_MASS, Constants.MAX_MASS, ballRb.getMass(),
                (value) -> ballRb.setMass(value)
        ));

        for (var control : controls.getChildren()) {
            HBox.setHgrow(control, Priority.ALWAYS);
        }

        return controls;
    }

    @FunctionalInterface
    interface SliderValueUpdater {
        void update(double value);
    }

    private VBox createSliderControl(String labelText, double min, double max, double defaultValue, SliderValueUpdater updater) {
        VBox vbox = new VBox(5);
        vbox.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(labelText);
        Label valueLabel = new Label(String.format("%.2f", defaultValue));

        Slider slider = new Slider(min, max, defaultValue);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((max - min) / 2);

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double roundedValue = newValue.doubleValue();
            valueLabel.setText(String.format("%.2f", roundedValue));
            updater.update(roundedValue);
        });

        vbox.getChildren().addAll(nameLabel, slider, valueLabel);
        return vbox;
    }

    public static void main(String[] args) {
        launch(args);
    }
}