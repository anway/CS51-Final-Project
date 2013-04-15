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
	
	//TODO
	public void setSize(double scale)
	{
		return;
	}
	
	//TODO
	public PixelArray toPixelArray()
	{
		return new PixelArray(null);
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