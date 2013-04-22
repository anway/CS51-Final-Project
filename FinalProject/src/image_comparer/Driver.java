package image_comparer;

import java.io.IOException;

/*
 * Takes in the names of two images as command-line arguments and outputs their
 * similarity based on four comparison algorithms.
 */
public class Driver
{
	public static void main(String[] args) throws IOException
	{
		MyImage im1 = new MyImage(args[0]);
		MyImage im2 = new MyImage(args[1]);

		int width, height;
		if (MyImage.compare(im1, im2) < 0)
		{
			width = im1.getWidth();
			height = im1.getHeight();
		}
		else
		{
			width = im2.getWidth();
			height = im2.getHeight();
		}
		im1.setSize(width, height);
		im2.setSize(width, height);
		PixelArray a1 = im1.toPixelArray();
		PixelArray a2 = im2.toPixelArray();

		KeypointComparer k = new KeypointComparer();
		SetComparer s = new SetComparer();
		HistogramComparer h = new HistogramComparer();
		PHashComparer p = new PHashComparer();

		System.out.printf("Keypoint matching: %f%n", k.compare(a1, a2));
		System.out.printf("Set resemblance check: %f%n", s.compare(a1, a2));

		System.out.printf("Histogram comparison: %f%n", h.compare(a1, a2));
		System.out.printf("Perceptual hash: %f%n", p.compare(a1, a2));
	}
}