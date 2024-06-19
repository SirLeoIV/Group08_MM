package com.sailing.math.functions;

import com.sailing.math.StateSystem;
import com.sailing.math.data_structures.Vector;
import com.sailing.math.data_structures.Vector2D;
import com.sailing.math.physics.Constants;

/**
 * D = 1/2 * p * C * V^2 * A                            <br>
 * Where:                                               <br>
 * D = drag                                             <br>
 * p = water density                                    <br>
 * C = water drag coefficient                           <br>
 * V = apparent water swell speed                       <br>
 * A = hull area underwater                             <br>
 * ==> roh * (m/s)^2 * m^2 = (kg/m^3) * (m/s)^2 * m^2 = (kg * m^2 * m^2) / (m^3 * s^2) = kg * m / s^2 = N
 */
public class WaterDragFunction implements Function {
    private static final Vector2D FLUID_SPEED = new Vector2D(0, 0); // m/s, representing still water

    @Override
    public Vector eval(Vector positions, Vector velocities, double mass, double h, double t) {
        Vector2D boatSpeed = new Vector2D(velocities.getValue(2), velocities.getValue(3));
        Vector2D apparentSwell = FLUID_SPEED.subtract(boatSpeed);
        double p = Constants.WATER_DENSITY;
        double v = apparentSwell.getLength();
        double C = Constants.WATER_DRAG_COEFFICIENT;
        double A = Constants.AREA_UNDERWATER;

        double dragForce = 0.5 * p * C * A * Math.pow(v, 2);
        Vector dragDirection = apparentSwell.normalize();
        return dragDirection.multiplyByScalar(dragForce);
    }

    public Vector eval(StateSystem stateSystem, double h) {
        return eval(stateSystem.getPosition(), stateSystem.getVelocity(), stateSystem.getMass(), h, stateSystem.getTime());
    }
}