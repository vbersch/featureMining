package featureMining.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import featureMining.FeatureMining;

public class RootFeatureWindow extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JPanel mainContainer;
	private JTextField urlField;
	private JList featureList;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public RootFeatureWindow(){
		this.setTitle("Feature Mining");
		this.setSize(800,600);
		this.setVisible(true);
		this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
				
		mainContainer = new JPanel(new BorderLayout());
		
	    DefaultListModel listModel = new DefaultListModel();
//        listModel.addElement("Jane Doe");
//        listModel.addElement("John Smith");
//        listModel.addElement("Kathy Green");
        
        DefaultListModel listModel2 = new DefaultListModel();
//        listModel2.addElement("Jane Doe2");
//        listModel2.addElement("John Smith2");
//        listModel2.addElement("Kathy Green2");
		
        featureList = new JList(listModel);
        featureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //list.setSelectedIndex(0);
        featureList.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(featureList);
        
        JList list2 = new JList(listModel2);
        list2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //list.setSelectedIndex(0);
        list2.setVisibleRowCount(5);
        JScrollPane listScrollPane2 = new JScrollPane(list2);
        
        JButton fireButton = new JButton("mine URL");
        fireButton.setEnabled(true);
        fireButton.addActionListener(this);
        //fireButton.set

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
        contentPane.setLayout(new BoxLayout(contentPane , BoxLayout.LINE_AXIS));
        
        contentPane.add(listScrollPane);
        contentPane.add(Box.createHorizontalStrut(20));
        //contentPane.add(new JSeparator(SwingConstants.VERTICAL));
        //contentPane.add(Box.createHorizontalStrut(20));
        contentPane.add(listScrollPane2);
        contentPane.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));
        mainContainer.add(contentPane , BorderLayout.CENTER);
        mainContainer.add(buttonPane, BorderLayout.PAGE_END);
        
		this.setContentPane(mainContainer);
		this.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//System.out.println(urlField.getText());
		FeatureMining.getSingleton().gateTest(urlField.getText());
		
	}

	public void fillFeatureList(ArrayList<String> featureStrings) {
		DefaultListModel model = (DefaultListModel) this.featureList.getModel();
		for(String feature : featureStrings){
			model.addElement(feature);
		}
	}	
}
