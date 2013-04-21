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
		return pixels.length;
	}
	
	public int getHeight()
	{
		return pixels[0].length;
	}
	
	public int getPixel(int x, int y)
	{
		return pixels[x][y];
	}
	
	public int getRed(int pixel)
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
		int w = getWidth();
		int h = getHeight();
		int[][] roundedArray = new int[w][h];
		for (int i = 0; i < w; i++)
			for (int j = 0; j < w; j++)
			{
				
			}
	}
	
	private double getDistance(int p1, int p2)
	{
		double r1 = (double) getRed(p1);
		double r2 = (double) getRed(p2);
		
		double rMean = (r1 + r2) / 2.;
		double weight = (r1 + r2) / 512. + 2;
		return Math.sqrt(weight * Math.pow(r1-r2, 2) + 4*Math.pow((double) getGreen(p1) - (double) getGreen(p2), 2) + weight * Math.pow((double) getBlue(p1) - (double) getBlue(p2), 2));
	}
}