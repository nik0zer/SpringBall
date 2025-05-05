package springball.components;

import springball.core.Constants;
import springball.core.GameComponent;
import springball.core.Vector2D;

public class Rigidbody extends GameComponent {

    private double mass = 1.0;
    public boolean isKinematic = false;
    public Vector2D velocity = new Vector2D();
    private Vector2D netForce = new Vector2D();

    public double getMass() {
        return mass;
    }

    public Vector2D getNetForce() {
        return netForce;
    }

    public Rigidbody(double mass, boolean isKinematic) {
        this.mass = Constants.GetInRange(mass, Constants.MAX_MASS, Constants.MIN_MASS);
        this.isKinematic = isKinematic;
    }

    public Rigidbody() {
        this(1.0, false);
    }

    public void addForce(Vector2D force) {
        if (!isKinematic) {
            this.netForce = this.netForce.add(force);
        }
    }

    public void setMass(double mass) {
        this.mass = Constants.GetInRange(mass, Constants.MAX_MASS, Constants.MIN_MASS);
    }

    @Override
    public void update(double deltaTime) {}

    @Override
    public void lateUpdate(double deltaTime) {
        if (isKinematic) {
            netForce = new Vector2D();
            return;
        }
        Vector2D acceleration = netForce.scale(1.0 / mass);
        velocity = velocity.add(acceleration.scale(deltaTime));
        gameObject.position = gameObject.position.add(velocity.scale(deltaTime));
        netForce = new Vector2D();
    }
}