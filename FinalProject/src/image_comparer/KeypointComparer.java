package image_comparer;
import java.util.Random;

public class KeypointComparer implements PixelArrayComparer
{
	public KeypointComparer()
	{
		
	}
	
	public boolean contains(int[] array1, int[] array2, int value1, int value2) {
		int length = array1.length; //they're the same length
		for(int i=0; i<length; i++) {
			if (value1 == array1[i]) && (value2 == array2[i])
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
	public double compare(PixelArray a1, PixelArray a2, int n) {
		// we're assuming that height and width of a1 and a2 are
		// same right??
		int width = a1.getWidth();
		int height = a1.getHeight();
		// since there aren't any tuples we'll just use two arrays
		// to record points already picked
		int[n] pickedWidth;
		int[n] pickedHeight;
		int numMatched, currWidth, currHeight;
		Random rand = new Random();
		for (int i=0; i<n; i++) {
			do {
				currWidth = rand.nextInt(width);
				currHeight = rand.nextInt(height);
			}
			while (contains(pickedWidth, currWidth, pickedHeight, currHeight));
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
        int dimension = width*height;
        int[(height-1)*(width-1)] diffs;
        int counter = 0;
        float rsum;
        float bsum;
        float gsum;
        for (int i=1;++i<height;) {
            for (int j=1;++j<height;) {
                rsum = 0;
                bsum = 0;
                gsum = 0;
                for (int k=-1;k<2;++k) {
                    for (int l=-1;l<2;++l) {
                        rsum += 
                        bsum +=
                        gsum +=
                    }
                }
                
                diffs[counter] = 
            }
        }
        
        int[n] pickedWidth;
		int[n] pickedHeight;
        for (int i=0; i<n; i++) {
			do {
				currWidth = rand.nextInt(width);
				currHeight = rand.nextInt(height);
			}
			while (contains(pickedWidth, currWidth, pickedHeight, currHeight));
			if (a1.getPixel(currWidth, currHeight)==a2.getPixel(currWidth, currHeight))
				numMatched++;
			pickedWidth[n]=currWidth;
			pickedHeight[n]=currHeight;
		}
    }
}
