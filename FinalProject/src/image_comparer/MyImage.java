package image_comparer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MyImage
{
	private BufferedImage im;
	
	public MyImage(String file) throws MyImageException
	{
		try
		{
			im = ImageIO.read(new File(file));
		} catch (IOException e)
		{
			throw new MyImageException(file);
		}
	}
	
	public int getWidth()
	{
		return im.getWidth();
	}
	
	public int getHeight()
	{
		return im.getHeight();
	}
	
	public double getAspectRatio()
	{
		return ((double) getWidth())/((double) getHeight());
	}
	
	/* Scales image */
	public void setSize(int newx, int newy)
	{	
		im = (BufferedImage) im.getScaledInstance(newx, newy, BufferedImage.SCALE_DEFAULT);		
	}
	
	/* Returns image as PixelArray */
	public PixelArray toPixelArray()
	{	
		int w = im.getWidth();
		int h = im.getHeight();
		int [][] result = new int[h][w];
		
		for (int row = 0; row < h; row++) {
		  for (int col = 0; col < w; col++) 
			result [row][col] = im.getRGB(col, row);
		}
		return new PixelArray(result);
	}
	
	class MyImageException extends IOException
	{
		private static final long serialVersionUID = 1L;
		
		public MyImageException(String file)
		{
			super(file + " is not an image file in a supported format");
		}
	}
}
