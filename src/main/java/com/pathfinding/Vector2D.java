package com.pathfinding;

/*
 * Math helper class to construct a 2D Vector
 */
public final class Vector2D
{
    private final int x;
    private final int y;

    public Vector2D()
    {
        this.x = 0;
        this.y = 0;
    }
    public Vector2D(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
