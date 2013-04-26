package graphics;

import image_comparer.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
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

	private JFileChooser c;
	private File fSimilar = null;
	private File fCompare1 = null, fCompare2 = null;

	public UIDriver()
	{
		setSize(SCREEN_SIZE.width/2, SCREEN_SIZE.height/2);
		setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH );
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
				"Change the root folder");
		menu.add(menuItem);

		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menu.getAccessibleContext().setAccessibleDescription("Help menu");
		menuBar.add(menu);

		menuItem = new JMenuItem("Contents");
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Help contents");
		menu.add(menuItem);

		menuItem = new JMenuItem("About");
		menuItem.getAccessibleContext().setAccessibleDescription(
				"About");
		menu.add(menuItem);

		return menuBar;
	}

	private JSplitPane createSplitPane()
	{
		JSplitPane p = new JSplitPane();
		p.setResizeWeight(1.);
		p.setLeftComponent(createWorkbench());
		p.setRightComponent(createComparisonPane());
		return p;
	}

	private JPanel createWorkbench()
	{
		return new JPanel();
	}

	private JSplitPane createComparisonPane()
	{
		JSplitPane p = new JSplitPane();
		p.setResizeWeight(0.5);
		p.setLeftComponent(createSimilarImagesFinder());
		p.setRightComponent(createPairwiseComparer());
		return p;
	}

	private JPanel createSimilarImagesFinder()
	{
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		
		JButton b = new JButton("Find Similar");
		b.setFont(b.getFont().deriveFont(Font.BOLD));
		b.setPreferredSize(new Dimension(200, 40));

		JPanel controls = new JPanel();
		controls.setLayout(new BorderLayout());
		controls.add(b, BorderLayout.NORTH);
		controls.add(createSelectionLabel(FIND_SIMILAR), BorderLayout.SOUTH);
		p.add(controls, BorderLayout.NORTH);
		
		final JTextArea t = createTextArea();
		p.add(t, BorderLayout.CENTER);
		
		b.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					new MyImage(fSimilar);
				} catch (Exception ex)
				{
					t.setText("\n" + ex.getMessage());
					return;
				}
				t.setText("\nComing soon!");
			}
		});
		
		return p;
	}

	private JPanel createPairwiseComparer()
	{
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		
		JButton b = new JButton("Compare");
		b.setFont(b.getFont().deriveFont(Font.BOLD));
		b.setPreferredSize(new Dimension(200, 40));

		JPanel controls = new JPanel();
		controls.setLayout(new BorderLayout());
		controls.add(b, BorderLayout.NORTH);
		controls.add(createSelectionLabel(COMPARE_FIRST), BorderLayout.CENTER);
		controls.add(createSelectionLabel(COMPARE_SECOND), BorderLayout.SOUTH);
		p.add(controls, BorderLayout.NORTH);
		
		final JTextArea t = createTextArea();
		p.add(t, BorderLayout.CENTER);
		
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
					t.setText("\n" + ex.getMessage());
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
				
				t.setText("\nHistogram comparison: " + h.compare(p1,p2) +
					"\n\nKeypoint matching: " + k.compare(p1,p2) +
					"\n\nPerceptual hash: " + pH.compare(p1,p2) +
					"\n\nSet resemblance: " + s.compare(p1,p2));
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

	private JFileChooser createImageChooser()
	{
		JFileChooser c = new JFileChooser();
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
		t.setWrapStyleWord(true);
		t.setLineWrap(true);
		return t;
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
