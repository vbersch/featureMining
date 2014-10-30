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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import featureMining.feature.Feature;
import featureMining.feature.FeatureContainer;
import featureMining.feature.OptionTransferObject;
import featureMining.persistence.IPersistenceHandler;
import featureMining.persistence.SettingsManager;
import featureMining.ui.listener.GuiActionListener;
import featureMining.ui.listener.GuiItemListener;
import featureMining.ui.listener.GuiListListener;


/**
 * The Class RootFeatureWindow.
 */
public class RootFeatureWindow extends JFrame implements ActionListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private OptionWindow optionFrame;
	
	private JPanel editBox;
	
	/** The main container. */
	private JPanel mainContainer;

	/** The url field. */
	private JTextField urlField;

	/** The feature list. */
	private JList<String> featureList;
	
	private JList<String> labelChoosingList;
	
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

	/** The gui listeners. */
	private GuiActionListener guiActionListener;
	private GuiListListener guiListListener;
	private GuiItemListener guiItemListener;

	private int featureFound;
	private int currentFeatureNum;

	private JTextArea evalTextArea;
	private IPersistenceHandler persistenceHandler;
	
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
		guiActionListener = new GuiActionListener();
		guiListListener = new GuiListListener();
		guiItemListener = new GuiItemListener();

		DefaultListModel listModel = new DefaultListModel();

		featureList = new JList(listModel);
		featureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		featureList.setVisibleRowCount(5);
		JScrollPane listScrollPane = new JScrollPane(featureList);
		featureList.addListSelectionListener(guiListListener);
		featureList.setName("featureList");

		JButton fireButton = new JButton("mine URL");
		fireButton.setEnabled(true);
		fireButton.addActionListener(this);

		urlField = new JTextField(10);
		OptionTransferObject settings = SettingsManager.restoreOptions();
		String url="";
		//urlField.setText("https://github.com/radiant/radiant/wiki");
		if(settings != null){
			url = settings.getBaseUrl();
		}
		urlField.setText(url);
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
		//listPane.add(Box.createVerticalStrut(20));
		listPane.add(editBox);
		listPane.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
		
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

	public GuiListListener getGuiListListener() {
		return guiListListener;
	}

	public GuiItemListener getGuiItemListener() {
		return guiItemListener;
	}

	public IPersistenceHandler getPersistenceHandler() {
		return persistenceHandler;
	}

	public void setPersistenceHandler(IPersistenceHandler persistenceHandler) {
		this.persistenceHandler = persistenceHandler;
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
		
		DefaultListModel<String> listModel = new DefaultListModel<String>();

		labelChoosingList = new JList<String>(listModel);
		labelChoosingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		labelChoosingList.setVisibleRowCount(5);
		labelChoosingList.addListSelectionListener(guiListListener);
		labelChoosingList.setName("labelChoosingList");
		JScrollPane listScrollPane = new JScrollPane(labelChoosingList);
		
		editBox.add(listScrollPane , BorderLayout.CENTER);

		JButton changeButton = new JButton("Change");
		changeButton.addActionListener(guiActionListener);
		changeButton.setName("changeButton");
		
		JButton deleteButton = new JButton("Delete Feature");
		deleteButton.setBackground(Color.RED);
		deleteButton.addActionListener(guiActionListener);
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
		
		editBox.add(buttonPane, BorderLayout.PAGE_END);
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
		JMenuItem urlsList = new JMenuItem("Get Urls from List");
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(guiActionListener);
		exit.setName("exit");
		
		JMenu exportMenu = new JMenu("Export");
		JMenu importMenu = new JMenu("Import");
		
		JMenuItem exportXmlItem = new JMenuItem("to xml");
		exportXmlItem.setName("exportXml");
		exportXmlItem.addActionListener(guiActionListener);
		
		JMenuItem exportLuceneItem = new JMenuItem("to lucene");
		exportLuceneItem.setName("exportLucene");
		exportLuceneItem.addActionListener(guiActionListener);
		
		JMenuItem exportSerialItem = new JMenuItem("to Serial Datastore");
		exportSerialItem.setName("exportSerial");
		exportSerialItem.addActionListener(guiActionListener);
		
		exportMenu.add(exportXmlItem);
		exportMenu.add(exportLuceneItem);
		exportMenu.add(exportSerialItem);
		
		JMenuItem importXmlItem = new JMenuItem("from xml");
		importXmlItem.setName("importXml");
		importXmlItem.addActionListener(guiActionListener);
		
		JMenuItem importLuceneItem = new JMenuItem("from lucene");
		importLuceneItem.setName("importLucene");
		importLuceneItem.addActionListener(guiActionListener);
		
		JMenuItem importSerialItem = new JMenuItem("from Serial Datastore");
		importSerialItem.setName("importSerial");
		importSerialItem.addActionListener(guiActionListener);
		
		importMenu.add(importXmlItem);
		importMenu.add(importLuceneItem);
		importMenu.add(importSerialItem);
		
		menu.add(urlsList);
		menu.addSeparator();
		menu.add(exportMenu);
		menu.add(importMenu);
		menu.addSeparator();
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
		this.showOptionsPane();
	}
	
	private void showOptionsPane(){
		optionFrame = new OptionWindow(urlField.getText());
	}
	
	public GuiActionListener getGuiListener() {
		return guiActionListener;
	}

	public OptionWindow getOptionFrame() {
		return optionFrame;
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
	public JList<String> getFeatureList() {
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
		DefaultListModel<String> model = (DefaultListModel<String>) this.featureList.getModel();
		model.clear();
		
		ArrayList<Feature> sortedFeatures = new ArrayList<Feature>(featureContainer.getFeatureStorage().values());
		Collections.sort(sortedFeatures);
		
		for (Feature feature : sortedFeatures) {
			model.addElement(feature.getLabel());
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

	public void updateEditBoxContent(String key) {
		editBox.setVisible(true);
		DefaultListModel<String> model = (DefaultListModel<String>) this.labelChoosingList.getModel();
		model.clear();
		
		ArrayList<String> distinctLabels = featureContainer.getDistinctLabels(key);
		for(String label : distinctLabels){
			model.addElement(label);
		}
	}
}
