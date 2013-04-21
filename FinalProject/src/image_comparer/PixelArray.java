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
	
	public PixelArray round()
	{
		return new PixelArray(pixels);
	}
}