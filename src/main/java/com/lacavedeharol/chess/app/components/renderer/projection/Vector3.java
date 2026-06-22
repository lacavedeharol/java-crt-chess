package com.lacavedeharol.chess.app.components.renderer.projection;

/**
 * A 3D vector.
 */
public class Vector3 {

    /**
     * The x coordinate.
     */
    public double x;

    /**
     * The y coordinate.
     */
    public double y;

    /**
     * The z coordinate.
     */
    public double z;

    /**
     * The w coordinate.
     */
    public double w = 1.0;

    /**
     * Constructor.
     * 
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordinate.
     */
    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Constructor.
     * 
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordinate.
     * @param w the w coordinate.
     */
    public Vector3(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Constructor.
     */
    public Vector3() {
        this(0, 0, 0);
    }
}
