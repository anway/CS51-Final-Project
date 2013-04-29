package graphics;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Scanner;

import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/* A tree view of a user manual.  Populated from helpContents.txt */
public class HelpTree extends JTree
{
	private static final long serialVersionUID = 1L;
	
	private LinkedHashMap<String, String> m;
	private JTextArea a;

	public HelpTree(JTextArea a)
	{
		super(new MyTreeNode(new NodeInfo("Overview")));
		this.a = a;
		init();
	}
	
	public void init()
	{
		getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION);
		initHashtable();
		addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					getLastSelectedPathComponent();
				if (node == null)
					return;
				Object info = node.getUserObject();
				if (info instanceof NodeInfo)
				{
					String content = m.get(((NodeInfo) info).header);
					a.setText(content);
				}
			}
			
		});
		createNodes();
		expandPath(new TreePath(((DefaultMutableTreeNode)getModel().getRoot()).
			getPath()));
	}
	
	private void initHashtable()
	{
		m = new LinkedHashMap<String, String>();
		InputStream s = HelpTree.class.getResourceAsStream(
			"files/helpContents.txt");
		Scanner in = new Scanner(s);
		
		while (in.hasNextLine())
		{
			String line = in.nextLine();
			int splitIndex = line.indexOf(":");
			String head = line.substring(0, splitIndex).trim();
			String tail = line.substring(splitIndex+1).trim();
			while (tail.contains("+"))
			{
				tail = tail.substring(0, tail.length()-1) + "\n" +
					in.nextLine().trim();
			}
			m.put(head, tail);
		}
		
		in.close();
	}
	
	private void createNodes()
	{
		DefaultMutableTreeNode top = (DefaultMutableTreeNode)
			getModel().getRoot();
		for (String s : m.keySet())
		{
			if (s.equals("Overview"))
				continue;
			if (!s.contains("/"))
				top.add(new MyTreeNode(new NodeInfo(s)));
			else
			{
				String parent = s.substring(0, s.indexOf("/"));
				int count = top.getChildCount();
				for (int i = 0; i < count; ++i)
				{
					MyTreeNode n = (MyTreeNode) top.getChildAt(i);
					if (n.toString().equals(parent))
					{
						n.add(new MyTreeNode(new NodeInfo(s)));
						break;
					}
				}
			}
		}
	}
	
	static class NodeInfo
	{
		private String header;
		
		public NodeInfo(String header)
		{
			this.header = header;
		}
		
		public String toString()
		{
			return header.substring(header.indexOf("/")+1);
		}
	}
	
	static class MyTreeNode extends DefaultMutableTreeNode
	{
		private static final long serialVersionUID = 1L;
		
		public MyTreeNode(NodeInfo o)
		{
			super(o);
		}
		
		public String toString()
		{
			return ((NodeInfo) getUserObject()).toString();
		}
	}
}
