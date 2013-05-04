package graphics;

import graphics.checkboxtree.AddCheckBoxToTree.CheckTreeSelectionModel;
import graphics.checkboxtree.FileTreeViewer;
import graphics.checkboxtree.FileTreeViewer.FileNode;
import graphics.checkboxtree.FileTreeViewer.IconData;
import image_comparer.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.pushingpixels.substance.api.skin.SubstanceMarinerLookAndFeel;

/* A graphical interface that lets a user select images from the native
 * file system, compare two images, and search for similar images */
public class UIDriver extends JFrame
{
	private static final long serialVersionUID = 1L;
	private static final Dimension SCREEN_SIZE =
		Toolkit.getDefaultToolkit().getScreenSize();
	private static final Dimension ZERO = new Dimension(0,0);
	
	private static final int FIND_SIMILAR = 0;
	private static final int COMPARE_FIRST = 1;
	private static final int COMPARE_SECOND = 2;
	
	private static final int M = 0;
	private static final int H = 1;
	private static final int K = 2;
	private static final int PH = 3;
	private static final int S = 4;

	// The workbench row size
	private static final int ROW_SIZE = 5;
	
	private final JTextArea t1 = createTextArea();
	private final JTextArea t2 = createTextArea();
	
	private JButton b1;
	private JButton b2;

	private JFileChooser c;
	private FileFilter ff;
	private JLabel lSimilar, lCompare1, lCompare2;
	private File fSimilar = null;
	private File fCompare1 = null, fCompare2 = null;
	private JLabel[] labels;
	
	private HistogramComparer h;
	private KeypointComparer k;
	private PHashComparer pH;
	private SetComparer s;
	
	private int comparer;
	private double threshold;
	private ImageIcon cow;
	
	private FileTreeViewer v;
	private CheckTreeSelectionModel m;
	private TreePath[] rootPaths;
	private ProgressMonitor pM;
	
	private JPanel customizePanel = new JPanel();
	private JComboBox<String> choiceBox = new JComboBox<String>();
	private JSlider slider = new JSlider();
	
	public UIDriver()
	{
		setSize(SCREEN_SIZE.width/2, SCREEN_SIZE.height/2);
		setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH );
		setVisible(true);
		setDefaultCloseOperation(UIDriver.DO_NOTHING_ON_CLOSE);
		setTitle("Image Cow");

