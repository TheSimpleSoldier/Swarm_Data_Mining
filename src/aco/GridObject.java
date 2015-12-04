package aco;

/**
 * Created by joshua on 11/29/15.
 * This class is inherited by and and data point and contains
 * the code for movement
 */
public class GridObject
{
    private int x;
    private int y;
    private int maxX;
    private int maxY;

    public GridObject(int x, int y, int maxX, int maxY)
    {
        this.x = x;
        this.y = y;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public int[] getLocation()
    {
        return new int[]{x, y};
    }

    public void setLocation(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    //moves, making sure not to go out of bounds, x and y are how far to move
    public void move(int x, int y)
    {
        this.x += x;
        this.y += y;

        if(this.x < 0)
        {
            this.x = 0;
        }
        if(this.y < 0)
        {
            this.y = 0;
        }
        if(this.x > maxX)
        {
            this.x = maxX;
        }
        if(this.y > maxY)
        {
            this.y = maxY;
        }
    }
}
