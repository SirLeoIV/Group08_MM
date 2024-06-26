package com.sailing.gui;

import com.sailing.Sailboat;
import com.sailing.Simulation;
import com.sailing.math.StateSystem;
import com.sailing.math.data_structures.Vector;
import com.sailing.math.data_structures.Vector2D;
import com.sailing.math.solver.RungeKutta;
import com.sailing.math.solver.Solver;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
class WindTunnel extends Pane {

    private final SailboatGUI sailboat;

    private final Simulation simulation;

    private final Arrows arrows;

    double windVelocity = 8;

    // for dragging
    private double startX;
    private double startY;

    WindTunnel() {
        sailboat = new SailboatGUI(new Sailboat(), new SailboatGUI.SailGUI(), 1);
        setBackground(new Background(new BackgroundImage(Images.background, null, null, null, null)));
        arrows = new Arrows(sailboat);

        // styling
        getStylesheets().add("/styling.css");
        setWidth(Sailing.WIDTH);
        setHeight(Sailing.HEIGHT);

        getChildren().addAll(sailboat);

        Vector position = new Vector(0, 0, -90, 0);
        Vector velocity = new Vector(0, 1, 0, 0).normalize().multiplyByScalar(windVelocity);
        Vector acceleration = new Vector(0, 0);
        double mass = 100;
        double time = 0;
        StateSystem stateSystem = new StateSystem(position, velocity, acceleration, mass, time);
        Solver solver = new RungeKutta();

        simulation = new Simulation(solver, stateSystem, 0.01);
        Legend legend = new Legend(
                new Vector2D(-50, 20),
                "top",
                Legend.Stat.LIFT,
                Legend.Stat.DRAG,
                Legend.Stat.AERO,
                Legend.Stat.ACCEL,
                Legend.Stat.VEL_AW,
                Legend.Stat.VEL_TW);
        legend.setLayoutX(50);
        int legendScale = 60;
        legend.setLayoutY(Sailing.HEIGHT - legend.getChildren().size() * legendScale + 50);
        makeDraggable(legend, false, true, null, new double[]{20, 340});
        getChildren().add(legend);

        Stats windStats = new Stats(new Vector2D(0, 0), "right", "wind", stateSystem, sailboat);
        makeDraggable(windStats, true, false, new double[]{-260, 0}, null);

        final boolean[] running = {true};

        Stats boatStats = new Stats(new Vector2D(1300, 0), "left", "boat", stateSystem, sailboat);
        makeDraggable(boatStats, true, false, new double[]{0, 250}, null);

        ImageView manual = new ImageView(Images.manual);
        manual.setLayoutX(1300);
        manual.setLayoutY(500);
        makeDraggable(manual, false, true, null, new double[]{0, 320});

        getChildren().addAll(windStats, boatStats, manual);


        AnimationTimer timer = new AnimationTimer() {
            long last = 0;
            long count = 0;

            @Override
            public void handle(long now) {
                // execute 10 times per second
                if (now - last >= 10_000_000) {
                    windStats.update(simulation.getCurrentState());
                    boatStats.update(simulation.getCurrentState());
                    if (running[0]) {
                        simulation.run(1);
                        arrows.update(simulation.getCurrentState());
                        sailboat.repaintSail(simulation.getCurrentState());
                        count++;
                        if (count % 100 == 0) {
                            simulation.getCurrentState().log();
                        }
                        sailboat.setPosition(new Vector2D(simulation.getCurrentState().getPosition().getValue(0), simulation.getCurrentState().getPosition().getValue(1)));
                    }
                    last = now;
                }
            }
        };
        timer.start();


        getChildren().addAll(
                arrows);
        arrows.update(simulation.getCurrentState());

        sailboat.rotate(simulation.getCurrentState().getPosition().getValue(2));
        sailboat.getSail().rotate(simulation.getCurrentState().getPosition().getValue(3));

        final List<KeyCode> validKeys = Arrays.asList(KeyCode.UP, KeyCode.DOWN,
                KeyCode.LEFT, KeyCode.RIGHT, KeyCode.SPACE, KeyCode.ESCAPE);
        final Set<KeyCode> pressedKeys = new HashSet<>();
        setOnKeyReleased(e -> pressedKeys.clear());

        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                System.out.println("Escape");
                getChildren().add(new EscMenu(getChildren()));
            }
            if (validKeys.contains(e.getCode())) {
                boatStats.update(stateSystem);
                pressedKeys.add(e.getCode());
                if (pressedKeys.contains(KeyCode.UP)) {
                    simulation.rotateSail(-1);
                    sailboat.getSail().rotate(simulation.getCurrentState().getPosition().getValue(3));
                } if (pressedKeys.contains(KeyCode.DOWN)) {
                    simulation.rotateSail(1);
                    sailboat.getSail().rotate(simulation.getCurrentState().getPosition().getValue(3));
                }  if (pressedKeys.contains(KeyCode.LEFT)) {
                    simulation.rotateBoat(-1);
                    sailboat.rotate(simulation.getCurrentState().getPosition().getValue(2));
                }  if (pressedKeys.contains(KeyCode.RIGHT)) {
                    simulation.rotateBoat(1);
                    sailboat.rotate(simulation.getCurrentState().getPosition().getValue(2));
                }  if (pressedKeys.contains(KeyCode.SPACE)) {
                    running[0] = !running[0]; // pause
                    simulation.getCurrentState().log();
                }
            }
            sailboat.repaintSail(simulation.getCurrentState());
            arrows.update(simulation.getCurrentState());
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

    public SailboatGUI getSailboat() {
        return sailboat;
    }


    void makeDraggable(Node node, boolean x, boolean y, double[] xBounds, double[] yBounds) {
        if (x && y) {

            node.setOnMousePressed(e -> {
                startY = e.getSceneY() - node.getTranslateY();
                startX = e.getSceneX() - node.getTranslateX();
            });
            node.setOnMouseDragged(e -> {
                node.setTranslateY(e.getSceneY() - startY);
                node.setTranslateX(e.getSceneX() - startX);
                if (xBounds != null) {
                    if (node.getTranslateX() > xBounds[1]) node.setTranslateX(xBounds[1]);
                    if (node.getTranslateX() < xBounds[0]) node.setTranslateX(xBounds[0]);
                }
                if (yBounds != null) {
                    if (node.getTranslateY() > yBounds[1]) node.setTranslateY(yBounds[1]);
                    if (node.getTranslateY() < yBounds[0]) node.setTranslateY(yBounds[0]);
                }
            });
        } else if (x) {
            node.setOnMousePressed(e -> {
                    startX = e.getSceneX() - node.getTranslateX();
            });

            node.setOnMouseDragged(e -> {
                node.setTranslateX(e.getSceneX() - startX);
                    if (xBounds != null) {
                        if (node.getTranslateX() > xBounds[1]) node.setTranslateX(xBounds[1]);
                        if (node.getTranslateX() < xBounds[0]) node.setTranslateX(xBounds[0]);
                    }
            });

        } else if (y) {
            node.setOnMousePressed(e -> {
                startY = e.getSceneY() - node.getTranslateY();
            });

            node.setOnMouseDragged(e -> {
                node.setTranslateY(e.getSceneY() - startY);
                if (yBounds != null) {
                    if (node.getTranslateY() > yBounds[1]) node.setTranslateY(yBounds[1]);
                    if (node.getTranslateY() < yBounds[0]) node.setTranslateY(yBounds[0]);
                }
            });

        }

    }
}
