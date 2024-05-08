package com.sailing.math.functions;

import com.sailing.math.StateSystem;
import com.sailing.math.data_structures.Vector;
import com.sailing.math.data_structures.Vector2D;
import com.sailing.math.physics.Coefficients;
import com.sailing.math.physics.Constants;

/**
 * L = 1/2 * p * C(beta) * V^2 * S
 * Where:
 * L = lift
 * p = air density
 * C(beta) = lift coefficient in respect to beta
 * beta = angle of attack
 * V = apparent wind speed
 * S = sail area
 */
public class LiftFunction implements Function {

    /**
     *
     * @param v1 position of Boat. (x0 = x, x1 = y, x2 = angle in degrees boat, x3 = angle in degrees sail (relative to boat)
     * @param v2 velocities (Wind and Boat). (Wind: x0-x1, Boat x2-x3)
     * @param m mass. (ignored in this function)
     * @param h step size. (ignored in this function)
     * @param t time. (ignored in this function)
     * @return Vector with the lift force.
     */
    public Vector eval(Vector v1, Vector v2, double m, double h, double t) {
        Vector2D windVelocity = new Vector2D(v2.getValue(0), v2.getValue(1));
        Vector2D boatVelocity = new Vector2D(v2.getValue(2), v2.getValue(3));
        Vector2D apparentWind = windVelocity.subtract(boatVelocity);
        double v = apparentWind.getLength();
        double beta = Math.toDegrees(apparentWind.toPolar().getX2()) + (v1.getValue(2) + v1.getValue(3));
        if (beta < 0) beta += 360;
        double p = Constants.AIR_DENSITY;
        double C = Coefficients.calculateLiftCoefficient(beta);
        double s = Constants.SAIL_AREA;

        double lift = 0.5 * p * C * Math.pow(v, 2) * s;
        Vector liftDirection = apparentWind.rotate(-90).normalize();
        return liftDirection.multiplyByScalar(lift);
    };

    public Vector eval(StateSystem stateSystem, double h) {
        return eval(stateSystem.getPosition(), stateSystem.getVelocity(), stateSystem.getMass(), h, stateSystem.getTime());
    }
}
