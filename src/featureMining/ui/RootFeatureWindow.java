package featureMining.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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

import featureMining.FeatureMining;
import featureMining.ui.listeners.MenuListener;

public class RootFeatureWindow extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JPanel mainContainer;
	private JTextField urlField;
	private JList featureList;
	JMenuBar menuBar;
	MenuListener menuListener;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public RootFeatureWindow(){
		this.setTitle("Feature Mining");
		this.setSize(800,600);
		this.setVisible(true);
		this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
				
		mainContainer = new JPanel(new BorderLayout());
		menuListener = new MenuListener();
		
	    DefaultListModel listModel = new DefaultListModel();
        DefaultListModel listModel2 = new DefaultListModel();
		
        featureList = new JList(listModel);
        featureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //list.setSelectedIndex(0);
        featureList.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(featureList);
        
        JList list2 = new JList(listModel2);
        list2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list2.setVisibleRowCount(5);
        JScrollPane listScrollPane2 = new JScrollPane(list2);
        
        JButton fireButton = new JButton("mine URL");
        fireButton.setEnabled(true);
        fireButton.addActionListener(this);

        urlField = new JTextField(10);
        urlField.setText("https://github.com/radiant/radiant/wiki");
        
        //Create a panel for the url
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane , BoxLayout.LINE_AXIS));
        buttonPane.add(urlField);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(fireButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane , BoxLayout.PAGE_AXIS));
        
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane , BoxLayout.LINE_AXIS));
        
        listPane.add(listScrollPane);
        listPane.add(Box.createHorizontalStrut(20));
        listPane.add(listScrollPane2);
        listPane.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));
        
        JTabbedPane infoTabs = new JTabbedPane();
        
        JPanel infoPanel = createInnerPanel("Info");
        JScrollPane descPane = createDescPane();
        
        infoTabs.addTab("Info", infoPanel);
        infoTabs.addTab("Description", descPane);
        
        contentPane.add(listPane);
        contentPane.add(infoTabs);
        
        mainContainer.add(contentPane , BorderLayout.CENTER);
        mainContainer.add(buttonPane, BorderLayout.PAGE_END);
        
        this.createMenu();
        
	}
	
	private JScrollPane createDescPane() {
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setText("Description");
		
		JScrollPane scrollPane = new JScrollPane(textArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		return scrollPane;
	}

	private void createMenu() {
		//create the Menu
        menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JMenuItem xmlExport = new JMenuItem("Export to XML");
        JMenuItem urlsList = new JMenuItem("Get Urls from List");
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(menuListener);
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

	protected JPanel createInnerPanel(String text) {
		JPanel jplPanel = new JPanel();
		JLabel jlbDisplay = new JLabel(text);
		jlbDisplay.setHorizontalAlignment(JLabel.CENTER);
		jplPanel.setLayout(new GridLayout(1, 1));
		jplPanel.add(jlbDisplay);
		return jplPanel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		FeatureMining.getSingleton().getDocumentProcessor().createCorpus(urlField.getText());
		FeatureMining.getSingleton().getDocumentProcessor().processCorpus();
	}

	public void fillFeatureList(ArrayList<String> featureStrings) {
		DefaultListModel model = (DefaultListModel) this.featureList.getModel();
		for(String feature : featureStrings){
			model.addElement(feature);
		}
	}	
}
