package image_comparer;

import java.io.IOException;

public class Driver
{
  public static void main(String[] args) throws IOException
  {
    MyImage im1 = new MyImage(args[0]);
    MyImage im2 = new MyImage(args[1]);

    int width = Math.min(im1.getWidth(), im2.getWidth());
    im1.setSize((int) ((double) width/(double) im1.getWidth()));
    im2.setSize((int) ((double) width/(double) im2.getWidth()));
    PixelArray a1 = im1.toPixelArray();
    PixelArray a2 = im2.toPixelArray();
    
    KeypointComparer k = new KeypointComparer();
    SetComparer s = new SetComparer();
    HistogramComparer h = new HistogramComparer();
    PHashComparer p = new PHashComparer();

    if (im1.getAspectRatio() == im2.getAspectRatio())
    {
      System.out.printf("Keypoint matching: %f%n", k.compare(a1, a2));
      System.out.printf("Set resemblance check: %f%n", s.compare(a1, a2));
    }

    System.out.printf("Histogram comparison: %f%n", h.compare(a1, a2));
    System.out.printf("Perceptual hash: %f%n", p.compare(a1.round(), a2.round()));
  }
}