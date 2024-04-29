package com.sailing.math.solver;

import com.sailing.math.StateSystem;
import com.sailing.math.Vector;
import com.sailing.math.functions.Function;
import com.sailing.math.functions.TestFunction;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class RungeKuttaTest {

    @Test
    public void testRungeKuttaOnSimpleFunction() {
        Vector positions = new Vector(0,0,0);
        Vector velocities = new Vector(0,0,0);
        Vector accelerations = new Vector(0,0,0);
        double mass = 1;
        double h = 1;
        double t = 0;
        StateSystem system = new StateSystem(positions, velocities, accelerations, mass);
        Solver solver = new RungeKutta();
        Function f = new TestFunction();
        ArrayList<StateSystem> history = new ArrayList<>();
        history.add(system);

        for (int i = 0; i < 10; i++) {
            system = solver.nextStep(f, system.getPosition(), system.getVelocity(), system.getMass(), h, t);
            history.add(system);
        }

        assertEquals(11, history.size());
        assertEquals(new Vector(0, 0, 0), history.get(0).getPosition());
        assertEquals(new Vector(0, 0, 0), history.get(0).getVelocity());

        assertEquals(new Vector(0.5, 0.5, 0.5), history.get(1).getPosition());
        assertEquals(new Vector(1, 1, 1), history.get(1).getVelocity());

        assertEquals(new Vector(2, 2, 2), history.get(2).getPosition());
        assertEquals(new Vector(2, 2, 2), history.get(2).getVelocity());

        assertEquals(new Vector(4.5, 4.5, 4.5), history.get(3).getPosition());
        assertEquals(new Vector(3, 3, 3), history.get(3).getVelocity());

        assertEquals(new Vector(8, 8, 8), history.get(4).getPosition());
        assertEquals(new Vector(4, 4, 4), history.get(4).getVelocity());

        assertEquals(new Vector(40.5, 40.5, 40.5), history.get(9).getPosition());
        assertEquals(new Vector(9, 9, 9), history.get(9).getVelocity());
    }
}