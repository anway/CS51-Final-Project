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
	
	//TODO
	public PixelArray round()
	{
		return new PixelArray(pixels);
	}
}
