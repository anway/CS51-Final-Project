package graphics;

import image_comparer.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.pushingpixels.substance.api.skin.SubstanceMarinerLookAndFeel;

public class UIDriver extends JFrame
{
	private static final long serialVersionUID = 1L;
	private static final Dimension SCREEN_SIZE =
		Toolkit.getDefaultToolkit().getScreenSize();
	private static final int FIND_SIMILAR = 0;
	private static final int COMPARE_FIRST = 1;
	private static final int COMPARE_SECOND = 2;

	private final JTextArea t1 = createTextArea();
	private final JTextArea t2 = createTextArea();

	private JFileChooser c;
	private JLabel lSimilar, lCompare1, lCompare2;
	private File fSimilar = null;
	private File fCompare1 = null, fCompare2 = null;
	private String helpContents = null;
	private JLabel[] labels;

	public UIDriver()
	{
		setSize(SCREEN_SIZE.width/2, SCREEN_SIZE.height/2);
		setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH );
		setVisible(true);
		setDefaultCloseOperation(UIDriver.EXIT_ON_CLOSE);
		setTitle("Image Organizer");

		init();
	}

	public void init()
	{
		try
		{
			Image cow1 = ImageIO.read(UIDriver.class.getResource(
				"images/cow_large.png"));
			Image cow2 = ImageIO.read(UIDriver.class.getResource(
				"images/cow_small.png"));
			setIconImages(Arrays.asList(new Image[]{cow1, cow2}));
		} catch(Exception e) {}

		c = createImageChooser();
		setJMenuBar(createMenuBar());
		setContentPane(createSplitPane());
		ToolTipManager.sharedInstance().setInitialDelay(50);
	}

	private JMenuBar createMenuBar()
	{
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;

		menuBar = new JMenuBar();

		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription("Main menu");
		menuBar.add(menu);

		menuItem = new JMenuItem("Open image");
		menuItem.getAccessibleContext().setAccessibleDescription(
			"Add an image to the workbench");
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (c.showOpenDialog(UIDriver.this) ==
					JFileChooser.APPROVE_OPTION)
				{
					int emptyIndex = -1;
					for (int i = 0; i < labels.length; ++i)
						if (labels[i].getIcon() == null)
						{
							emptyIndex = i;
							break;
						}
					if (emptyIndex < 0)
						JOptionPane.showMessageDialog(UIDriver.this,
							"Workbench is full", "Error",
							JOptionPane.WARNING_MESSAGE);
					else
						setIcon(labels[emptyIndex], c.getSelectedFile(), false);
				}
			}
		});
		menu.add(menuItem);

		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menu.getAccessibleContext().setAccessibleDescription("Help menu");
		menuBar.add(menu);

		menuItem = new JMenuItem("About");
		menuItem.getAccessibleContext().setAccessibleDescription("About");
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (helpContents == null)
					helpContents = loadHelpContents();
				JOptionPane.showMessageDialog(UIDriver.this, helpContents,
					"Help", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menu.add(menuItem);

		return menuBar;
	}

	private JSplitPane createSplitPane()
	{
		JSplitPane p = new JSplitPane();
		p.setResizeWeight(1.);
		p.setLeftComponent(createWorkbench());

		JSplitPane pSub = createComparisonPane();
		p.setRightComponent(pSub);

		return p;
	}

	private JPanel createWorkbench()
	{
		JPanel p = new JPanel()
		{
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				g.setColor(Color.GRAY);
				g.setFont(new Font(g.getFont().getName(), Font.ITALIC, 50));
				g.drawString("Add images to workbench", 20, 50);
			}
		};
		
		p.setMinimumSize(new Dimension(0,0));
		p.setLayout(new GridLayout(5,5));
		labels = createImageLabels(5,5);
		for (JLabel label : labels)
			p.add(label);
		
		p.addComponentListener(new ComponentAdapter()
		{

			public void componentResized(ComponentEvent arg0)
			{
				for (JLabel label : labels)
				{
					if (label.getIcon() != null)
					{
						try
						{
							setIcon(label, new File(label.getName()), true);
						} catch (Exception ex) {};
					}
				}
			}
		});
		
		return p;
	}

	private JLabel[] createImageLabels(int rows, int cols)
	{
		JLabel[] labels = new JLabel[rows*cols];
		for (int i = 0; i < labels.length; ++i)
			labels[i] = createImageLabel();
		return labels;
	}

	private JSplitPane createComparisonPane()
	{
		JSplitPane p = new JSplitPane();
		p.setMinimumSize(new Dimension(0,0));
		p.setResizeWeight(0.5);
		p.setLeftComponent(createSimilarImagesFinder());
		p.setRightComponent(createPairwiseComparer());
		return p;
	}

	private JPanel createSimilarImagesFinder()
	{
		JPanel p = new JPanel();
		p.setMinimumSize(new Dimension(0,0));
		p.setLayout(new BorderLayout());

		JButton b = new JButton("Find Similar");
		b.setFont(b.getFont().deriveFont(Font.BOLD));
		b.setPreferredSize(new Dimension(200, 40));

		JPanel controls = new JPanel();
		controls.setLayout(new BorderLayout());
		controls.add(b, BorderLayout.NORTH);
		controls.add(lSimilar = createSelectionLabel(FIND_SIMILAR),
			BorderLayout.SOUTH);
		p.add(controls, BorderLayout.NORTH);

		p.add(t1, BorderLayout.CENTER);

		b.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					new MyImage(fSimilar);
				} catch (Exception ex)
				{
					t1.setText("\n" + ex.getMessage());
					return;
				}
				t1.setText("\nComing soon!");
			}
		});

		return p;
	}

	private JPanel createPairwiseComparer()
	{
		JPanel p = new JPanel();
		p.setMinimumSize(new Dimension(0,0));
		p.setLayout(new BorderLayout());

		JButton b = new JButton("Compare");
		b.setFont(b.getFont().deriveFont(Font.BOLD));
		b.setPreferredSize(new Dimension(200, 40));

		JPanel controls = new JPanel();
		controls.setLayout(new BorderLayout());
		controls.add(b, BorderLayout.NORTH);
		controls.add(lCompare1 = createSelectionLabel(COMPARE_FIRST),
			BorderLayout.CENTER);
		controls.add(lCompare2 = createSelectionLabel(COMPARE_SECOND),
			BorderLayout.SOUTH);
		p.add(controls, BorderLayout.NORTH);

		p.add(t2, BorderLayout.CENTER);

		b.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				MyImage im1, im2;
				try
				{
					im1 = new MyImage(fCompare1);
					im2 = new MyImage(fCompare2);
				} catch (Exception ex)
				{
					t2.setText("\n" + ex.getMessage());
					return;
				}
				im1.setSize(32, 32);
				im2.setSize(32, 32);

				PixelArray p1 = im1.toPixelArray();
				PixelArray p2 = im2.toPixelArray();

				//TODO what to display?
				HistogramComparer h = new HistogramComparer();
				KeypointComparer k = new KeypointComparer();
				PHashComparer pH = new PHashComparer();
				SetComparer s = new SetComparer();

				double hScore = h.compare(p1,p2);
				double kScore = k.compare(p1,p2);
				double pHScore = pH.compare(p1,p2);
				double sScore = s.compare(p1,p2);
				
				t2.setText("\nHistogram comparison: " + hScore +
					"\n\nKeypoint matching: " + kScore +
					"\n\nPerceptual hash: " + pHScore +
					"\n\nSet resemblance: " + sScore);
				
				double score;
				if (sScore > 0.9)
					score = sScore;
				else
					score = (hScore + kScore + pHScore)/3;
				
				t2.append("\n\n**Beta**\nOverall similarity: " + score);
			}

		});

		return p;
	}

	private JLabel createSelectionLabel(final int mode)
	{
		final JLabel l = new JLabel("Select image");
		l.setPreferredSize(new Dimension(200, 40));
		l.setOpaque(true);
		l.setBackground(Color.BLACK);
		l.setFont(l.getFont().deriveFont(Font.BOLD));
		l.setHorizontalAlignment(SwingConstants.CENTER);
		l.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (c.showOpenDialog(UIDriver.this) ==
					JFileChooser.APPROVE_OPTION)
				{
					File f = c.getSelectedFile();
					l.setText(f.getName());
					if (mode == FIND_SIMILAR)
						fSimilar = f;
					else if (mode == COMPARE_FIRST)
						fCompare1 = f;
					else
						fCompare2 = f;
					
					l.setToolTipText(getToolTipText(f));
				}
			}

			public void mouseEntered(MouseEvent e)
			{
				l.setBackground(Color.WHITE);
			}

			public void mouseExited(MouseEvent e)
			{
				l.setBackground(Color.BLACK);
			}
		});
		
		return l;
	}

	private String getToolTipText(File f)
	{
		Image im;
		try
		{
			im = ImageIO.read(f);
		} catch(Exception ex)
		{
			return "";
		}
		int w, h;
		if (im.getWidth(null) > im.getHeight(null))
		{
			w = 50;
			h = (int)((double) w / im.getWidth(null) *
				im.getHeight(null));
		}
		else
		{
			h = 50;
			w = (int)((double) h / im.getHeight(null) *
				im.getWidth(null));
		}
		return "<html><img src=\"file:" + f.getPath() + "\" width=" + w +
			" height=" + h + " </img></html>";
	}
	
	private JFileChooser createImageChooser()
	{
		JFileChooser c = new JFileChooser();
		c.setDragEnabled(true);
		c.setDialogTitle("Open image");
		c.setAcceptAllFileFilterUsed(false);
		c.setFileFilter(new FileFilter()
		{
			public final String[] IMAGE_EXTS = new String[]
					{"png", "gif", "jpg", "tif", "bmp"};

			public boolean accept(File f)
			{
				if (f.isDirectory())
					return true;

				String name = f.getName();
				int nameEnd = name.lastIndexOf('.');
				if (nameEnd < 0 || nameEnd >= name.length())
					return false;
				String extension = name.substring(nameEnd+1).toLowerCase();
				return Arrays.asList(IMAGE_EXTS).contains(extension);
			}

			public String getDescription()
			{
				return "Image files";
			}
		});
		c.setAccessory(new ImagePreview(c));
		return c;
	}

	private JTextArea createTextArea()
	{
		JTextArea t = new JTextArea();
		t.setEditable(false);
		t.setLineWrap(true);
		t.setWrapStyleWord(true);
		return t;
	}

	private String neatlyPrint(String s, Font f, int w)
	{
		FontMetrics metrics = getFontMetrics(f);
		String[] sArray = s.split("\n\n");
		String result = "";
		for (String line : sArray)
			result += neatlyPrintOneLine(line, metrics, w) + "\n\n";
		return result.length() == 0 ? result :
			result.substring(0, result.length()-2);
	}

	private String neatlyPrintOneLine(String s, FontMetrics metrics, int w)
	{
		String result = "";
		int width = 0;

		String[] sArray = s.split(" ");
		for (int i = 0; i < sArray.length; ++i)
		{
			String word = sArray[i].replace("\n", " ") +
				(i < sArray.length-1 ? " " : "");
			int wordWidth = metrics.stringWidth(word);
			if (width + wordWidth < w)
			{
				result += word;
				width += wordWidth;
			}
			else
			{
				result += "\n" + word;
				width = 0;
			}
		}

		return result;
	}

	private String loadHelpContents()
	{
		Scanner in = new Scanner(UIDriver.class.getResourceAsStream(
			"files/helpContents.txt"));
		String contents = "";
		while (in.hasNextLine())
			contents += in.nextLine() + "\n";
		in.close();
		return neatlyPrint(contents.substring(0, contents.length()-1),
			getFont(), 150);
	}

	private boolean openedImagesContains(String filename)
	{
		for (JLabel label : labels)
			if (label.getName() != null && label.getName().equals(filename))
				return true;
		return false;
	}
	
	private JLabel createImageLabel()
	{
		final JLabel label = new JLabel();
		final JPopupMenu menu = createPopupMenu(label);
		label.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1 &&
						label.getIcon() == null)
					if (c.showOpenDialog(UIDriver.this) ==
							JFileChooser.APPROVE_OPTION)
						setIcon(label, c.getSelectedFile(), false);
			}
			public void mousePressed(MouseEvent e)
			{
				maybeShowPopup(e);
			}

			public void mouseReleased(MouseEvent e)
			{
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e)
			{
				if (e.isPopupTrigger() && label.getIcon() != null)
					menu.show(e.getComponent(), e.getX(), e.getY());
			}
		});
		return label;
	}
	
	private void setIcon(JLabel label, File f, boolean modifying)
	{
		if (!modifying && openedImagesContains(f.getPath()))
			JOptionPane.showMessageDialog(UIDriver.this,
					"Image is in workbench",
					"Error",
					JOptionPane.WARNING_MESSAGE);
		else
		{
			ImageIcon imOriginal;
			try
			{
				imOriginal = new ImageIcon(ImageIO.read(f));
			} catch(Exception ex)
			{
				JOptionPane.showMessageDialog(UIDriver.this,
					"Cannot open image", "Error", JOptionPane.WARNING_MESSAGE);
				return;
			};
			
			int origW = imOriginal.getIconWidth();
			int origH = imOriginal.getIconHeight();
			
			int buffer = Math.min(8, Math.min(label.getWidth(),
				label.getHeight()));
			int width = label.getWidth() - buffer/2;
			int height = label.getHeight() - buffer/2;
			
			double ratioW = (double) origW / width;
			double ratioH = (double) origH / height;
			
			int imW, imH;
			if (ratioW > ratioH)
			{
				imW = width;
				imH = (int) ((double)width/origW*origH);
			}
			else
			{
				imH = height;
				imW = (int)((double)height/origH*origW);
			}
			
			ImageIcon im = new ImageIcon(
				imOriginal.getImage().getScaledInstance(
				imW, imH, Image.SCALE_SMOOTH));
			label.setIcon(im);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setVerticalAlignment(JLabel.CENTER);
			
			if (!modifying)
			{
				label.setName(f.getPath());
				label.setToolTipText(f.getName());
			}
		}
	}
	
	private JPopupMenu createPopupMenu(final JLabel label)
	{
		final JPopupMenu menu = new JPopupMenu();
		
		JMenuItem item = new JMenuItem("Remove image");
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				label.setName(null);
				label.setIcon(null);
				label.setToolTipText(null);
			}
		});
		menu.add(item);
		
		item = new JMenuItem("Change image");
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (c.showOpenDialog(UIDriver.this) ==
						JFileChooser.APPROVE_OPTION)
					setIcon(label, c.getSelectedFile(), false);
			}
		});
		menu.add(item);
		
		item = new JMenuItem("Send to <Find Similar>");
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				fSimilar = new File(label.getName());
				lSimilar.setText(fSimilar.getName());
				lSimilar.setToolTipText(getToolTipText(fSimilar));
			}
		});
		menu.add(item);
		
		item = new JMenuItem("Send to <Compare> Image 1");
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				fCompare1 = new File(label.getName());
				lCompare1.setText(fCompare1.getName());
				lCompare1.setToolTipText(getToolTipText(fCompare1));
			}
		});
		menu.add(item);
		
		item = new JMenuItem("Send to <Compare> Image 2");
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				fCompare2 = new File(label.getName());
				lCompare2.setText(fCompare2.getName());
				lCompare2.setToolTipText(getToolTipText(fCompare2));
			}
		});
		menu.add(item);
		
		return menu;
	}
	
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(new SubstanceMarinerLookAndFeel());
		} catch (Exception e) {}

		java.awt.EventQueue.invokeLater ( new Runnable()
		{
			public void run()
			{
				JFrame viewer = new UIDriver();
				viewer.validate();
			}
		} );
	}
}