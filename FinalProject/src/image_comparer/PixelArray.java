package image_comparer;

/*
 * Handles pixel arrays.
 */
public class PixelArray
{
	private int[][] pixels;
	
	public PixelArray(int[][] pixels)
	{
		this.pixels = pixels;
	}
	
	/* Gets the width */
	public int getWidth()
	{
        return pixels[0].length;
	}
	
	/* Gets the height */
	public int getHeight()
	{
		return pixels.length;
	}
	
	/* Gets a pixel */
	public int getPixel(int x, int y)
	{
		return pixels[y][x];
	}
    
	public static int getAlpha(int pixel)
	{
		return (pixel >> 24) & 0x000000FF;
	}
	
	/* Gets the amount of red in a pixel */
	public static int getRed(int pixel)
	{
		return (pixel >> 16) & 0x000000FF;
	}
	
	/* Gets the amount of green in a pixel */
	public static int getGreen(int pixel)
	{
		return (pixel >> 8) & 0x000000FF;
	}
	
	/* Gets the amount of blue in a pixel */
	public static int getBlue(int pixel)
	{
		return pixel & 0x000000FF;
	}
	
	/*
	 * Rounds the colors in a pixel array.
	 * Takes in a number of discrete colors to round to.
	 * Returns a rounded pixel array.
	 */
	public PixelArray round(int colors)
	{
		int colorUnit = 256/colors;
		
		int w = getWidth();
		int h = getHeight();
		int[][] roundedArray = new int[h][w];
		for (int i = 0; i < w; ++i)
			for (int j = 0; j < h; ++j)
			{
				int pixel = getPixel(i, j);
				int redBelow = (getRed(pixel)/colorUnit) * colorUnit;
				int greenBelow = (getGreen(pixel)/colorUnit) * colorUnit;
				int blueBelow = (getBlue(pixel)/colorUnit) * colorUnit;
				
				double minDistance = Double.MAX_VALUE;
				for (int k = 0; k < 8; ++k)
				{
					int red = redBelow + ((k >> 2) & 0x00000001) * colorUnit;
					int green = greenBelow +
							((k >> 1) & 0x00000001) * colorUnit;
					int blue = blueBelow + (k & 0x00000001) * colorUnit;
					int roundedPixel = (red << 16) + (green << 8) + blue;
					double distance = getDistance(pixel, roundedPixel);
					if (distance < minDistance)
					{
						minDistance = distance;
						roundedArray[j][i] = (PixelArray.getAlpha(pixel) << 24)
							+ roundedPixel;
					}
				}
			}
		return new PixelArray(roundedArray);
	}
    

	
	// Helper method to get the distance between two pixels
	public static double getDistance(int p1, int p2)
	{
		double r1 = (double) getRed(p1);
		double r2 = (double) getRed(p2);
		
		double weight = (r1 + r2) / 512. + 2;
		return Math.sqrt(weight * Math.pow(r1-r2, 2) + 4*Math.pow((double)
				getGreen(p1) - (double) getGreen(p2), 2) + weight *
				Math.pow((double) getBlue(p1) - (double) getBlue(p2), 2));
	}
}