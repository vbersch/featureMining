package featureMining.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import featureMining.feature.Feature;
import featureMining.feature.FeatureContainer;
import featureMining.ui.listener.GuiListener;

// TODO: Auto-generated Javadoc
/**
 * The Class RootFeatureWindow.
 */
public class RootFeatureWindow extends JFrame implements ActionListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private JPanel editBox;
	
	/** The main container. */
	private JPanel mainContainer;

	/** The url field. */
	private JTextField urlField;

	/** The feature list. */
	private JList featureList;

	/** The info text area. */
	private JTextArea infoTextArea;

	/** The desc text area. */
	private JTextArea descTextArea;
	
	private JTextField newNameField;
	
	public JTextField getNewNameField() {
		return newNameField;
	}

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

	private int featureFound;
	private int currentFeatureNum;

	private JTextArea evalTextArea;

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

		featureList = new JList(listModel);
		featureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// list.setSelectedIndex(0);
		featureList.setVisibleRowCount(5);
		JScrollPane listScrollPane = new JScrollPane(featureList);
		featureList.addListSelectionListener(guiListener);

//		JList list2 = new JList(listModel2);
//		list2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		list2.setVisibleRowCount(5);
//		JScrollPane listScrollPane2 = new JScrollPane(list2);

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
		
		this.editBox = this.createEditBox();
		editBox.setVisible(false);
		listPane.add(listScrollPane);
		listPane.add(Box.createHorizontalStrut(20));
		listPane.add(editBox);
		listPane.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
		
		//listPane.setMaximumSize(new Dimension(1000,1000));
		
		JTabbedPane infoTabs = new JTabbedPane();

		JScrollPane infoPane = createInfoTextArea("Info");
		JScrollPane descPane = createDescTextArea("Description");
		JScrollPane evalPane = createEvalTextArea("Evaluation");

		infoTabs.addTab("Info", infoPane);
		infoTabs.addTab("Description", descPane);
		infoTabs.addTab("Evaluation", evalPane);
		
		listScrollPane.setPreferredSize(new Dimension(250 , 200));
		infoTabs.setMinimumSize(new Dimension(300 , 200));
		infoTabs.setPreferredSize(new Dimension(400 , 300));
		listPane.setPreferredSize(new Dimension(500 , 400));
		
		contentPane.add(listPane);
		contentPane.add(infoTabs);

		mainContainer.add(contentPane, BorderLayout.CENTER);
		mainContainer.add(buttonPane, BorderLayout.PAGE_END);

		this.createMenu();

	}

	public JTextArea getEvalTextArea() {
		return evalTextArea;
	}

	private JScrollPane createEvalTextArea(String text) {
		this.evalTextArea = new JTextArea();
		evalTextArea.setEditable(false);
		evalTextArea.setText(text);

		JScrollPane scrollPane = new JScrollPane(evalTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		return scrollPane;
	}

	public int getFeatureFound() {
		return featureFound;
	}

	public void setFeatureFound(int featureFound) {
		this.featureFound = featureFound;
	}

	public int getCurrentFeatureNum() {
		return currentFeatureNum;
	}

	public void setCurrentFeatureNum(int currentFeatureNum) {
		this.currentFeatureNum = currentFeatureNum;
	}

	private JPanel createEditBox() {
		JPanel editBox = new JPanel(new BorderLayout());
		//editBox.setMaximumSize(new Dimension(50,50));
		
		JButton changeButton = new JButton("Change");
		changeButton.addActionListener(guiListener);
		changeButton.setName("changeButton");
		
		JButton deleteButton = new JButton("Delete Feature");
		deleteButton.setBackground(Color.RED);
		deleteButton.addActionListener(guiListener);
		deleteButton.setName("deleteButton");
		
		newNameField = new JTextField(10);
		newNameField.setText("Enter new Name");
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane , BoxLayout.LINE_AXIS));
		buttonPane.add(newNameField);
		buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(changeButton);
		buttonPane.add(deleteButton);
		buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		editBox.add(buttonPane , BorderLayout.PAGE_END);
		return editBox;
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
		UiWorker uiWorker = new UiWorker(urlField.getText(), infoTextArea, "");
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

		this.updateFeatureList();
		
		this.featureFound = featureContainer.getFeatureStorage().size();
		this.currentFeatureNum = this.featureFound;
		int linkNum = featureContainer.getLinkNum();
		
		String text = "Found "
				+ featureFound
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

	public JPanel getEditBox() {
		return this.editBox;
	}

	public void updateFeatureList() {
		DefaultListModel model = (DefaultListModel) this.featureList.getModel();
		model.clear();
		
		List<Feature> sortedFeatures = new ArrayList<Feature>(featureContainer.getFeatureStorage().values());
		Collections.sort(sortedFeatures);
		
		for (Feature feature : sortedFeatures) {
			model.addElement(feature.getName());
		}
	}

	public void updateEvalTextArea() {
		this.currentFeatureNum--;
		float prec = (float)currentFeatureNum/(float)featureFound;
		String text = "";
		text += currentFeatureNum + " of " + this.featureFound + " correctly found";
		text += "\nPrecision: " + prec;
		evalTextArea.setText(text);
		
	}
}
