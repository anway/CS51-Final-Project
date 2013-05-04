package image_comparer;

/*
 * Compares two images using perceptual hashing.
 * Expects the images to be the same (small) size.
 */
public class PHashComparer extends PixelArrayComparer
{
	public static final int NUM_COLORS = 32;
	
	public PHashComparer()
	{
		
	}
	
	protected double compareIDs(Object o1, Object o2)
	{
		Object[] id1 = (Object[]) o1;
		Object[] id2 = (Object[]) o2;
		
		int sum1 = (Integer) id1[0], sum2 = (Integer) id2[0];
		int[][] grayArray1 = (int[][]) id1[1], grayArray2 = (int[][]) id2[1];
		
        int matches = 0;
        for (int i = 0, n = grayArray1.length; i < n; ++i)
        	for (int j = 0, m = grayArray1[i].length; j < m; ++j)
        		if ((grayArray1[i][j] < sum1) == (grayArray2[i][j] < sum2)) {
                    ++matches;
                }
        
        return ((double) matches) /
        	((double) (grayArray1.length * grayArray1[0].length));
	}
	
	protected Object[] getID(PixelArray a)
	{
		int w = a.getWidth();
        int h = a.getHeight();
        int[][] grayArray = new int[h][w];
        int pixel;
        int temp;
        int sum = 0;
        for (int i=0;i<w;++i) {
            for (int j=0;j<h;++j) {
                pixel = a.getPixel(i, j);
                temp = ((PixelArray.getBlue(pixel)+PixelArray.getBlue(pixel)+
                	PixelArray.getGreen(pixel))/12);
                sum += temp;
                grayArray[j][i] = temp;
            }
        }
        return new Object[]{sum / (w*h), grayArray};
	}
}
