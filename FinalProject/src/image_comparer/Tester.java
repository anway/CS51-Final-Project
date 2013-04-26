package image_comparer;

/*
 * Tests all the codes!!
 */
public class Tester
{
	public static void main(String[] args) throws MyImage.MyImageException
	{
		System.out.println("TESTING A 10x5 RED AND BLACK IMAGE");
		MyImage im = new MyImage("images/red_black.gif");
		PixelArray p = im.toPixelArray();
		printInfo(im, p);
		
		System.out.println("RESIZING THE IMAGE TO 4x2");
		im.setSize(4, 2);
		p = im.toPixelArray();
		printInfo(im, p);
		
		System.out.println("TESTING AN 8x10 GRADIENT IMAGE");
		MyImage im2 = new MyImage("images/gradient.gif");
		PixelArray p2 = im2.toPixelArray();
		printInfo(im2, p2);
		
		System.out.println("ROUNDING THE PIXEL ARRAY");
		printInfo(im2, p2.round(32));
		
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
		MyImage image3 = new MyImage("images/cow3.gif");
		MyImage image4 = new MyImage("images/kenshin1.gif");
		MyImage image5 = new MyImage("images/kenshin2.gif");
		MyImage image6 = new MyImage("images/kenshin3.gif");
		MyImage image7 = new MyImage("images/kenshin4.gif");
		image1.setSize(32, 32);
		image2.setSize(32, 32);
		image3.setSize(32, 32);
		image4.setSize(32, 32);
		image5.setSize(32, 32);
		image6.setSize(32, 32);
		image7.setSize(32, 32);
		PixelArray cow1 = image1.toPixelArray();
		PixelArray cow2 = image2.toPixelArray();
		PixelArray cow3 = image3.toPixelArray();
		PixelArray kenshin1 = image4.toPixelArray();
		PixelArray kenshin2 = image5.toPixelArray();
		PixelArray kenshin3 = image6.toPixelArray();
		PixelArray kenshin4 = image7.toPixelArray();
		
		System.out.println("SAME 32x32 IMAGE");
		System.out.printf("\tKeypoint matching: %f%n",
			doComparison(k, cow1, cow1));
		System.out.printf("\tHistogram comparison: %f%n",
			doComparison(h, cow1, cow1));
		System.out.printf("\tSet resemblance: %f%n",
			doComparison(s, cow1, cow1));
		System.out.printf("\tPerceptual hash: %f%n",
			doComparison(pH, cow1, cow1));
		
		System.out.println("COMPLETELY DIFFERENT IMAGES, ENLARGED TO 32x32");
		im = new MyImage("images/red_black.gif");
		im.setSize(32, 32);
		p = im.toPixelArray();
		im2 = new MyImage("images/gradient.gif");
		im2.setSize(32, 32);
		p2 = im2.toPixelArray();
		System.out.printf("\tKeypoint matching: %f%n", doComparison(k, p, p2));
		System.out.printf("\tHistogram comparison: %f%n",
			doComparison(h, p, p2));
		System.out.printf("\tSet resemblance: %f%n", doComparison(s, p, p2));
		System.out.printf("\tPerceptual hash: %f%n", doComparison(pH, p, p2));
		
		System.out.println("COMPLETELY DIFFERENT IMAGES, SHRUNK TO 32x32");
		System.out.printf("\tKeypoint matching: %f%n",
			doComparison(k, cow2, kenshin1));
		System.out.printf("\tHistogram comparison: %f%n",
			doComparison(h, cow2, kenshin1));
		System.out.printf("\tSet resemblance: %f%n",
			doComparison(s, cow2, kenshin1));
		System.out.printf("\tPerceptual hash: %f%n",
			doComparison(pH, cow2, kenshin1));
		
		System.out.println("SOMEWHAT SIMILAR 32x32 IMAGES");
		System.out.printf("\tKeypoint matching: %f%n",
			doComparison(k, cow1, cow2));
		System.out.printf("\tHistogram comparison: %f%n",
			doComparison(h, cow1, cow2));
		System.out.printf("\tSet resemblance: %f%n",
			doComparison(s, cow1, cow2));
		System.out.printf("\tPerceptual hash: %f%n",
			doComparison(pH, cow1, cow2));
		
		System.out.println("EXTREMELY SIMILAR 32x32 IMAGES");
		System.out.printf("\tKeypoint matching: %f%n",
			doComparison(k, cow2, cow3));
		System.out.printf("\tHistogram comparison: %f%n",
			doComparison(h, cow2, cow3));
		System.out.printf("\tSet resemblance: %f%n",
			doComparison(s, cow2, cow3));
		System.out.printf("\tPerceptual hash: %f%n",
			doComparison(pH, cow2, cow3));
		
		System.out.println("CROPPED 32x32 IMAGE");
		System.out.printf("\tKeypoint matching: %f%n",
			doComparison(k, kenshin1, kenshin2));
		System.out.printf("\tHistogram comparison: %f%n",
			doComparison(h, kenshin1, kenshin2));
		System.out.printf("\tSet resemblance: %f%n",
			doComparison(s, kenshin1, kenshin2));
		System.out.printf("\tPerceptual hash: %f%n",
			doComparison(pH, kenshin1, kenshin2));
		
		System.out.println("ROTATED 32x32 IMAGE");
		System.out.printf("\tKeypoint matching: %f%n",
			doComparison(k, kenshin1, kenshin3));
		System.out.printf("\tHistogram comparison: %f%n",
			doComparison(h, kenshin1, kenshin3));
		System.out.printf("\tSet resemblance: %f%n",
			doComparison(s, kenshin1, kenshin3));
		System.out.printf("\tPerceptual hash: %f%n",
			doComparison(pH, kenshin1, kenshin3));
		
		System.out.println("REFLECTED 32x32 IMAGE");
		System.out.printf("\tKeypoint matching: %f%n",
			doComparison(k, kenshin1, kenshin4));
		System.out.printf("\tHistogram comparison: %f%n",
			doComparison(h, kenshin1, kenshin4));
		System.out.printf("\tSet resemblance: %f%n",
			doComparison(s, kenshin1, kenshin4));
		System.out.printf("\tPerceptual hash: %f%n",
			doComparison(pH, kenshin1, kenshin4));
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
				int alpha = PixelArray.getAlpha(pixel);
				int red = PixelArray.getRed(pixel);
				int green = PixelArray.getGreen(pixel);
				int blue = PixelArray.getBlue(pixel);
				System.out.printf("\t\t%3d %3d %3d %3d",
					alpha, red, green, blue);
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