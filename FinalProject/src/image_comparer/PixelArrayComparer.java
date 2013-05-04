package image_comparer;

import java.util.Hashtable;

/*
 * Abstract class that all image comparer classes extend.
 */
public abstract class PixelArrayComparer
{
	protected Hashtable<String, Object> t = new Hashtable<String, Object>();
	
	/*
	 * Compares two pixel arrays.  A pixel array has an ID object stored in a
	 * hash table.  For each array, compare() either retrieves the ID from
	 * the hash table or, if the array has not been seen yet, computes the ID.
	 */
	public double compare(PixelArray a1, PixelArray a2)
	{
		Object id1, id2;
		String str1 = a1.toString(), str2 = a2.toString();
		
		if (t.containsKey(str1))
			id1 = t.get(str1);
		else
		{
			id1 = getID(a1);
			t.put(str1, id1);
		}
		
		if (t.containsKey(str2))
			id2 = t.get(str2);
		else
		{
			id2 = getID(a2);
			t.put(str2, id2);
		}
		
		return compareIDs(id1, id2);
	}
	
	/*
	 * Compares the IDs of two pixel arrays
	 */
	protected abstract double compareIDs(Object id1, Object id2);
	
	/*
	 * Generates a unique ID for a pixel array.
	 * The type of the ID depends on the image comparer class.
	 */
	protected abstract Object getID(PixelArray a);
}
