package com.pathfinding;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ApplicationEntry extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage)
    {
        stage.setTitle("[Path Finding Application]");

        StackPane layout = new StackPane();

        // Initialize scene
        Scene scene = new Scene(layout, 800, 450);

        // Create a Canvas
        Canvas canvas = new Canvas(800, 450);
        layout.getChildren().add(canvas);

        // Setup pathfinding
        Node initialNode = new Node(0, 0);
        Node finalNode = new Node(31, 31);
        AStar pathFinding = new AStar(32, 32, initialNode, finalNode);
        pathFinding.render(canvas);

        scene.setOnKeyPressed(keyEvent -> handeKeyInputPressed(keyEvent, stage));

        stage.setScene(scene);
        stage.show();
    }

    // Handle key input callback
    private void handeKeyInputPressed(KeyEvent event, Stage stage)
    {
        if (event.getCode() == KeyCode.ESCAPE)
        {
            stage.close();
        }
    }
}