package com.sailing.math.functions;

import com.sailing.math.StateSystem;
import com.sailing.math.data_structures.Vector;
import com.sailing.math.data_structures.Vector2D;

/**
 * F = D + L                            <br>
 * Where:                               <br>
 * F = wind force in Newtons            <br>
 * D = drag force in Newtons            <br>
 * L = lift force in Newtons            <br>
 *                                      <p>
 * a = F / m                            <br>
 * Where:                               <br>
 * a = acceleration in m/s^2            <br>
 * F = wind force in Newtons            <br>
 * m = mass in kg                       <br>
 * ==> N / kg = kg * (m/s^2) / kg = m/s^2
 */
public class WindForceAccelerationFunction implements Function {

    /**
     *
     * @param v1 position of Boat. (x0 = x, x1 = y, x2 = angle in degrees boat, x3 = angle in degrees sail (relative to boat)
     * @param v2 velocities (Wind and Boat). (Wind: x0-x1, Boat x2-x3)
     * @param m mass
     * @param h step size
     * @param t time
     * @return Vector with the acceleration of the boat in m/s^2.
     */
    public Vector eval(Vector v1, Vector v2, double m, double h, double t) {
        Vector acceleration = calculateAcceleration(v1, v2, m, h, t);
        Vector2D acceleration2D = (new Vector2D(acceleration.getValue(0), acceleration.getValue(1))).toPolar();

        double angleBoatForce = v1.getValue(2) - Math.toDegrees(acceleration2D.getX2());

        Vector2D accelerationWithRespectToBoat = new Vector2D(
                acceleration2D.getX1() * Math.cos(Math.toRadians(angleBoatForce)),
                Math.toRadians(v1.getValue(2)),
                true
        );

        return accelerationWithRespectToBoat.toCartesian();
    }

    public Vector eval(StateSystem stateSystem, double h) {
        return eval(stateSystem.getPosition(), stateSystem.getVelocity(), stateSystem.getMass(), h, stateSystem.getTime());
    }

    public Vector calculateAcceleration(Vector v1, Vector v2, double m, double h, double t) {
        Vector drag = new DragFunction().eval(v1, v2, m, h, t);
        Vector lift = new LiftFunction().eval(v1, v2, m, h, t);

        //return drag.add(lift).multiplyByScalar(1d / m);
        Vector waterDrag = new WaterDragFunction().eval(v1, v2, m, h, t);
        return drag.add(lift).add(waterDrag).multiplyByScalar(1d / m);
    }

    public Vector calculateAcceleration(StateSystem stateSystem, double h) {
        return calculateAcceleration(stateSystem.getPosition(), stateSystem.getVelocity(), stateSystem.getMass(), h, stateSystem.getTime());
    }



}
