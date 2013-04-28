package image_comparer;

/*
 * Compares two images using perceptual hashing.
 */
public class PHashComparer implements PixelArrayComparer
{
	public static final int NUM_COLORS = 32;
	
	public PHashComparer()
	{
		
	}
	
	//TODO
	@SuppressWarnings("unused")
    
    // this is supposed to be called on pixel arrays already set to be small
	public double compare(PixelArray a1, PixelArray a2)
	{
		int w = a1.getWidth();
        int h = a1.getHeight();
        int dimension = h*w;
        int[][] grayArray1 = new int[h][w];
        int pixel;
        int temp;
        int sum1 = 0;
        int sum2 = 0;
        for (int i=0;i<w;++i) {
            for (int j=0;j<h;++j) {
                pixel = a1.getPixel(i, j);
                temp = ((getBlue(pixel)+getBlue(pixel)+getGreen(pixel))/12);
                sum1 += temp;
                grayArray1[j][i] = temp;
            }
        }
        int[][] grayArray2 = new int[h][w];
        for (int i=0;i<w;++i) {
            for (int j=0;j<h;++j) {
                pixel = a2.getPixel(i, j);
                temp = ((getBlue(pixel)+getBlue(pixel)+getGreen(pixel))/12);
                sum2 += temp;
                grayArray2[j][i] = temp;
            }
        }
        sum1 /= dimension;
        sum2 /= dimension;
        int matches = 0;
        for (int i=0;i<w;++i) {
            for (int j=0;j<h;++j) {
                if ((grayArray1[j][i] < sum1) == (grayArray2[j][i] < sum2)) {
                    ++matches;
                }
            }
        }
		return (double(matches)/double(dimension));
	}

}
