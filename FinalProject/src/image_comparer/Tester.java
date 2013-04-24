package image_comparer;

/*
 * Tests all the codes!!
 */
public class Tester
{
	public static void main(String[] args) throws MyImage.MyImageException
	{
		System.out.println("TESTING A 10x5 RED AND BLACK PNG IMAGE");
		MyImage im = new MyImage("images/red_black.png");
		PixelArray p = im.toPixelArray();
		printInfo(im, p);
		
		System.out.println("RESIZING THE IMAGE TO 4x2");
		im.setSize(4, 2);
		p = im.toPixelArray();
		printInfo(im, p);
		
		System.out.println("TESTING AN 8x10 GRADIENT JPEG IMAGE");
		MyImage im2 = new MyImage("images/gradient.jpg");
		PixelArray p2 = im2.toPixelArray();
		printInfo(im2, p2);
		
		System.out.println("ROUNDING THE PIXEL ARRAY");
		p2 = p2.round(32);
		printInfo(im2, p2);
		
		System.out.println("MYIMAGE.COMPARE: SAME SIZE IMAGES");
		System.out.printf("\t%d%n", MyImage.compare(im, im));
		
		System.out.println("MYIMAGE.COMPARE: IMAGE1 < IMAGE2");
		System.out.printf("\t%d%n", MyImage.compare(im, im2));
		
		System.out.println("MYIMAGE.COMPARE: IMAGE1 > IMAGE2");
		System.out.printf("\t%d%n", MyImage.compare(im2, im));
		
		KeypointComparer k = new KeypointComparer();
		HistogramComparer h = new HistogramComparer();
		SetComparer s = new SetComparer();
		PHashComparer pH = new PHashComparer();
		
		MyImage image1 = new MyImage("images/cow1.gif");
		MyImage image2 = new MyImage("images/cow2.gif");
		MyImage image3 = new MyImage("images/cow3.bmp");
		image1.setSize(32, 32);
		image2.setSize(32, 32);
		image3.setSize(32, 32);
		PixelArray cow1 = image1.toPixelArray();
		PixelArray cow2 = image2.toPixelArray();
		PixelArray cow3 = image3.toPixelArray();
		
		System.out.println("SAME GIF IMAGE");
		System.out.printf("\tKeypoint matching: %f%n",
				doComparison(k, cow1, cow1));
		System.out.printf("\tHistogram comparison: %f%n",
				doComparison(h, cow1, cow1));
		System.out.printf("\tSet resemblance: %f%n",
				doComparison(s, cow1, cow1));
		System.out.printf("\tPerceptual hash: %f%n",
				doComparison(pH, cow1, cow1));
		
		System.out.println("SAME IMAGE, GIF AND BMP");
		System.out.printf("\tKeypoint matching: %f%n",
				doComparison(k, cow2, cow3));
		System.out.printf("\tHistogram comparison: %f%n",
				doComparison(h, cow2, cow3));
		System.out.printf("\tSet resemblance: %f%n",
				doComparison(s, cow2, cow3));
		System.out.printf("\tPerceptual hash: %f%n",
				doComparison(pH, cow2, cow3));
		
		System.out.println("COMPLETELY DIFFERENT IMAGES");
		im2.setSize(4, 2);
		p2 = im2.toPixelArray();
		System.out.printf("\tKeypoint matching: %f%n", doComparison(k, p, p2));
		System.out.printf("\tHistogram comparison: %f%n",
				doComparison(h, p, p2));
		System.out.printf("\tSet resemblance: %f%n", doComparison(s, p, p2));
		System.out.printf("\tPerceptual hash: %f%n", doComparison(pH, p, p2));
		
		System.out.println("SIMILAR IMAGES");
		System.out.printf("\tKeypoint matching: %f%n",
				doComparison(k, cow1, cow2));
		System.out.printf("\tHistogram comparison: %f%n",
				doComparison(h, cow1, cow2));
		System.out.printf("\tSet resemblance: %f%n",
				doComparison(s, cow1, cow2));
		System.out.printf("\tPerceptual hash: %f%n",
				doComparison(pH, cow1, cow2));
	}
	
	private static void printInfo(MyImage im, PixelArray p)
	{
		System.out.printf("Width: %d %d%n", im.getWidth(), p.getWidth());
		System.out.printf("Height: %d %d%n", im.getHeight(), p.getHeight());
		for (int y = 0, n = p.getHeight(); y < n; ++y)
		{
			for (int x = 0, m = p.getWidth(); x < m; ++x)
			{
				int pixel = p.getPixel(x, y);
				int red = PixelArray.getRed(pixel);
				int green = PixelArray.getGreen(pixel);
				int blue = PixelArray.getBlue(pixel);
				System.out.printf("\t%3d %3d %3d", red, green, blue);
			}
			System.out.print("\n");
		}
	}
	
	private static double doComparison(PixelArrayComparer c, PixelArray p1,
			PixelArray p2)
	{
		return c.compare(p1, p2);
	}
}