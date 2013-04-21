package image_comparer;

public class PixelArray
{
	private int[][] pixels;
	
	public PixelArray(int[][] pixels)
	{
		this.pixels = pixels;
	}
	
	public int getWidth()
	{
        return pixels[0].length;
	}
	
	public int getHeight()
	{
		return pixels.length;
	}
	
	public int getPixel(int x, int y)
	{
		return pixels[y][x];
	}
	
<<<<<<< HEAD
	public PixelArray round()
=======
	public int getRed(int pixel)
>>>>>>> 886590553121ee38b709217672347145e92a60af
	{
		return (pixel >> 16) & 0x000000FF;
	}
	
	public int getGreen(int pixel)
	{
		return (pixel >> 8) & 0x000000FF;
	}
	
	public int getBlue(int pixel)
	{
		return pixel & 0x000000FF;
	}
	
	public PixelArray round(int colors)
	{
		int colorUnit = 256/colors;
		
		int w = getWidth();
		int h = getHeight();
		int[][] roundedArray = new int[w][h];
		for (int i = 0; i < w; i++)
			for (int j = 0; j < w; j++)
			{
				int pixel = getPixel(i, j);
				int redBelow = (getRed(pixel)/colorUnit) * colorUnit;
				int greenBelow = (getGreen(pixel)/colorUnit) * colorUnit;
				int blueBelow = (getBlue(pixel)/colorUnit) * colorUnit;
				
				double minDistance = Double.MAX_VALUE;
				for (int k = 0; k < 8; k++)
				{
					int red = redBelow + ((k >> 4) & 0x00000002);
					int green = greenBelow + ((k >> 2) & 0x00000002);
					int blue = blueBelow + (k & 0x00000002);
					int roundedPixel = (red << 4) + (green << 2) + blue;
					double distance = getDistance(pixel, roundedPixel);
					if (distance < minDistance)
					{
						minDistance = distance;
						roundedArray[i][j] = roundedPixel;
					}
				}
			}
		return new PixelArray(roundedArray);
	}
	
	private double getDistance(int p1, int p2)
	{
		double r1 = (double) getRed(p1);
		double r2 = (double) getRed(p2);
		
		double weight = (r1 + r2) / 512. + 2;
		return Math.sqrt(weight * Math.pow(r1-r2, 2) + 4*Math.pow((double) getGreen(p1) - (double) getGreen(p2), 2) + weight * Math.pow((double) getBlue(p1) - (double) getBlue(p2), 2));
	}
}