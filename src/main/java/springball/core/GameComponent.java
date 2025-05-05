package springball.core;

public abstract class GameComponent {
    protected GameObject gameObject;

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public void start() {}

    public abstract void update(double deltaTime);

    public void lateUpdate(double deltaTime) {}
}