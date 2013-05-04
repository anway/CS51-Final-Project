/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package graphics;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

/* Displays a thumbnail view of the selected image in the file chooser
 * Adapted from http://docs.oracle.com/javase/tutorial/uiswing/components/
 * filechooser.html, accessed 4/26/2013 */
public class ImagePreview extends JComponent implements PropertyChangeListener
{
	private static final long serialVersionUID = 1L;

	private ImageIcon thumbnail = null;
	private File file = null;

	public ImagePreview(JFileChooser c)
	{
		setPreferredSize(new Dimension(100, 50));
		c.addPropertyChangeListener(this);
	}

	public void loadImage()
	{
		if (file == null)
		{
			thumbnail = null;
			return;
		}

		ImageIcon tmpIcon = new ImageIcon(file.getPath());
		if (tmpIcon != null)
		{
			if (tmpIcon.getIconWidth() > 90)
			{
				thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance(
						90, -1, Image.SCALE_DEFAULT));
			}
			else
			{
				thumbnail = tmpIcon;
			}
		}
	}

	public void propertyChange(PropertyChangeEvent e)
	{
		boolean update = false;
		String prop = e.getPropertyName();

		if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop))
		{
			file = null;
			update = true;
		}
		else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop))
		{
			file = (File) e.getNewValue();
			update = true;
		}

		if (update)
		{
			thumbnail = null;
			if (isShowing())
			{
				loadImage();
				repaint();
			}
		}
	}

	protected void paintComponent(Graphics g)
	{
		if (thumbnail == null)
			loadImage();
		if (thumbnail != null)
		{
			int x = getWidth()/2 - thumbnail.getIconWidth()/2;
			int y = getHeight()/2 - thumbnail.getIconHeight()/2;
			if (y < 0)
				y = 0;
			if (x < 5)
				x = 5;
			thumbnail.paintIcon(this, g, x, y);
		}
	}
}