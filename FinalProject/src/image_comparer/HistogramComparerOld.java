package image_comparer;

/*
 * Constructs histograms to compare two images.
 */
public class HistogramComparerOld implements PixelArrayComparer
{
	public HistogramComparerOld()
	{
		
	}
	
	public double[] processImage1(PixelArray a)
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
	
	public double compare1(PixelArray a1, PixelArray a2)
	{
		double[] a1Rgba = new double[3];
		double[] a2Rgba = new double[3];
		a1Rgba = processImage1(a1);
		a2Rgba = processImage1(a2);
		double difference = 0.0;
		for (int i=0; i<3; i++){
			difference += Math.abs(a1Rgba[i] - a2Rgba[i]);
		}
		return (3.0-difference)/3.0;
	}
	
	public double processImage2(PixelArray a)
	{
		// find average difference between current pixel and neighbors
		// for each pixel in array. then construct color histogram
		double[] rgba = new double[]{0.0, 0.0, 0.0};
		int width = a.getWidth();
		int height = a.getHeight();
		int currPixel = 0, neighbor = 0, numNeigh=0;
		for (int i=0; i<width; i++){
			for (int j=0; j<height; j++){
				for (int k=-2; k<=2; k++){
					for (int m=-2; m<=2; m++){
						if ((i+k>0 && i+k<width) && (j+m>0 && j+m<height) && (m!=0 && k!=0))
						{
							++numNeigh;
							neighbor = a.getPixel(i+k, j+m);
							currPixel = a.getPixel(i, j);
							rgba[0] += Math.abs(PixelArray.getRed(currPixel)-PixelArray.getRed(neighbor));
							rgba[1] += Math.abs(PixelArray.getGreen(currPixel)-PixelArray.getGreen(neighbor));
							rgba[2] += Math.abs(PixelArray.getBlue(currPixel)-PixelArray.getBlue(neighbor));
						}
					}
				}
				rgba[0]/=(double)numNeigh;
				rgba[1]/=(double)numNeigh;
				rgba[2]/=(double)numNeigh;
			}
		}
		return (rgba[0]+rgba[1]+rgba[2])/3.0;
	}
	/*
	public double compare2(PixelArray a1, PixelArray a2)
	{
		double a1Rgba = processImage2(a1);
		double a2Rgba = processImage2(a2);
		double difference = Math.abs(a1Rgba - a2Rgba);
		return (1.0-difference)/1.0;
	}
	
	public double[] processImage3(PixelArray a)
	{
		double[] rgba = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		int width = a.getWidth();
		int height = a.getHeight();
		int currPixel = 0, currRed=0, currGreen=0, currBlue=0;
		for (int i=0; i<width; i++){
			for (int j=0; j<height; j++){
				currPixel = a.getPixel(i, j);
				currRed = PixelArray.getRed(currPixel);
				if (currRed>0 && currRed<51)
					rgba[0]++;
				if (currRed>50 && currRed<102)
					rgba[1]++;
				if (currRed>101 && currRed<153)
					rgba[2]++;
				if (currRed>152 && currRed<204)
					rgba[3]++;
				if (currRed>203 && currRed<=255)
					rgba[4]++;
				currGreen = PixelArray.getGreen(currPixel);
				if (currGreen>0 && currGreen<51)
					rgba[5]++;
				if (currGreen>50 && currGreen<102)
					rgba[6]++;
				if (currGreen>101 && currGreen<153)
					rgba[7]++;
				if (currGreen>152 && currGreen<204)
					rgba[8]++;
				if (currGreen>203 && currGreen<=255)
					rgba[9]++;
				currBlue = PixelArray.getBlue(currPixel);
				if (currBlue>0 && currBlue<51)
					rgba[10]++;
				if (currBlue>50 && currBlue<102)
					rgba[11]++;
				if (currBlue>101 && currBlue<153)
					rgba[12]++;
				if (currBlue>152 && currBlue<204)
					rgba[13]++;
				if (currBlue>203 && currBlue<=255)
					rgba[14]++;
			}
		}
		for (int i=0; i<15; i++)
		{
			rgba[i]=(double)rgba[i]/(height*width);
		}
		return rgba;
	}
	
	public double compare3(PixelArray a1, PixelArray a2)
	{
		int width = a1.getWidth();
		int height = a1.getHeight();
		double[] a1Rgba = new double[15];
		double[] a2Rgba = new double[15];
		a1Rgba = processImage3(a1);
		a2Rgba = processImage3(a2);
		double difference = 0.0;
		for (int i=0; i<15; i++){
			difference += Math.abs(a1Rgba[i] - a2Rgba[i]);
		}
		return (double)(15.0-difference)/15.0;
	}*/
	
	public double compare(PixelArray a1, PixelArray a2)
	{
		return compare1(a1, a2);
	}

}