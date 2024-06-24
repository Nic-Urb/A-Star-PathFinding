package com.pathfinding;

import java.util.ArrayList;

/*
 * Instance class for each node, containing information about a node
 */
public final class Node
{
    public boolean blocking = false;   // flag a blocking node
    public boolean visited = false;    // flag a visited node
    public double g;     // distance from starting node (cost)
    public double f;     // final cost

    public Vector2D position;
    public ArrayList<Node> neighbours;
    public Node parent = null;

    public Node(int x, int y)
    {
        position = new Vector2D(x, y);
        this.neighbours = new ArrayList<>();
    }

    public double calculateHeuristic(Node target)
    {
        // on square grid that allows 4 directions of movement we would use Manhattan distance instead
        // calculate Euclidean distance to get heuristic
        return Math.sqrt(Math.pow(position.getX() - target.position.getX(), 2) + Math.pow(position.getY() - target.position.getY(), 2));
    }
}
