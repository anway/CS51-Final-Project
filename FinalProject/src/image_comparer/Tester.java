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
		MyImage cow1 = new MyImage("images/cow1.gif");
		MyImage cow2 = new MyImage("images/cow2.gif");
		MyImage cow3 = new MyImage("images/cow3.bmp");
		
		System.out.println("RANDOM KEYPOINT COMPARISON: SAME GIF IMAGE");
		System.out.printf("\t%f%n", k.compare(cow1.toPixelArray(),
				cow1.toPixelArray()));
		
		System.out.println("RANDOM KEYPOINT COMPARISON: SAME IMAGE, " +
				"GIF AND BMP");
		System.out.printf("\t%f%n", k.compare(cow2.toPixelArray(),
				cow3.toPixelArray()));
		
		System.out.println("RANDOM KEYPOINT COMPARISON: " +
				"COMPLETELY DIFFERENT IMAGES");
		im2.setSize(4, 2);
		p2 = im2.toPixelArray();
		System.out.printf("\t%f%n", k.compare(p, p2));
		
		System.out.println("RANDOM KEYPOINT COMPARISON: SIMILAR IMAGES");
		System.out.printf("\t%f%n", k.compare(cow1.toPixelArray(),
				cow2.toPixelArray()));
	}
	
	private static void printInfo(MyImage im, PixelArray p)
	{
		System.out.printf("Width: %d %d%n", im.getWidth(), p.getWidth());
		System.out.printf("Height: %d %d%n", im.getHeight(), p.getHeight());
		for (int y = 0, n = p.getHeight(); y < n; y++)
		{
			for (int x = 0, m = p.getWidth(); x < m; x++)
			{
				int pixel = p.getPixel(x, y);
				int red = p.getRed(pixel);
				int green = p.getGreen(pixel);
				int blue = p.getBlue(pixel);
				System.out.printf("\t%3d %3d %3d", red, green, blue);
			}
			System.out.print("\n");
		}
	}
}