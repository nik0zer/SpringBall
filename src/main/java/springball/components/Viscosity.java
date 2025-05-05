package springball.components;

import springball.core.Constants;
import springball.core.GameComponent;

public class Viscosity extends GameComponent {
    public double dragCoefficient;
    private Rigidbody rb;

    public Viscosity(double dragCoefficient) {
        this.dragCoefficient = Math.min(Constants.MAX_VISCOSITY, Math.max(dragCoefficient, Constants.MIN_VISCOSITY));
    }

    @Override
    public void start() {
        rb = gameObject.getComponent(Rigidbody.class).orElse(null);
         if (rb == null) {
            System.err.println("Viscosity requires a Rigidbody on its own GameObject!");
        }
    }

    @Override
    public void update(double deltaTime) {
        if (rb == null || rb.isKinematic) return;
        var velocity = rb.velocity;
        var dragForce = velocity.scale(-dragCoefficient);
        rb.addForce(dragForce);
    }
}