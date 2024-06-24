package com.pathfinding;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import java.util.Comparator;
import java.util.PriorityQueue;

/*
 *  Class contains logic to find the nearest path to the goal position
 *  This class only shows 2D traversal, but you can use it as a template for traversing in any dimensions
 */
public final class AStar
{
    // Traverse area size
    private final Vector2D areaSize;

    private final Node[][] nodes;
    private Node initialNode;
    private Node finalNode;

    public AStar(int areaSizeX, int areaSizeY, Node initialNode, Node finalNode)
    {
        areaSize = new Vector2D(areaSizeX, areaSizeY);
        this.initialNode = initialNode;
        this.finalNode = finalNode;

        nodes = new Node[areaSizeX][areaSizeY];

        init();
    }

    private void init()
    {
        // Create 2D array of nodes and initialize the positions
        for (int x = 0; x < areaSize.getX(); x++)
        {
            for (int y = 0; y < areaSize.getY(); y++)
            {
                nodes[x][y] = new Node(x, y);
            }
        }

        // Set neighbours for each node
        for (int x = 0; x < areaSize.getX(); x++)
        {
            for (int y = 0; y < areaSize.getY(); y++)
            {
                Node node = nodes[x][y];
                // Check boundaries and add neighbours

                if (y > 0) { node.neighbours.add(nodes[x][y-1]); }
                if (y < areaSize.getY() - 1) { node.neighbours.add(nodes[x][y+1]); }
                if (x > 0) { node.neighbours.add(nodes[x-1][y]); }
                if (x < areaSize.getX() - 1) { node.neighbours.add(nodes[x+1][y]); }

                // Check diagonally
                if (y > 0 && x > 0) { node.neighbours.add(nodes[x-1][y-1]); }
                if (y < areaSize.getY() - 1 && x > 0) { node.neighbours.add(nodes[x-1][y+1]); }
                if (y > 0 && x < areaSize.getX() - 1) { node.neighbours.add(nodes[x+1][y-1]); }
                if (y < areaSize.getY() - 1 && x < areaSize.getX() - 1) { node.neighbours.add(nodes[x+1][y+1]); }
            }
        }

        // Set initial and final node
        initialNode = nodes[initialNode.position.getX()][initialNode.position.getY()];
        finalNode = nodes[finalNode.position.getX()][finalNode.position.getY()];
    }

    public void findPath()
    {
        if (initialNode == null || finalNode == null) {
            return;
        }

        // Reset nodes
        for (int x = 0; x < areaSize.getX(); x++)
        {
            for (int y = 0; y < areaSize.getY(); y++)
            {
                Node node = nodes[x][y];
                node.visited = false;
                node.g = Double.MAX_VALUE;
                node.f = Double.MAX_VALUE;
                node.parent = null;
            }
        }

        // Starting condition
        initialNode.g = 0.0;
        initialNode.f = initialNode.calculateHeuristic(finalNode);

        // Creating a list of nodes in the open queue, which we will compare
        PriorityQueue<Node> openNodeList = new PriorityQueue<>(
                Comparator.comparingDouble(node -> node.f)
        );
        openNodeList.add(initialNode);

        while (!openNodeList.isEmpty())
        {
            Node currentNode = openNodeList.poll();

            // Path found, stop searching
            if (currentNode.equals(finalNode)) {
                break;
            }

            currentNode.visited = true;

            // Compare neighbours
            for (Node nodeNeighbours : currentNode.neighbours)
            {
                if (nodeNeighbours.visited || nodeNeighbours.blocking) {
                    continue;
                }

                // Calculate current node g(n)
                double tempG = currentNode.g + currentNode.calculateHeuristic(nodeNeighbours);

                if (tempG < nodeNeighbours.g)
                {
                    nodeNeighbours.parent = currentNode;
                    nodeNeighbours.g = tempG;
                    nodeNeighbours.f = nodeNeighbours.g + nodeNeighbours.calculateHeuristic(finalNode);

                    if (!openNodeList.contains(nodeNeighbours)) {
                        openNodeList.add(nodeNeighbours);
                    }
                }
            }
        }
    }

    public void render(Canvas canvas)
    {
        Vector2D nodeSize = new Vector2D(12, 12);

        // Calculate starting position centered
        final double startX = (canvas.getWidth() - areaSize.getX() * (nodeSize.getX() + 2) - 2) / 2;
        final double startY = (canvas.getHeight() - areaSize.getY() * (nodeSize.getY() + 2) - 2) / 2;

        // Handle on mouse clicked event
        canvas.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY)
            {
                final int nSelectedNodeX = (int) ((mouseEvent.getX() - startX) / (nodeSize.getX() + 2));
                final int nSelectedNodeY = (int) ((mouseEvent.getY() - startY) / (nodeSize.getY() + 2));

                if (nSelectedNodeX >= 0 && nSelectedNodeX < areaSize.getX())
                {
                    if (nSelectedNodeY >= 0 && nSelectedNodeY < areaSize.getY())
                    {
                        nodes[nSelectedNodeX][nSelectedNodeY].blocking = !nodes[nSelectedNodeX][nSelectedNodeY].blocking;

                        findPath();

                        redrawCanvas(canvas, startX, startY, nodeSize);
                    }
                }
            }
        });

        redrawCanvas(canvas, startX, startY, nodeSize);
    }

    private void redrawCanvas(Canvas canvas, double startX, double startY, Vector2D nodeSize)
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw nodes
        for (int x = 0; x < areaSize.getX(); x++)
        {
            for (int y = 0; y < areaSize.getY(); y++)
            {
                Node node = nodes[x][y];

                // Determine node color
                if (node.equals(initialNode)) {
                    gc.setFill(Color.GREEN);
                } else if (node.equals(finalNode)) {
                    gc.setFill(Color.RED);
                } else if (node.visited && !node.blocking) {
                    gc.setFill(Color.MEDIUMTURQUOISE);
                } else if (node.blocking) {
                    gc.setFill(Color.BLACK);
                } else {
                    gc.setFill(Color.GREY); // avail node color
                }

                gc.fillRect(calculatePosition(startX, x, nodeSize.getX()), calculatePosition(startY, y, nodeSize.getY()), nodeSize.getX(), nodeSize.getY());
            }
        }

        // Draw path by starting from final node
        if (finalNode != null) {
            Node p = finalNode;

            gc.setStroke(Color.MAGENTA);
            gc.setLineWidth(5.0);

            while (p.parent != null)
            {
                // Calculate current node center position
                double currentX = calculatePosition(startX, p.position.getX(), nodeSize.getX()) + nodeSize.getX() / 2.0;
                double currentY = calculatePosition(startY, p.position.getY(), nodeSize.getY()) + nodeSize.getY() / 2.0;

                // Calculate parent node center position
                double parentX = calculatePosition(startX, p.parent.position.getX(), nodeSize.getX()) + nodeSize.getX() / 2.0;
                double parentY = calculatePosition(startY, p.parent.position.getY(), nodeSize.getY()) + nodeSize.getY() / 2.0;

                // Draw the line from the current node to its parent
                gc.strokeLine(currentX, currentY, parentX, parentY);

                // Move to the parent node
                p = p.parent;
            }
        }
    }

    // Helper function to calculate node position
    private double calculatePosition(double startPos, int nodePos, int nodeSize)
    {
        return startPos + nodePos * (nodeSize + /*spacing*/2);
    }
}
