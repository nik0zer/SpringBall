package springball.components;

import springball.core.Constants;
import springball.core.GameComponent;
import springball.core.GameObject;
import springball.core.Vector2D;

public class Spring extends GameComponent {
    private GameObject firstObject;
    private GameObject secondObject;
    private double thickness = 5.0;

    private double stiffness;
    private double restLength;

    private Rigidbody rbA;
    private Rigidbody rbB;

    public double getStiffness() {
        return stiffness;
    }

    public void setStiffness(double stiffness) {
        this.stiffness = Constants.GetInRange(stiffness, Constants.MAX_STIFNESS, Constants.MIN_STIFNESS);
    }

    public double getRestLength() {
        return restLength;
    }

    public void setRestLength(double restLength) {
        this.restLength = Constants.GetInRange(restLength, Constants.MAX_RESTLENGTH, Constants.MIN_RESTLENGTH);
    }

    public double getThickness() {
        return thickness;
    }

    public void setThickness(double thickness) {
        this.thickness = Constants.GetInRange(thickness, Constants.MAX_THIKNESS, Constants.MIN_THIKNESS);
    }

    public Spring(GameObject firstObject, GameObject secondObject, double stiffness, double restLength, double thickness) {
        if (firstObject == null || secondObject == null) {
             throw new IllegalArgumentException("Connected objects cannot be null");
        }
        this.firstObject = firstObject;
        this.secondObject = secondObject;
        setStiffness(stiffness);
        setRestLength(restLength);
        setThickness(thickness);
    }


    public Spring(GameObject firstObject, GameObject secondObject, double stiffness, double restLength) {
        this(firstObject, secondObject, stiffness, restLength, 5.0);
    }


    @Override
    public void start() {

        rbA = firstObject.getComponent(Rigidbody.class).orElse(null);
        rbB = secondObject.getComponent(Rigidbody.class).orElse(null);

        if (rbA == null) {
            System.err.println("Spring requires a Rigidbody on the first connected GameObject!");
        }
        if (rbB == null) {
            System.err.println("Spring requires a Rigidbody on the second connected GameObject!");
        }


         if (this.gameObject == null) {
            System.err.println("Spring component needs to be attached to a GameObject!");
        }
    }

    @Override
    public void update(double deltaTime) {

        if (this.gameObject == null || firstObject == null || secondObject == null) return;
        if (rbA == null || rbB == null) return;

        Vector2D posA = firstObject.position;
        Vector2D posB = secondObject.position;


        Vector2D delta = posB.subtract(posA);
        double currentLength = delta.magnitude();

        if (currentLength > 0.0001) {
            double displacement = currentLength - restLength;
            double forceMagnitude = stiffness * displacement;
            Vector2D forceDirection = delta.normalize();
            Vector2D springForce = forceDirection.scale(forceMagnitude);

            rbA.addForce(springForce);
            rbB.addForce(springForce.scale(-1.0));

            this.gameObject.position.x = (posA.x + posB.x) / 2.0;
            this.gameObject.position.y = (posA.y + posB.y) / 2.0;

            if (this.gameObject.size == null) {
                 this.gameObject.size = new Vector2D(currentLength, thickness);
            } else {
                 this.gameObject.size.x = currentLength;
                 this.gameObject.size.y = thickness;
            }

            double angleRadians = Math.atan2(delta.y, delta.x);
            this.gameObject.rotation = Math.toDegrees(angleRadians);
        } else {
             if (this.gameObject.size == null) {
                 this.gameObject.size = new Vector2D(0, thickness);
             } else {
                 this.gameObject.size.x = 0;
                 this.gameObject.size.y = thickness;
             }
             this.gameObject.position.x = posA.x;
             this.gameObject.position.y = posA.y;
             this.gameObject.rotation = 0;
        }
    }
}