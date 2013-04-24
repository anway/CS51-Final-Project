package image_comparer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/*
 * Compares two images using a set resemblance algorithm.
 */
public class SetComparer implements PixelArrayComparer
{
	public static final int SKETCH_SIZE = 100;
	
	public SetComparer()
	{
		
	}
	
	//TODO
	public double compare(PixelArray a1, PixelArray a2)
	{
		int[] sketch1 = getSketch(a1);
		int[] sketch2 = getSketch(a2);
		
		int matches = 0;
		for (int i = 0; i < SKETCH_SIZE; ++i)
			if (sketch1[i] == sketch2[i])
				++matches;
		
		return (double) matches / (double) 100;
	}

	public int[] getSketch(PixelArray p)
	{
		int[] sketch = new int[SKETCH_SIZE];
		
		Boolean[] shingles = shingle(p);
		Random r;
		for (int i = 0; i < SKETCH_SIZE; ++i)
		{
			r = new Random(i);
			Boolean[] permutation = shingles;
			Collections.shuffle(Arrays.asList(permutation), r);
			sketch[i] = Arrays.asList(permutation).indexOf(true);
		}
		
		return sketch;
	}
	
	public Boolean[] shingle(PixelArray p)
	{
		Boolean[] shingles = new Boolean[256];
		for (int i = 0; i < shingles.length; i++)
			shingles[i] = false;
		int size = Math.min(Math.min(p.getWidth(), p.getHeight()), 4);
		for (int i = 0, n = p.getWidth() - (size-1); i < n; ++i)
			for (int j = 0, m = p.getHeight() - (size-1); j < m; ++j)
			{
				int shingle = 0;
				for (int k = 0; k < size; ++k)
					for (int l = 0; l < size; ++l)
					{
						int pixel = p.getPixel(i+k, j+l);
						int gray = (PixelArray.getRed(pixel) +
							PixelArray.getGreen(pixel) +
							PixelArray.getBlue(pixel))/3;
						shingle = (k==0 && l==0) ? gray :
							shingle ^ gray;
					}
				shingles[shingle] = true;
			}
		return shingles;
	}
}