		init();
	}

	/* Our gui has a menu bar, a workbench, a Find Similar images panel, and a
	 * Compare images panel */
	public void init()
	{
		v = new FileTreeViewer();
		v.validate();
		m = v.getCheckTreeManager().getSelectionModel();
		
		try
		{
			Image cow1 = ImageIO.read(UIDriver.class.getResource(
				"images/cow_large.png"));
			Image cow2 = ImageIO.read(UIDriver.class.getResource(
				"images/cow_small.png"));
			setIconImages(Arrays.asList(new Image[]{cow1, cow2}));
			cow = new ImageIcon(cow2);
			
		} catch(Exception e)
		{
			cow = null;
		};

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				if (confirmExit())
					System.exit(0);
			}
		});
		
		comparer = M;
		threshold = 0.5;
		
		ff = createImageFilter();
		c = createImageChooser();
		
		h = new HistogramComparer();
		k = new KeypointComparer();
		pH = new PHashComparer();
		s = new SetComparer();

		customizePanel = createCustomizePanel();
		
		setJMenuBar(createMenuBar());
		setContentPane(createSplitPane());
		getContentPane().requestFocus();
		
		// Tool tips display image names upon mouse over
		ToolTipManager.sharedInstance().setInitialDelay(50);
		
		initRootDirectories();
	}
	
	public void initRootDirectories()
	{
		if (JOptionPane.showConfirmDialog(UIDriver.this, v,
				"Where are your images?", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, cow) != JOptionPane.OK_OPTION)
			System.exit(0);
		rootPaths = m.getSelectionPaths();
	}

	// There is a File menu (for opening images) and a Help menu
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

		menuItem = new JMenuItem("Open Image...");
		menuItem.getAccessibleContext().setAccessibleDescription(
			"Add an image to the workbench");
		
		// Uses a JFileChooser to add an image to the workbench
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				c.setMultiSelectionEnabled(true);
				if (c.showOpenDialog(UIDriver.this) ==
					JFileChooser.APPROVE_OPTION)
				{
					for (File f : c.getSelectedFiles())
						if (!setFirstAvailableLabel(f, true))
							break;
				}
				c.setMultiSelectionEnabled(false);
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Clear Workbench");
		menuItem.getAccessibleContext().setAccessibleDescription(
			"Remove all images from the workbench");
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				for (JLabel label : labels)
					resetLabel(label);
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Exit");
		menuItem.getAccessibleContext().setAccessibleDescription(
			"Exit Image Cow");
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (confirmExit())
					System.exit(0);
			}
		});
		menu.add(menuItem);
		
		menu = new JMenu("Find Similar");
		menu.setMnemonic(KeyEvent.VK_S);
		menu.getAccessibleContext().setAccessibleDescription("Find menu");
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Select Image...");
		menuItem.getAccessibleContext().setAccessibleDescription(
			"Add an image to the <Find Similar> panel");
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				lSimilar.getMouseListeners()[0].mouseClicked(null);
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Change Directories...");
		menuItem.getAccessibleContext().setAccessibleDescription(
			"Customize which directories are searchable");
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (JOptionPane.showConfirmDialog(UIDriver.this, v,
					"Select directories", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE, cow) ==
					JOptionPane.CANCEL_OPTION)
				{
					m.removeSelectionPaths(m.getSelectionPaths());
					m.addSelectionPaths(rootPaths);
				}
				else
				{
					rootPaths = m.getSelectionPaths();
					
					// We don't want the comparers to store old pixel array IDs
					h = new HistogramComparer();
					k = new KeypointComparer();
					pH = new PHashComparer();
					s = new SetComparer();
				}
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Customize Comparer ...");
		menuItem.getAccessibleContext().setAccessibleDescription(
			"Customize the comparison criteria");
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				choiceBox.setSelectedIndex(comparer);
				slider.setValue((int) (threshold*100));
				
				if (JOptionPane.showConfirmDialog(UIDriver.this,
					customizePanel, "Customize comparison tool",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
					cow) == JOptionPane.OK_OPTION)
				{
					comparer = choiceBox.getSelectedIndex();
					threshold = (double) slider.getValue() / 100.;
				}
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Go!");
		menuItem.getAccessibleContext().setAccessibleDescription("Find!");
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				b1.getActionListeners()[0].actionPerformed(e);
			}
		});
		menu.add(menuItem);
		
		menu = new JMenu("Compare");
		menu.setMnemonic(KeyEvent.VK_C);
		menu.getAccessibleContext().setAccessibleDescription("Compare menu");
		menuBar.add(menu);
		
		menuItem = new JMenuItem("Select Image 1...");
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Add an image to the <Compare> panel");
			menuItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					lCompare1.getMouseListeners()[0].mouseClicked(null);
				}
			});
			menu.add(menuItem);
			
		menuItem = new JMenuItem("Select Image 2...");
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Add an image to the <Compare> panel");
			menuItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					lCompare2.getMouseListeners()[0].mouseClicked(null);
				}
			});
			menu.add(menuItem);
			
		menuItem = new JMenuItem("Go!");
		menuItem.getAccessibleContext().setAccessibleDescription("Compare!");
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				b2.getActionListeners()[0].actionPerformed(e);
			}
		});
		menu.add(menuItem);

		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menu.getAccessibleContext().setAccessibleDescription("Help menu");
		menuBar.add(menu);

		menuItem = new JMenuItem("Contents");
		menuItem.getAccessibleContext().setAccessibleDescription("Contents");
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JSplitPane p = new JSplitPane();
				p.setOrientation(JSplitPane.VERTICAL_SPLIT);
				
				JTextArea t = createTextArea();
				t.setPreferredSize(new Dimension(400,50));
				t.setMinimumSize(ZERO);
				p.setBottomComponent(t);
				
				HelpTree h = new HelpTree(t);
				h.setMinimumSize(ZERO);
				p.setTopComponent(h);
				
				JOptionPane.showMessageDialog(UIDriver.this, p, "Help",
					JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menu.add(menuItem);

		return menuBar;
	}

	private JPanel createCustomizePanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2,2));
		
		panel.add(new JLabel("Choose comparer: ", JLabel.CENTER));
		
		String[] choices = new String[]{"Quadratic mean",
			"Histogram comparison", "Keypoint matching",
			"Perceptual hash", "Set resemblance"};
		choiceBox = new JComboBox<String>(choices);
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		p.add(choiceBox);
		panel.add(p);
		
		panel.add(new JLabel("Adjust threshold: ", JLabel.CENTER));
		
		slider = new JSlider();
		slider.setMajorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		panel.add(slider);
		
		return panel;
	}
	
	/* The left pane is the workbench, the right pane is a split pane that
	 * contains the Find Similar and Compare panels */
	private JSplitPane createSplitPane()
	{
		JSplitPane p = new JSplitPane();
		p.setResizeWeight(1.);
		p.setLeftComponent(createWorkbench());

		JSplitPane pSub = createComparisonPane();
		p.setRightComponent(pSub);

		return p;
	}

	// Creates the workbench
	private JPanel createWorkbench()
	{
		JPanel p = new JPanel()
		{
			private static final long serialVersionUID = 1L;

			// Writes faint instructions across the top of the workbench
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				g.setColor(new Color(200,200,200));
				g.setFont(new Font(g.getFont().getName(), Font.ITALIC, 50));
				g.drawString("Add images to workbench", 20, 50);
			}
		};
		
		// Adds 25 labels that will be used to display images
		p.setMinimumSize(ZERO);
		p.setLayout(new GridLayout(ROW_SIZE, ROW_SIZE));
		labels = createImageLabels();
		for (JLabel label : labels)
			p.add(label);
		
		// If workbench is resized, recompute image sizes
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
							setIcon(
								label, new File(label.getName()), true, true);
						} catch (Exception ex) {};
					}
				}
			}
		});
		
		return p;
	}

	// Creates the workbench labels that will hold images
	private JLabel[] createImageLabels()
	{
		JLabel[] labels = new JLabel[ROW_SIZE*ROW_SIZE];
		for (int i = 0; i < labels.length; ++i)
			labels[i] = createImageLabel();
		return labels;
	}

	// Adds the Find Similar and Compare panels to a split pane on the right
	private JSplitPane createComparisonPane()
	{
		JSplitPane p = new JSplitPane();
		p.setMinimumSize(ZERO);
		p.setResizeWeight(0.5);
		p.setLeftComponent(createSimilarImagesFinder());
		p.setRightComponent(createPairwiseComparer());
		return p;
	}

	// Creates the Find Similar panel
	private JPanel createSimilarImagesFinder()
	{
		JPanel p = new JPanel();
		p.setMinimumSize(ZERO);
		p.setLayout(new BorderLayout());

		b1 = new JButton("Find Similar");
		b1.setFont(b1.getFont().deriveFont(Font.BOLD));
		b1.setPreferredSize(new Dimension(200, 40));

		// Controls consist of a Find Similar button and a Select Image label
		JPanel controls = new JPanel();
		controls.setLayout(new BorderLayout());
		controls.add(b1, BorderLayout.NORTH);
		controls.add(lSimilar = createSelectionLabel(FIND_SIMILAR),
			BorderLayout.SOUTH);
		p.add(controls, BorderLayout.NORTH);

		JScrollPane textScroll = new JScrollPane();
		textScroll.getViewport().add(t1);
		p.add(textScroll, BorderLayout.CENTER);

		b1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				MyImage im;
				try
				{
					im = new MyImage(fSimilar);
				} catch (Exception ex)
				{
					showWarning(ex.getMessage());
					return;
				}
				try
				{
					im.setSize(32, 32);
				} catch (Exception ex)
				{
					showWarning("Could not read " + fSimilar.getName());
					return;
				}
				
				t1.setText("\nFinding similar images to " + fSimilar.getName()
					+ "\n");
				
				pM = new ProgressMonitor(UIDriver.this, "Finding images", "",
					0, 100);
				pM.setProgress(0);
				
				final DirectorySearcher d = new DirectorySearcher(
					rootPaths, im);
				d.addPropertyChangeListener(new PropertyChangeListener()
				{
					public void propertyChange(PropertyChangeEvent e)
					{
						if (e.getPropertyName().equals("progress"))
						{
							int progress = (Integer) e.getNewValue();
							pM.setProgress(progress);
							
							if (pM.isCanceled())
							{
								d.cancel(true);
								b1.setEnabled(true);
				            }
						}
					}
					
				});
				d.execute();
				b1.setEnabled(false);
			}
		});

		return p;
	}

	class DirectorySearcher extends SwingWorker<Void, Void>
	{
		private TreePath[] paths;
		private MyImage source;
		private ArrayList<ComparableResult> results;
		
		public DirectorySearcher(TreePath[] paths, MyImage source)
		{
			this.paths = paths;
			this.source = source;
			results = new ArrayList<ComparableResult>();
		}
		
		protected Void doInBackground() throws Exception
		{
			for (TreePath p : paths)
			{
				DefaultMutableTreeNode n = (DefaultMutableTreeNode)
					p.getLastPathComponent();
				File f;
				
				if (n.getUserObject().toString().equals("Computer"))
					f = new File("/");
				else
					f = ((FileNode) ((IconData) n.getUserObject()).
						getObject()).getFile();
				
				searchDirectory(f, source);
			}

			Collections.sort(results, Collections.reverseOrder());
			for (ComparableResult r : results)
			{
				String score = Double.toString(r.getScore());
				score = score.substring(0, Math.min(score.length(), 4));
				t1.append("\n" + r.getFilename() + " (" + score + ")");
			}
			
			return null;
		}
		
		public void done()
		{
			pM.close();
			b1.setEnabled(true);
		}
		
		private void searchDirectory(File root, MyImage source)
		{
			int counter = 0;
			Stack<File> stack = new Stack<File>();
			stack.push(root);
			while (!stack.isEmpty())
			{
				if (pM.isCanceled())
					return;
				
				File f = stack.pop();
				File[] files = f.listFiles(ff);
				if (files == null)
					continue;
				pM.setNote(f.getPath());
				for (File subFile : files)
				{
					if (pM.isCanceled())
						return;
					if (subFile.isDirectory())
						stack.push(subFile);
					else
					{
						if (counter++ >= 20)
						{
							counter = 0;
							if (getProgress() >= 99)
								setProgress(0);
							else
								setProgress(getProgress()+1);
						}
						
						MyImage target;
						try
						{
							target = new MyImage(subFile);
							target.setSize(32, 32);
						} catch (Exception ex)
						{
							continue;
						}
						double score = compare(source.toPixelArray(),
							target.toPixelArray(), comparer);
						if (score > threshold)
						{
							String strPath = subFile.getPath();
							strPath = strPath.substring(Math.max(0,
								strPath.indexOf(f.getName())));
							results.add(new ComparableResult(strPath, score));
						}
					}
				}
			}
		}
		
		class ComparableResult implements Comparable<Object>
		{
			private String filename;
			private double score;
			
			public ComparableResult(String filename, double score)
			{
				this.filename = filename;
				this.score = score;
			}
			
			public int compareTo(Object o)
			{
				ComparableResult other = (ComparableResult) o;
				if (this.score < other.score)
					return -1;
				else if (this.score == other.score)
					return 0;
				return 1;
			}
			
			public String getFilename()
			{
				return filename;
			}
			
			public double getScore()
			{
				return score;
			}
		}
	}
	
	// Creates the Compare panel
	private JPanel createPairwiseComparer()
	{
		JPanel p = new JPanel();
		p.setMinimumSize(ZERO);
		p.setLayout(new BorderLayout());

		b2 = new JButton("Compare");
		b2.setFont(b2.getFont().deriveFont(Font.BOLD));
		b2.setPreferredSize(new Dimension(200, 40));

		// Controls consist of a Compare button and two Select Image labels
		JPanel controls = new JPanel();
		controls.setLayout(new BorderLayout());
		controls.add(b2, BorderLayout.NORTH);
		controls.add(lCompare1 = createSelectionLabel(COMPARE_FIRST),
			BorderLayout.CENTER);
		controls.add(lCompare2 = createSelectionLabel(COMPARE_SECOND),
			BorderLayout.SOUTH);
		p.add(controls, BorderLayout.NORTH);

		p.add(t2, BorderLayout.CENTER);

		// Compares two user-selected images
		b2.addActionListener(new ActionListener()
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
					showWarning(ex.getMessage());
					return;
				}
				try
				{
					im1.setSize(32, 32);
					im2.setSize(32, 32);
				} catch (Exception ex)
				{
					showWarning("Could not read one or both image files");
					return;
				}

				PixelArray p1 = im1.toPixelArray();
				PixelArray p2 = im2.toPixelArray();
				
				t2.setText("\nHistogram comparison: " + compare(p1, p2, H) +
					"\n\nKeypoint matching: " + compare(p1, p2, K) +
					"\n\nPerceptual hash: " + compare(p1, p2, PH) +
					"\n\nSet resemblance: " + compare(p1, p2, S));
				
				double score = compare(p1, p2, M);
				
				t2.append("\n\nOverall similarity: " + score);
			}

		});

		return p;
	}

	// Creates a Select Image label
	private JLabel createSelectionLabel(final int mode)
	{
		final JLabel l = new JLabel("Select Image");
		l.setPreferredSize(new Dimension(200, 40));
		l.setOpaque(true);
		l.setBackground(Color.BLACK);
		l.setFont(l.getFont().deriveFont(Font.BOLD));
		l.setHorizontalAlignment(SwingConstants.CENTER);
		
		// Mouse click brings up a file chooser
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
					
					setFirstAvailableLabel(f, false);
					
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

	/*
	 * Sets the icon of the first empty image label.  Returns false if there
	 * are no empty image labels remaining.
	 */
	private boolean setFirstAvailableLabel(File f, boolean showWarnings)
	{
		int emptyIndex = -1;
		for (int i = 0; i < labels.length; ++i)
			if (labels[i].getIcon() == null)
			{
				emptyIndex = i;
				break;
			}
		if (emptyIndex < 0)
		{
			if (showWarnings)
				showWarning("Workbench is full");
			return false;
		}
		setIcon(labels[emptyIndex], f, false, showWarnings);
		return true;
	}
	
	// Sets the tool tip text to the image in the file parameter
	private String getToolTipText(File f)
	{
		try
		{
			Image im = ImageIO.read(f);
		
			// Computes a good thumbnail size
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
		} catch(Exception ex)
		{
			return "No image";
		}
	}
	
	// Creates a file chooser
	private JFileChooser createImageChooser()
	{
		JFileChooser c = new JFileChooser();
		c.setDragEnabled(true);
		c.setDialogTitle("Open image");
		c.setAcceptAllFileFilterUsed(false);
		c.setFileFilter(new FileNameExtensionFilter(
			"Image file", "png", "gif", "jpg", "tif", "bmp"));
		c.setAccessory(new ImagePreview(c));
		return c;
	}

	private FileFilter createImageFilter()
	{
		return new FileFilter()
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
		};
	}
	
	// Creates a text area
	private JTextArea createTextArea()
	{
		JTextArea t = new JTextArea();
		t.setEditable(false);
		t.setLineWrap(true);
		t.setWrapStyleWord(true);
		return t;
	}

	// Checks whether an image is in the workbench
	private boolean openedImagesContains(String filename)
	{
		for (JLabel label : labels)
			if (label.getName() != null && label.getName().equals(filename))
				return true;
		return false;
	}
	
	// Creates a workbench label for displaying an image
	private JLabel createImageLabel()
	{
		final JLabel label = new JLabel();
		final JPopupMenu menu = createPopupMenu(label);
		
		/* Clicking on an empty label brings up a file chooser, and double-
		 * clicking on a label with an image brings up a menu in-place*/
		label.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (label.getIcon() == null)
					if (c.showOpenDialog(UIDriver.this) ==
							JFileChooser.APPROVE_OPTION)
						setIcon(label, c.getSelectedFile(), false, true);
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
	
	/* Sets the image on the specified label. The modifying flag indicates
	 * whether we are loading a new image into the workbench or resizing an
	 * existing one. */
	private void setIcon(JLabel label, File f, boolean modifying,
		boolean showWarnings)
	{
		if (!modifying && openedImagesContains(f.getPath()))
		{
			if (showWarnings)
				showWarning(f.getName() + " is in the workbench");
			return;
		}
		
		ImageIcon imOriginal;
		try
		{
			imOriginal = new ImageIcon(ImageIO.read(f));
		} catch(Exception ex)
		{
			if (showWarnings)
				showWarning("Cannot open " + f.getName());
			return;
		};
		
		// Fits the image to the label size
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
	
	// Creates the pop-up menu associated with each workbench label
	private JPopupMenu createPopupMenu(final JLabel label)
	{
		final JPopupMenu menu = new JPopupMenu();
		
		JMenuItem item = new JMenuItem("Remove image");
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				resetLabel(label);
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
					setIcon(label, c.getSelectedFile(), false, true);
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
	
	// Clears an image label
	private void resetLabel(JLabel label)
	{
		label.setName(null);
		label.setIcon(null);
		label.setToolTipText(null);
	}
	
	// Heuristic for computing an overall similarity score between two images.
	private double compare(PixelArray a1, PixelArray a2, int comparer)
	{
		if (comparer == H)
			return h.compare(a1, a2);
		else if (comparer == K)
			return k.compare(a1, a2);
		else if (comparer == PH)
			return pH.compare(a1, a2);
		else if (comparer == S)
			return s.compare(a1, a2);
		
		double hScore = h.compare(a1, a2);
		double kScore = k.compare(a1, a2);
		double pHScore = pH.compare(a1, a2);
		double sScore = s.compare(a1, a2);
		
		if (sScore > 0.9)
			return sScore;
		
		return Math.sqrt((1./3.)*
			(hScore*hScore + kScore*kScore + pHScore*pHScore));
	}
	
	private void showWarning(String warning)
	{
		JOptionPane.showMessageDialog(UIDriver.this, warning, "Error",
			JOptionPane.WARNING_MESSAGE);
	}
	
	private boolean confirmExit()
	{
		return JOptionPane.showConfirmDialog(UIDriver.this, "Exit Image Cow?",
			"Confirm Exit", JOptionPane.YES_NO_CANCEL_OPTION) ==
			JOptionPane.YES_OPTION;
	}
	
	public static void main(String[] args) throws Exception
	{
		try
		{
			// Uses a look and feel from the Substance library
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