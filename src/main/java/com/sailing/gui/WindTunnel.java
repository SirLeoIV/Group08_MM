package com.sailing.gui;

import com.sailing.Simulation;
import com.sailing.math.StateSystem;
import com.sailing.math.data_structures.Vector;
import com.sailing.math.data_structures.Vector2D;
import com.sailing.math.functions.DragFunction;
import com.sailing.math.functions.LiftFunction;
import com.sailing.math.functions.WindForceAccelerationFunction;
import com.sailing.math.solver.Euler;
import com.sailing.math.solver.Solver;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;

/**
 *
 */
class WindTunnel extends Pane {

    private final SailboatGUI sailboat;

    private final Simulation simulation;

    Arrow windVelocityArrow = new Arrow(100, 100, 100, 90, Color.ALICEBLUE);
    Arrow accelerationArrow = new Arrow(Sailing.X_CENTER, Sailing.Y_CENTER, 100, 90, Color.RED);
    Arrow velocityArrow = new Arrow(Sailing.X_CENTER, Sailing.Y_CENTER, 100, 90, Color.BLUE);

    Arrow dragForceArrow = new Arrow(Sailing.X_CENTER, Sailing.Y_CENTER, 100, 90, Color.GREEN);
    Arrow liftForceArrow = new Arrow(Sailing.X_CENTER, Sailing.Y_CENTER, 100, 90, Color.BLUEVIOLET);

    WindTunnel(SailboatGUI boat) {
        this.sailboat = boat;
        setStyle("-fx-background-color: grey;");

        // styling
        getStylesheets().add("/styling.css");
        setWidth(Sailing.WIDTH);
        setHeight(Sailing.HEIGHT);

        getChildren().addAll(sailboat);

        Vector position = new Vector(0, 0, 0, 0);
        Vector velocity = new Vector(1, 1, 0, 0);
        Vector acceleration = new Vector(0, 0);
        double mass = 100;
        double time = 0;
        StateSystem stateSystem = new StateSystem(position, velocity, acceleration, mass, time);
        Solver solver = new Euler();

        simulation = new Simulation(solver, stateSystem, 1);

        getChildren().add(windVelocityArrow);
        getChildren().add(accelerationArrow);
        getChildren().add(velocityArrow);
        getChildren().add(dragForceArrow);
        getChildren().add(liftForceArrow);
        drawArrows(simulation.getCurrentState());
        drawForceArrow(simulation.getCurrentState());

        Text windLabel = new Text("Wind");
        windLabel.setFill(Color.ALICEBLUE);
        windLabel.setX(120);
        windLabel.setY(90);
        getChildren().add(windLabel);

        setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT -> {
                    sailboat.steer(-1);
                    simulation.rotateBoat(-1);
                }
                case RIGHT -> {
                    sailboat.steer(1);
                    simulation.rotateBoat(1);
                }
                case UP -> {
                    sailboat.getSail().heaveSail(-1);
                    simulation.rotateSail(-1);
                }
                case DOWN -> {
                    sailboat.getSail().heaveSail(1);
                    simulation.rotateSail(1);
                }
                case SPACE -> {
                    simulation.step();
                    drawArrows(simulation.getCurrentState());
                }
           }
           drawForceArrow(simulation.getCurrentState());
       });

       Scale s = new Scale();
       s.setPivotX(Sailing.X_CENTER);
       s.setPivotY(Sailing.Y_CENTER);
       sailboat.getTransforms().add(s);

       setOnScroll(event -> {
           s.setX(s.getX() + event.getDeltaY() / 500);
           s.setY(s.getY() + event.getDeltaY() / 500);
       });
    }

    private void drawForceArrow(StateSystem currentState) {
        Vector drag = new DragFunction().eval(currentState, 1);
        Vector lift = new LiftFunction().eval(currentState, 1);
        Vector acceleration = new WindForceAccelerationFunction().eval(currentState, 1);
        Vector2D dragForceVector = new Vector2D(drag.getValue(0), drag.getValue(1));
        Vector2D liftForceVector = new Vector2D(lift.getValue(0), lift.getValue(1));
        Vector2D accelerationVector = new Vector2D(acceleration.getValue(0), acceleration.getValue(1));

        double scalar = 500;

        dragForceArrow.setLengthAndAngle(
                dragForceVector.getLength() * scalar * (1d/currentState.getMass()),
                dragForceVector.toPolar().getX2());
        liftForceArrow.setLengthAndAngle(
                liftForceVector.getLength() * scalar * (1d/currentState.getMass()),
                liftForceVector.toPolar().getX2());
        accelerationArrow.setLengthAndAngle(
                accelerationVector.getLength() * scalar,
                accelerationVector.toPolar().getX2());

    }

    private void drawArrows(StateSystem currentState) {
        Vector2D wind = new Vector2D(currentState.getVelocity().getValue(0), currentState.getVelocity().getValue(1));
        Vector2D boat = new Vector2D(currentState.getVelocity().getValue(2), currentState.getVelocity().getValue(3));
        Vector2D acceleration = new Vector2D(currentState.getAcceleration().getValue(0), currentState.getAcceleration().getValue(1));

        double scalar = 50;

        windVelocityArrow.setLengthAndAngle(wind.getLength() * scalar, wind.toPolar().getX2());
        accelerationArrow.setLengthAndAngle(acceleration.toPolar().getX1() * scalar, acceleration.toPolar().getX2());
        velocityArrow.setLengthAndAngle(boat.getLength() * scalar, boat.toPolar().getX2());
    }

    public SailboatGUI getSailboat() {
        return sailboat;
    }
}