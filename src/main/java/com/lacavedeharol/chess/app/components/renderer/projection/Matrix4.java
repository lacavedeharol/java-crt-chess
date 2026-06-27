package com.lacavedeharol.chess.app.components.renderer.projection;

/**
 * Represents a 4x4 matrix.
 */
public class Matrix4 {
    private double[][] m = new double[4][4];

    /**
     * Creates a new identity matrix.
     */
    public Matrix4() {
        identity();
    }

    /**
     * Sets the matrix to the identity matrix.
     */
    public void identity() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++)
                m[i][j] = (i == j) ? 1 : 0;
        }
    }

    /**
     * Creates a perspective projection matrix.
     * 
     * @param fovY   the field of view in the y direction (in radians)
     * @param aspect the aspect ratio of the viewport
     * @param near   the distance to the near clipping plane
     * @param far    the distance to the far clipping plane
     * @return the perspective projection matrix
     */
    public static Matrix4 perspective(double fovY, double aspect, double near, double far) {
        Matrix4 mat = new Matrix4();
        double tanHalfFovY = Math.tan(fovY / 2.0);
        double range = near - far;

        mat.m[0][0] = 1.0 / (aspect * tanHalfFovY);
        mat.m[1][1] = 1.0 / tanHalfFovY;
        mat.m[2][2] = (near + far) / range;
        mat.m[2][3] = -1.0;
        mat.m[3][2] = 2.0 * near * far / range;
        mat.m[3][3] = 0.0;
        return mat;
    }

    /**
     * Multiplies the matrix by a vector.
     * 
     * @param v the vector to multiply by
     * @return the result of the multiplication
     */
    public Vector3 multiply(Vector3 v) {
        double x = v.x * m[0][0] + v.y * m[1][0] + v.z * m[2][0] + m[3][0];
        double y = v.x * m[0][1] + v.y * m[1][1] + v.z * m[2][1] + m[3][1];
        double z = v.x * m[0][2] + v.y * m[1][2] + v.z * m[2][2] + m[3][2];
        double w = v.x * m[0][3] + v.y * m[1][3] + v.z * m[2][3] + m[3][3];

        if (w != 0)
            return new Vector3(x / w, y / w, z / w, w);
        return new Vector3(x, y, z, 1.0);
    }

    /**
     * Creates a translation matrix.
     * 
     * @param x the x translation
     * @param y the y translation
     * @param z the z translation
     * @return the translation matrix
     */
    public static Matrix4 translate(double x, double y, double z) {
        Matrix4 mat = new Matrix4();
        mat.m[3][0] = x;
        mat.m[3][1] = y;
        mat.m[3][2] = z;
        return mat;
    }

    /**
     * Creates a rotation matrix around the x axis.
     * 
     * @param angle the angle of rotation (in radians)
     * @return the rotation matrix
     */
    public static Matrix4 rotateX(double angle) {
        Matrix4 mat = new Matrix4();
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        mat.m[1][1] = c;
        mat.m[1][2] = s;
        mat.m[2][1] = -s;
        mat.m[2][2] = c;
        return mat;
    }

    /**
     * Creates a rotation matrix around the y axis.
     * 
     * @param angle the angle of rotation (in radians)
     * @return the rotation matrix
     */
    public static Matrix4 rotateY(double angle) {
        Matrix4 mat = new Matrix4();
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        mat.m[0][0] = c;
        mat.m[0][2] = s;
        mat.m[2][0] = -s;
        mat.m[2][2] = c;
        return mat;
    }

    /**
     * Multiplies the matrix by another matrix.
     *
     * @param other the matrix to multiply by
     * @return the result of the multiplication
     */
    public Matrix4 multiply(Matrix4 other) {
        Matrix4 result = new Matrix4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.m[i][j] = 0;
                for (int k = 0; k < 4; k++)
                    result.m[i][j] += this.m[k][j] * other.m[i][k];
            }
        }
        return result;
    }

    /**
     * Inverts the matrix.
     * 
     * @return the inverted matrix
     */
    public Matrix4 invert() {
        double[] m = new double[16];
        int k = 0;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                m[k++] = this.m[j][i];

        double[] inv = new double[16];
        double[] src = new double[16];

        int idx = 0;
        for (int c = 0; c < 4; c++)
            for (int r = 0; r < 4; r++)
                src[idx++] = this.m[c][r];

        inv[0] = src[5] * src[10] * src[15] -
                src[5] * src[11] * src[14] -
                src[9] * src[6] * src[15] +
                src[9] * src[7] * src[14] +
                src[13] * src[6] * src[11] -
                src[13] * src[7] * src[10];

        inv[4] = -src[4] * src[10] * src[15] +
                src[4] * src[11] * src[14] +
                src[8] * src[6] * src[15] -
                src[8] * src[7] * src[14] -
                src[12] * src[6] * src[11] +
                src[12] * src[7] * src[10];

        inv[8] = src[4] * src[9] * src[15] -
                src[4] * src[11] * src[13] -
                src[8] * src[5] * src[15] +
                src[8] * src[7] * src[13] +
                src[12] * src[5] * src[11] -
                src[12] * src[7] * src[9];

        inv[12] = -src[4] * src[9] * src[14] +
                src[4] * src[10] * src[13] +
                src[8] * src[5] * src[14] -
                src[8] * src[6] * src[13] -
                src[12] * src[5] * src[10] +
                src[12] * src[6] * src[9];

        inv[1] = -src[1] * src[10] * src[15] +
                src[1] * src[11] * src[14] +
                src[9] * src[2] * src[15] -
                src[9] * src[3] * src[14] -
                src[13] * src[2] * src[11] +
                src[13] * src[3] * src[10];

        inv[5] = src[0] * src[10] * src[15] -
                src[0] * src[11] * src[14] -
                src[8] * src[2] * src[15] +
                src[8] * src[3] * src[14] +
                src[12] * src[2] * src[11] -
                src[12] * src[3] * src[10];

        inv[9] = -src[0] * src[9] * src[15] +
                src[0] * src[11] * src[13] +
                src[8] * src[1] * src[15] -
                src[8] * src[3] * src[13] -
                src[12] * src[1] * src[11] +
                src[12] * src[3] * src[9];

        inv[13] = src[0] * src[9] * src[14] -
                src[0] * src[10] * src[13] -
                src[8] * src[1] * src[14] +
                src[8] * src[2] * src[13] +
                src[12] * src[1] * src[10] -
                src[12] * src[2] * src[9];

        inv[2] = src[1] * src[6] * src[15] -
                src[1] * src[7] * src[14] -
                src[5] * src[2] * src[15] +
                src[5] * src[3] * src[14] +
                src[13] * src[2] * src[7] -
                src[13] * src[3] * src[6];

        inv[6] = -src[0] * src[6] * src[15] +
                src[0] * src[7] * src[14] +
                src[4] * src[2] * src[15] -
                src[4] * src[3] * src[14] -
                src[12] * src[2] * src[7] +
                src[12] * src[3] * src[6];

        inv[10] = src[0] * src[5] * src[15] -
                src[0] * src[7] * src[13] -
                src[4] * src[1] * src[15] +
                src[4] * src[3] * src[13] +
                src[12] * src[1] * src[7] -
                src[12] * src[3] * src[5];

        inv[14] = -src[0] * src[5] * src[14] +
                src[0] * src[6] * src[13] +
                src[4] * src[1] * src[14] -
                src[4] * src[2] * src[13] -
                src[12] * src[1] * src[6] +
                src[12] * src[2] * src[5];

        inv[3] = -src[1] * src[6] * src[11] +
                src[1] * src[7] * src[10] +
                src[5] * src[2] * src[11] -
                src[5] * src[3] * src[10] -
                src[9] * src[2] * src[7] +
                src[9] * src[3] * src[6];

        inv[7] = src[0] * src[6] * src[11] -
                src[0] * src[7] * src[10] -
                src[4] * src[2] * src[11] +
                src[4] * src[3] * src[10] +
                src[8] * src[2] * src[7] -
                src[8] * src[3] * src[6];

        inv[11] = -src[0] * src[5] * src[11] +
                src[0] * src[7] * src[9] +
                src[4] * src[1] * src[11] -
                src[4] * src[3] * src[9] -
                src[8] * src[1] * src[7] +
                src[8] * src[3] * src[5];

        inv[15] = src[0] * src[5] * src[10] -
                src[0] * src[6] * src[9] -
                src[4] * src[1] * src[10] +
                src[4] * src[2] * src[9] +
                src[8] * src[1] * src[6] -
                src[8] * src[2] * src[5];

        double det = src[0] * inv[0] + src[1] * inv[4] + src[2] * inv[8] + src[3] * inv[12];

        Matrix4 resMatrix = new Matrix4();
        if (det == 0)
            return resMatrix;

        det = 1.0 / det;

        idx = 0;
        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 4; r++)
                resMatrix.m[c][r] = inv[idx++] * det;

        }
        return resMatrix;
    }

    /**
     * Creates a scaling matrix.
     *
     * @param x the x scale factor
     * @param y the y scale factor
     * @param z the z scale factor
     * @return the scaling matrix
     */
    public static Matrix4 scale(double x, double y, double z) {
        Matrix4 mat = new Matrix4();
        mat.m[0][0] = x;
        mat.m[1][1] = y;
        mat.m[2][2] = z;
        return mat;
    }

    /**
     * Returns a string representation of the matrix.
     * 
     * @return the string representation of the matrix
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append("[ ");
            for (int j = 0; j < 4; j++)
                sb.append(String.format("%.2f ", m[i][j]));
            sb.append("]\n");
        }
        return sb.toString();
    }

    /**
     * Projects a 3D point to a 2D screen coordinate.
     * 
     * @param x      the x coordinate of the point
     * @param y      the y coordinate of the point
     * @param z      the z coordinate of the point
     * @param width  the width of the viewport
     * @param height the height of the viewport
     * @return the projected 2D screen coordinate
     */
    public Vector3 project(double x, double y, double z, int width, int height) {
        Vector3 vec = new Vector3(x, y, z);
        Vector3 clip = this.multiply(vec);

        if (clip.w <= 0.1)
            return null;

        double screenX = (clip.x + 1.0) * 0.5 * width;
        double screenY = (1.0 - clip.y) * 0.5 * height;

        return new Vector3(screenX, screenY, clip.z);
    }
}