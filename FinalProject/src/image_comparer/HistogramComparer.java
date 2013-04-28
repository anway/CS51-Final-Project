package image_comparer;

/*
 * Constructs histograms to compare two images.
 */
public class HistogramComparer implements PixelArrayComparer
{
	public HistogramComparer()
	{
		
	}
	
	public double[] processImage(PixelArray a)
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
		System.out.println("Red: " + rgba[0] + "Green: " + rgba[1] + "Blue: " + rgba[2]);
		return rgba;
	}
	
	//TODO
	public double compare(PixelArray a1, PixelArray a2)
	{
		double[] a1Rgba = new double[3];
		double[] a2Rgba = new double[3];
		a1Rgba = processImage(a1);
		a2Rgba = processImage(a2);
		double difference = 0.0;
		for (int i=0; i<3; i++){
			difference += Math.abs(a1Rgba[i] - a2Rgba[i]);
		}
		return (3.0-difference)/3.0;
	}

}
