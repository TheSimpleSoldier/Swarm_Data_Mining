package aco;

/**
 * Created by joshua on 11/29/15.
 */
public class DataPoint extends GridObject
{
    private double[] data;

    public DataPoint(int x, int y, int maxX, int maxY, double[] data)
    {
        super(x, y, maxX, maxY);
        this.data = data;
    }

    public double distance(DataPoint point)
    {
        double[] data2 = point.getData();
        double sum = 0;
        for(int k = 0; k < data.length; k++)
        {
            sum += Math.abs(data[k] - data2[k]);
        }

        return sum;
    }

    public double[] getData()
    {
        return data;
    }
}
