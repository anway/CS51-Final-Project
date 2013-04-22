package image_comparer;
import java.util.Random;
import java.util.*;

public class KeypointComparer implements PixelArrayComparer
{
	public KeypointComparer()
	{

	}

	public boolean contains(int[] array1, int[] array2, int value1, int value2){
		int length = array1.length; //they're the same length
		for(int i=0; i<length; i++){
			if (value1 == array1[i] && value2 == array2[i])
				return true;
		}
		return false;
	}

	//TODO
	/*
	 * number of pixels to compare determined by user?????
	 * I've left it as n for now
	 * Pick random x and random y and make sure it's not a repeat
	 * of already picked point. Then compare and get a percentage
	 */
	public double compare(PixelArray a1, PixelArray a2) {
		//TODO n is computed how?
		int n = 0;
		
		// we're assuming that height and width of a1 and a2 are
		// same right??
		int width = a1.getWidth();
		int height = a1.getHeight();
		// since there aren't any tuples we'll just use two arrays
		// to record points already picked
		int pickedWidth[] = new int[n];
		int pickedHeight[] = new int[n];
		int numMatched=0, currWidth, currHeight;
		Random rand = new Random();
		for (int i=0; i<n; i++) {
			do {
				currWidth = rand.nextInt(width);
				currHeight = rand.nextInt(height);
			}
			while (contains(pickedWidth, pickedHeight, currWidth, currHeight));
			if (a1.getPixel(currWidth, currHeight)==a2.getPixel(currWidth, currHeight))
				numMatched++;
			pickedWidth[n]=currWidth;
			pickedHeight[n]=currHeight;
		}
		return (double)numMatched/n;
	}

	public double keypointcompare(PixelArray a1, PixelArray a2, int n) {
		int width = a1.getWidth();
		int height = a1.getHeight();
		//int dimension = width*height;
		//ArrayList<Double> diffs = new ArrayList<Double>();
		ArrayList<Integer> xcos = new ArrayList<Integer>();
		ArrayList<Integer> ycos = new ArrayList<Integer>();
		float rsum;
		float bsum;
		float gsum;
		float diff;
		int counter = 0;
        // finds interesting pixels
		for (int i=1;++i<height;) {
			for (int j=1;++j<width;) {
				rsum = 0;
				bsum = 0;
				gsum = 0;
				for (int k=-1;k<2;++k) {
					for (int l=-1;l<2;++l) {
						rsum += (float) a1.getRed(a1.getPixel(i+k, j+l));
						bsum += (float) a1.getBlue(a1.getPixel(i+k, j+l));
						gsum += (float) a1.getGreen(a1.getPixel(i+k, j+l));
					}
				}
				diff = Math.abs(rsum-a1.getRed(a1.getPixel(i, j)))
						+ Math.abs(bsum-a1.getBlue(a1.getPixel(i, j)))
								+ Math.abs(gsum-a1.getGreen(a1.getPixel(i, j)));
								if (diff > 0.2) {
									xcos.add(i);
									ycos.add(j);
									++counter;
								}
			}
		}
        if (counter<20) {
            return compare(a1, a2, n);
        }
        int matched = 0;
		for (int i=0; i<counter; i++) {
			if (a1.getPixel(xcos.get(i), ycos.get(i))==a2.getPixel(xcos.get(i), ycos.get(i)))
				matched++;
		}
		return ((double) matched) / ((double) n);
	}
}
