package aco;

import java.util.Random;

/**
 * Created by joshua on 11/29/15.
 */
public class Ant extends GridObject
{
    private DataPoint holding;
    private boolean isHolding;
    private double gamma;
    private Random rand;

    public Ant(int x, int y, int maxX, int maxY, double gamma)
    {
        super(x, y, maxX, maxY);
        holding = null;
        isHolding = false;
        this.gamma = gamma;
        rand = new Random();
    }

    public boolean shouldPickUp(DataPoint point, DataPoint[] neighborhood)
    {
        if(!isHolding)
        {
            double lamda = computeLamda(point, neighborhood);
            double pickUp = Math.pow(gamma / (gamma + lamda), 2);
            if(rand.nextDouble() <= pickUp)
            {
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean shouldDrop(DataPoint[] neighborhood)
    {
        if(isHolding)
        {
            double lamda = computeLamda(holding, neighborhood);
            double drop = lamda * 2;
            if(lamda < gamma)
            {
                if(rand.nextDouble() <= drop)
                {
                    return true;
                }
                return false;
            }
            return true;
        }
        return false;
    }

    private double computeLamda(DataPoint point, DataPoint[] neighborhood)
    {
        double sum = 0;
        for(int k = 0; k < neighborhood.length; k++)
        {
            sum += (1 - (point.distance(neighborhood[k]) / gamma));
        }
        if(neighborhood.length == 0)
        {
            return 0.01;
        }
        return Math.max(0, (1 / Math.pow(neighborhood.length, 2)) * sum);
    }

    public boolean pickUp(DataPoint point)
    {
        if(!isHolding)
        {
            holding = point;
            isHolding = true;
            return true;
        }

        return false;
    }

    public DataPoint drop()
    {
        if(isHolding)
        {
            isHolding = false;
            return holding;
        }
        return null;
    }

    public boolean isHolding()
    {
        return isHolding;
    }
}
