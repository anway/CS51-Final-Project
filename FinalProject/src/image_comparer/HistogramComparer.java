package image_comparer;

/*
 * Constructs histograms to compare two images.
 */
public class HistogramComparer implements PixelArrayComparer
{
	public HistogramComparer()
	{
		
	}
	
	public int[] processImage(PixelArray a)
	{
		int[] rgba = new int[]{0,0,0,0};
		int width = a.getWidth();
		int height = a.getHeight();
		int currPixel = 0;
		for (int i=0; i<width; i++){
			for (int j=0; j<height; j++){
				currPixel = a.getPixel(i, j);
				red += PixelArray.getRed(currPixel);
				green += PixelArray.getGreen(currPixel);
				blue += PixelArray.getBlue(currPixel);
				alpha += PixelArray.getAlpha(currPixel);
			}
		}
		rgba[0] /= (height*width*255);
		rgba[1] /= (height*width*255);
		rgba[2] /= (height*width*255);
		rgba[3] /= (height*width*255);
		return rgba;
	}
	
	//TODO
	public double compare(PixelArray a1, PixelArray a2)
	{
		int a1Rgba = new int[4];
		int a2Rgba = new int[4];
		a1Rgba = processImage(a1);
		a2Rgba = processImage(a2);
		int difference = 0;
		for (int i=0; i<4; i++){
			difference += Math.abs(a1Rgba[i] - a2Rgba[i]);
		}
		return (4.0-difference)/4.0;
	}

}
