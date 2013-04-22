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
	public double compare(PixelArray a1, PixelArray a2)
	{
		PixelArray a1Rounded = a1.round(NUM_COLORS);
		PixelArray a2Rounded = a2.round(NUM_COLORS);
		return 0.;
	}

}
