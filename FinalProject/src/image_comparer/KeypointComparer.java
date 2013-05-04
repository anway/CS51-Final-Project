package image_comparer;
import java.util.*;

/*
 * Matches key points in two images to compare them.  Assumes the images are
 * the same size.
 */
public class KeypointComparer extends PixelArrayComparer
{
	private Random rand;
	private PixelArray a1, a2;
	
	public KeypointComparer()
	{
		rand = new Random();
	}

	/*
	 * Overrides compare() because the comparison method needs access to the
	 * pixel arrays.  The instance fields a1 and a2 cannot be modified before
	 * the comparison is finished!
	 * */
	public synchronized double compare(PixelArray a1, PixelArray a2)
	{
		this.a1 = a1;
		this.a2 = a2;
		return super.compare(a1, a2);
	}
	
	/*
	 * If #(interesting pixels) < 20, pixels are randomly chosen
	 */
	private HashSet<int[]> getRandomID(PixelArray a)
	{
		int width = a.getWidth(), height = a.getHeight();
		
		int n = (int) Math.sqrt(width * height * 1.386);
		if (n == 0)
			n = 1;
		
		HashSet<int[]> cos = new HashSet<int[]>();
		for (int i = 0; i < n; ++i)
			cos.add(new int[]{rand.nextInt(width), rand.nextInt(height)});
		
		return cos;
	}

	@SuppressWarnings("unchecked")
	protected double compareIDs(Object o1, Object o2)
	{
		HashSet<int[]> cos = (HashSet<int[]>) o1;
		cos.addAll((HashSet<int[]>) o2);
		
		int counter = cos.size();
		int matched = 0;
		
		for (int[] p : cos)
		{
			if (PixelArray.getDistance(a1.getPixel(p[0], p[1]),
					a2.getPixel(p[0], p[1])) < 20.)
				++matched;
		}
		
		return (double) matched / (double) counter;
	}
	
	protected HashSet<int[]> getID(PixelArray a)
	{
		int width = a.getWidth();
		int height = a.getHeight();
		HashSet<int[]> cos = new HashSet<int[]>();
		double asum;
		double rsum;
		double bsum;
		double gsum;
		double diff;
		int counter = 0;

		// finds interesting pixels
		for (int i=1;++i<width-1;) {
			for (int j=1;++j<height-1;) {
				asum = 0;
				rsum = 0;
				bsum = 0;
				gsum = 0;
				int neighbor;
				for (int k=-1;k<2;++k) {
					for (int l=-1;l<2;++l) {
						neighbor = a.getPixel(i+k, j+l);
						asum += (double)
							PixelArray.getAlpha(neighbor);
						rsum += (double)
							PixelArray.getRed(neighbor);
						bsum += (double)
							PixelArray.getBlue(neighbor);
						gsum += (double)
							PixelArray.getGreen(neighbor);
					}
				}
				asum /= 9.0; rsum /= 9.0; bsum /= 9.0; gsum /= 9.0;
				int pixel = a.getPixel(i, j);
				diff = (Math.abs(asum-PixelArray.getAlpha(pixel))
					+ Math.abs(rsum-PixelArray.getRed(pixel))
					+ Math.abs(bsum-PixelArray.getBlue(pixel))
					+ Math.abs(gsum
						- PixelArray.getGreen(pixel)))/1024.0;
				if (diff > 0.1) {
					cos.add(new int[]{i,j});
					++counter;
				}
			}
		}
		
		if (counter < 20)
			return getRandomID(a);

		return cos;
	}
}
