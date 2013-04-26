package image_comparer;

import java.awt.Graphics2D;
import java.awt.Image;
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

	public MyImage(File file) throws MyImageException
	{
		try
		{
			im = ImageIO.read(file);
		} catch (IOException e)
		{
			throw new MyImageException(file.getName());
		}
	}
	
	public MyImage(String fileName) throws MyImageException
	{
		this(new File(fileName));
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
		Image scaledImage = im.getScaledInstance(newx, newy,
				Image.SCALE_DEFAULT);
		BufferedImage imScaled = new BufferedImage(newx, newy,
				im.getType());
		Graphics2D g2 = imScaled.createGraphics();
		g2.drawImage(scaledImage, null, null);
		im = imScaled;
	}

	/* Returns image as PixelArray */
	public PixelArray toPixelArray()
	{	
		int w = im.getWidth();
		int h = im.getHeight();
		int [][] result = new int[h][w];

		for (int row = 0; row < h; ++row) {
			for (int col = 0; col < w; ++col)
				result[row][col] = im.getRGB(col, row);
		}
		return new PixelArray(result);
	}

	public static int compare(MyImage im1, MyImage im2)
	{
		int size1 = (im1.getWidth() + im1.getHeight()) / 2;
		int size2 = (im2.getWidth() + im2.getHeight()) / 2;
		return size1 < size2 ? -1 : size1 == size2 ? 0 : 1;
	}

	/*
	 * The exception to be thrown if passed a bad image name
	 */
	class MyImageException extends IOException
	{
		private static final long serialVersionUID = 1L;

		MyImageException(String file)
		{
			super(file + " is not an image file in a supported format");
		}
	}
}
