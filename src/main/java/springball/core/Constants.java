package springball.core;

public class Constants {
    public static double MIN_MASS = 0.1;
    public static double MAX_MASS = 5;
    public static double MIN_RESTLENGTH = 10;
    public static double MAX_RESTLENGTH = 1000;
    public static double MIN_THIKNESS = 10;
    public static double MAX_THIKNESS = 100;
    public static double MIN_STIFNESS = 0.0;
    public static double MAX_STIFNESS = 100;
    public static double MIN_VISCOSITY = 0;
    public static double MAX_VISCOSITY = 20;
    public static double MAX_DELTA_TIME = 0.1;
    public static String leftMouseButtonName = "PRIMARY";

    public static double GetInRange(double param, double max, double min) {
        return Math.min(max, Math.max(min, param));
    }
}
