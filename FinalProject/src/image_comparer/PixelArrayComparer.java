package image_comparer;

/*
 * Interface that all image comparer classes implement.
 */
public interface PixelArrayComparer
{
	public double compare(PixelArray a1, PixelArray a2);
}
