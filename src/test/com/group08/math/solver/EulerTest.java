package com.math.solver;

import com.math.StateSystem;
import com.math.Vector;
import com.math.functions.Function;
import com.math.functions.TestFunction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EulerTest {

    @Test
    void testEulerOnSimpleFunction() {
        Vector positions = new Vector(0,0,0);
        Vector velocities = new Vector(0,0,0);
        Vector accelerations = new Vector(0,0,0);
        double mass = 1;
        double h = 1;
        double t = 0;
        StateSystem system = new StateSystem(positions, velocities, accelerations, mass);
        Solver solver = new Euler();
        Function f = new TestFunction();
        ArrayList<StateSystem> history = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            history.add(system);
            system = solver.nextStep(f, system.getPosition(), system.getVelocity(), system.getMass(), h, t);
        }

        assertEquals(10, history.size());
        assertEquals(new Vector(0, 0, 0), history.get(0).getPosition());
        assertEquals(new Vector(0, 0, 0), history.get(0).getVelocity());

        assertEquals(new Vector(1, 1, 1), history.get(2).getPosition());
        assertEquals(new Vector(2, 2, 2), history.get(0).getVelocity());

        assertEquals(new Vector(3, 3, 3), history.get(3).getPosition());
        assertEquals(new Vector(3, 3, 3), history.get(0).getVelocity());

        assertEquals(new Vector(6, 6, 6), history.get(4).getPosition());
        assertEquals(new Vector(4, 4, 4), history.get(0).getVelocity());

        assertEquals(new Vector(36, 36, 36), history.get(10).getPosition());
        assertEquals(new Vector(10, 10, 10), history.get(0).getVelocity());
        assertEquals(1, 0);
    }
}