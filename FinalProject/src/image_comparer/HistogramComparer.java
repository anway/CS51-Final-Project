package image_comparer;

/*
 * Constructs histograms to compare two images.
 */
public class HistogramComparer extends PixelArrayComparer
{
	public HistogramComparer()
	{
		
	}
	
	/* Creates a histogram from the colors of a pixel array */
	protected double[] getID(PixelArray a)
	{
		double[] rgba = new double[]{0.0, 0.0, 0.0};
		int width = a.getWidth();
		int height = a.getHeight();
		int currPixel = 0;
		for (int i=0; i<width; i++){
			for (int j=0; j<height; j++){
				currPixel = a.getPixel(i, j);
				rgba[0] += PixelArray.getRed(currPixel);
				rgba[1] += PixelArray.getGreen(currPixel);
				rgba[2] += PixelArray.getBlue(currPixel);
			}
		}
		rgba[0] = (double)rgba[0]/(height*width*255.0);
		rgba[1] = (double)rgba[1]/(height*width*255.0);
		rgba[2] = (double)rgba[2]/(height*width*255.0);
		return rgba;
	}
	
	protected double compareIDs(Object o1, Object o2)
	{
		double[] a1Rgba = (double[]) o1;
		double[] a2Rgba = (double[]) o2;
		
		double difference = 0.0;
		for (int i=0; i<3; i++){
			difference += Math.abs(a1Rgba[i] - a2Rgba[i]);
		}
		
		return (3.0-difference)/3.0;
	}
}
