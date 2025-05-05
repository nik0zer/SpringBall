package springball.components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import springball.core.GameComponent;
import springball.core.Vector2D;

import java.io.InputStream;

public class Sprite extends GameComponent {

    private String imagePath;
    private ImageView imageView;
    private Rotate rotationTransform;
    private double pivotX = 0;
    private double pivotY = 0;

    public Sprite(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            throw new IllegalArgumentException("Image path cannot be null or empty");
        }
        this.imagePath = imagePath.startsWith("/") ? imagePath : "/" + imagePath;
    }

    @Override
    public void start() {
        if (gameObject == null) {
            System.err.println("Sprite component cannot start without a GameObject!");
            return;
        }

        initializeGameObjectSizeIfNeeded();
        try (InputStream stream = getClass().getResourceAsStream(imagePath)) {
            if (stream == null) {
                System.err.println("Error: Could not find image resource at path: " + imagePath);
                this.imageView = createFallbackView();
            } else {
                Image image = new Image(stream);
                if (image.isError()) {
                    System.err.println("Error loading image from path: " + imagePath);
                    if (image.getException() != null) image.getException().printStackTrace();
                    this.imageView = createFallbackView();
                } else {
                    this.imageView = new ImageView(image);

                    if (gameObject.size == null) {
                         gameObject.size = new Vector2D(image.getWidth(), image.getHeight());
                         System.out.println("Sprite: Initialized GameObject size to natural image size: " + gameObject.size);
                    }
                    calculatePivot();


                    this.rotationTransform = new Rotate(0, pivotX, pivotY);
                    this.imageView.getTransforms().add(rotationTransform);
                }
            }
        } catch (Exception e) {
            System.err.println("Exception while loading image: " + imagePath);
            e.printStackTrace();
            this.imageView = createFallbackView();

             if (gameObject != null && gameObject.size == null) {
                 gameObject.size = new Vector2D(0, 0);
             }
        }

        gameObject.setView(this.imageView);
        applyTransformations();
    }

    private void initializeGameObjectSizeIfNeeded() {
         if (gameObject != null && gameObject.size == null) {
             gameObject.size.x = 10;
             gameObject.size.y = 10;
             System.out.println("Sprite: GameObject.size is null, will initialize after image load.");
         }
    }



    private void calculatePivot() {
        pivotX = gameObject.size.x / 2.0;
        pivotY = gameObject.size.y / 2.0;
    }

    private ImageView createFallbackView() {
        ImageView fallback = new ImageView();
        this.rotationTransform = new Rotate(0, 0, 0);
        fallback.getTransforms().add(rotationTransform);
        pivotX = 0;
        pivotY = 0;
        return fallback;
    }

    @Override
    public void update(double deltaTime) {
        applyTransformations();
    }

    private void applyTransformations() {
        calculatePivot();
        if (gameObject != null && imageView != null) {
            Vector2D targetSize = gameObject.size;
            if (targetSize != null && targetSize.x > 0 && targetSize.y > 0) {
                imageView.setFitWidth(targetSize.x);
                imageView.setFitHeight(targetSize.y);
            } else {
                imageView.setFitWidth(-1);
                imageView.setFitHeight(-1);
                if (gameObject.size == null || gameObject.size.x <= 0 || gameObject.size.y <=0 ) {
                    if (imageView.getImage() != null) {
                        gameObject.size = new Vector2D(imageView.getImage().getWidth(), imageView.getImage().getHeight());
                    } else {
                        gameObject.size = new Vector2D(0, 0);
                    }
                }
            }

            imageView.setTranslateX(gameObject.position.x - pivotX);
            imageView.setTranslateY(gameObject.position.y - pivotY);

            if (rotationTransform != null) {
                rotationTransform.setPivotX(pivotX);
                rotationTransform.setPivotY(pivotY);
                rotationTransform.setAngle(gameObject.rotation);
            }
        }
    }

    public void setSize(double size, double height) {
        if (gameObject != null) {
            if (gameObject.size == null) {
                gameObject.size = new Vector2D(size, height);
            } else {
                gameObject.size.x = size;
                gameObject.size.y = height;
            }
        } else {
             System.err.println("Cannot set size: Sprite is not attached to a GameObject.");
        }
    }

    public Vector2D getSize() {
        if (gameObject != null && gameObject.size != null) {
            return new Vector2D(gameObject.size.x, gameObject.size.y);
        }
        return new Vector2D(0, 0);
    }
}
