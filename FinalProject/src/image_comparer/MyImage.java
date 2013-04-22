package image_comparer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/*
 * Handles images.
 */
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
			// Throws an exception if not passed a valid image name
			throw new MyImageException(file);
		}
	}
	
	/* Gets the width */
	public int getWidth()
	{
		return im.getWidth();
	}
	
	/* Gets the height */
	public int getHeight()
	{
		return im.getHeight();
	}
	
	/* Scales image */
	public void setSize(int newx, int newy)
	{	
		BufferedImage imScaled = new BufferedImage(newx, newy,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = imScaled.createGraphics();
		g2.drawImage(imScaled, null, null);
		im = imScaled;
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
	
	/*
	 * The exception to be thrown if passed a bad image name
	 */
	class MyImageException extends IOException
	{
		private static final long serialVersionUID = 1L;
		
		public MyImageException(String file)
		{
			super(file + " is not an image file in a supported format");
		}
	}
}
