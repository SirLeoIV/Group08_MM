package com.sailing.math.solver;

import com.sailing.math.DifferentialEquation;
import com.sailing.math.StateSystem;
import com.sailing.math.data_structures.Vector;
import com.sailing.math.functions.Function;

import static com.sailing.math.data_structures.Round.round;

/**
 * 2 step Adams-Moulton method (predictor-corrector)
 */
public class AdamsMoulton implements Solver {

    public AdamsMoulton(Solver predictor) {
        this.predictor = predictor;
    }

    StateSystem lastState;
    Solver predictor;

    @Override
    public StateSystem nextStep(Function f, Vector positions, Vector velocities, double mass, double h, double t) {

        if (lastState == null) {
            lastState = new StateSystem(positions, velocities, f.eval(positions, velocities, mass, h, t), mass);
        }

        // f(i+1)
        StateSystem prediction = predictor.nextStep(f, positions, velocities, mass, h, t);
        Vector[] fi1 = DifferentialEquation.solve(
                f,
                prediction.getPosition(),
                prediction.getVelocity(),
                prediction.getMass(),
                h,
                prediction.getTime());

        fi1[0] = new Vector(
                fi1[0].getValue(2),
                fi1[0].getValue(3),
                0,
                0
        );
        fi1[1] = new Vector(
                0,
                0,
                fi1[1].getValue(0),
                fi1[1].getValue(1)
        );

        // f(i)
        Vector[] fi = DifferentialEquation.solve(
                f,
                positions,
                velocities,
                mass,
                h,
                t);

        fi[0] = new Vector(
                fi[0].getValue(2),
                fi[0].getValue(3),
                0,
                0
        );
        fi[1] = new Vector(
                0,
                0,
                fi[1].getValue(0),
                fi[1].getValue(1)
        );

        // f(i-1)
        Vector[] fim1 = new Vector[] {
                new Vector(
                        lastState.getVelocity().getValue(2),
                        lastState.getVelocity().getValue(3),
                        0,
                        0
                ),
                new Vector(
                        0,
                        0,
                        lastState.getAcceleration().getValue(0),
                        lastState.getAcceleration().getValue(1)
                )
        };

        Vector positionsWi1 = positions
                        .add(fi1[0].multiplyByScalar(5)
                                .add(fi[0].multiplyByScalar(8))
                                .add(fim1[0].multiplyByScalar(-1))
                                .multiplyByScalar(h / 12d));

        Vector velocitiesWi1 = velocities
                        .add(fi1[1].multiplyByScalar(5)
                                .add(fi[1].multiplyByScalar(8))
                                .add(fim1[1].multiplyByScalar(-1))
                                .multiplyByScalar(h / 12d));

        Vector accelerationsWi1 = f.eval(positionsWi1, velocitiesWi1, mass, h, round(t + h));

        StateSystem newState = new StateSystem(positionsWi1, velocitiesWi1, accelerationsWi1, mass, round(t + h));
        lastState = newState.copy();

        return newState;
    }
}
