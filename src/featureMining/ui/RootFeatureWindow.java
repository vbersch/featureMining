package featureMining.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import featureMining.FeatureMining;
import featureMining.processing.feature.FeatureContainer;
import featureMining.ui.listeners.GuiListener;

// TODO: Auto-generated Javadoc
/**
 * The Class RootFeatureWindow.
 */
public class RootFeatureWindow extends JFrame implements ActionListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The main container. */
	private JPanel mainContainer;

	/** The url field. */
	private JTextField urlField;

	/** The feature list. */
	private JList featureList;

	/** The info pane. */
	private JScrollPane infoPane;

	/** The desc pane. */
	private JScrollPane descPane;

	/** The info text area. */
	private JTextArea infoTextArea;

	/** The desc text area. */
	private JTextArea descTextArea;

	/**
	 * Gets the feature container.
	 *
	 * @return the feature container
	 */
	public FeatureContainer getFeatureContainer() {
		return featureContainer;
	}

	/** The feature container. */
	private FeatureContainer featureContainer;

	/** The menu bar. */
	private JMenuBar menuBar;

	/** The gui listener. */
	private GuiListener guiListener;

	/**
	 * Instantiates a new root feature window.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public RootFeatureWindow() {
		this.setTitle("Feature Mining");
		this.setSize(800, 600);
		this.setVisible(true);
		this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);

		mainContainer = new JPanel(new BorderLayout());
		guiListener = new GuiListener();

		DefaultListModel listModel = new DefaultListModel();
		DefaultListModel listModel2 = new DefaultListModel();

		featureList = new JList(listModel);
		featureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// list.setSelectedIndex(0);
		featureList.setVisibleRowCount(5);
		JScrollPane listScrollPane = new JScrollPane(featureList);
		featureList.addListSelectionListener(guiListener);

		JList list2 = new JList(listModel2);
		list2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list2.setVisibleRowCount(5);
		JScrollPane listScrollPane2 = new JScrollPane(list2);

		JButton fireButton = new JButton("mine URL");
		fireButton.setEnabled(true);
		fireButton.addActionListener(this);

		urlField = new JTextField(10);
		//urlField.setText("https://github.com/radiant/radiant/wiki");
		urlField.setText("http://mixxx.org/manual/latest/");
		// Create a panel for the url
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.add(urlField);
		buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(fireButton);
		buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.LINE_AXIS));

		listPane.add(listScrollPane);
		listPane.add(Box.createHorizontalStrut(20));
		listPane.add(listScrollPane2);
		listPane.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

		JTabbedPane infoTabs = new JTabbedPane();

		this.infoPane = createInfoTextArea("Info");
		this.descPane = createDescTextArea("Description");

		infoTabs.addTab("Info", infoPane);
		infoTabs.addTab("Description", descPane);

		contentPane.add(listPane);
		contentPane.add(infoTabs);

		mainContainer.add(contentPane, BorderLayout.CENTER);
		mainContainer.add(buttonPane, BorderLayout.PAGE_END);

		this.createMenu();

	}

	/**
	 * Creates the info text area.
	 *
	 * @param text
	 *            the text
	 * @return the j scroll pane
	 */
	private JScrollPane createInfoTextArea(String text) {

		this.infoTextArea = new JTextArea();
		infoTextArea.setEditable(false);
		infoTextArea.setText(text);

		JScrollPane scrollPane = new JScrollPane(infoTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		return scrollPane;
	}

	/**
	 * Creates the desc text area.
	 *
	 * @param text
	 *            the text
	 * @return the j scroll pane
	 */
	private JScrollPane createDescTextArea(String text) {

		this.descTextArea = new JTextArea();
		descTextArea.setEditable(false);
		descTextArea.setText(text);

		JScrollPane scrollPane = new JScrollPane(descTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		return scrollPane;
	}

	/**
	 * Creates the menu.
	 */
	private void createMenu() {
		// create the Menu
		menuBar = new JMenuBar();
		JMenu menu = new JMenu("Options");
		JMenuItem xmlExport = new JMenuItem("Export to XML");
		JMenuItem urlsList = new JMenuItem("Get Urls from List");
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(guiListener);
		exit.setName("exit");

		menu.add(xmlExport);
		menu.add(urlsList);
		menu.add(exit);

		JMenu helpMenu = new JMenu("Help");

		menuBar.add(menu);
		menuBar.add(helpMenu);

		this.setJMenuBar(menuBar);
		this.setContentPane(mainContainer);
		this.setVisible(true);

	}

	/**
	 * Creates the inner panel.
	 *
	 * @param text the text
	 * @return the j panel
	 */
	protected JPanel createInnerPanel(String text) {
		JPanel jplPanel = new JPanel();
		JLabel jlbDisplay = new JLabel(text);
		jlbDisplay.setHorizontalAlignment(JLabel.CENTER);
		jplPanel.setLayout(new GridLayout(1, 1));
		jplPanel.add(jlbDisplay);
		return jplPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e){
		UiWorker uiWorker = new UiWorker(urlField.getText(), infoTextArea);
		uiWorker.execute();
	}

	/**
	 * Sets the content.
	 *
	 * @param featureContainer
	 *            the new content
	 */
	public void setContent(FeatureContainer featureContainer) {
		this.featureContainer = featureContainer;

		DefaultListModel model = (DefaultListModel) this.featureList.getModel();
		for (String key : featureContainer.getFeatureStorage().keySet()) {
			model.addElement(key);
		}
		
		int linkNum = featureContainer.getLinkNum();
		
		String text = "Found "
				+ this.featureContainer.getFeatureStorage().size()
				+ " Features";
		if(linkNum > 0){
			text += "\nParsed " + linkNum + " Links";
		}
		
		text += "\nSelect a Feature for further Information and Editing";
		this.infoTextArea.setText(text);

	}

	/**
	 * Gets the info text area.
	 *
	 * @return the info text area
	 */
	public JTextArea getInfoTextArea() {
		return infoTextArea;
	}

	/**
	 * Gets the desc text area.
	 *
	 * @return the desc text area
	 */
	public JTextArea getDescTextArea() {
		return descTextArea;
	}

	/**
	 * Gets the feature list.
	 *
	 * @return the feature list
	 */
	public JList getFeatureList() {
		return featureList;
	}

	public void addInfoTextLine(String newText) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				infoTextArea.append(newText);
			}
		});		
	}

	public void setInfoText(String string) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				infoTextArea.setText(string);
			}
		});
	}
}
