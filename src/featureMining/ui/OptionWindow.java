package featureMining.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import featureMining.feature.OptionTransferObject;
import featureMining.main.FeatureMining;
import featureMining.persistence.xml.SettingsManager;



public class OptionWindow extends JFrame {

	private String baseUrl;
	private JFormattedTextField threadField;
	private JTextField hostField;
	private JComboBox preprocessingOptions;
	private JComboBox htmlProcessingOptions;
	private JComboBox domainProcessingOptions;
	private JLabel hostLabel;
	private JPanel labelPane;
	private JPanel fieldPane;
	private JLabel htmlLabel;
	
	public String getBaseUrl() {
		return baseUrl;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public OptionWindow(String url){
		this.baseUrl = url;
		this.setSize(480 , 320);
		this.setVisible(true);
		this.setTitle("Options");
		this.setLocationRelativeTo(null);
		
		OptionTransferObject settings = SettingsManager.getOptions();
		
		if(settings.getHostName() == null){
			if(this.baseUrl != settings.getBaseUrl()){
				settings.setBaseUrl(this.baseUrl);
			}
			
			Pattern hostPattern = Pattern.compile("//.*");
			Matcher hostMatcher = hostPattern.matcher(this.baseUrl);
			hostMatcher.find();
			settings.setHostName(hostMatcher.group().replace("//github.com", ""));
			settings.setDomainSpecific(true);
			settings.setDocumentationType("General");
			settings.setPreprocessingName("Html");
			settings.setThreadNum(4);
		}
		
		JPanel optionsPanel = new JPanel(new BorderLayout());
		
		labelPane = new JPanel(new GridLayout(0,1));
		fieldPane = new JPanel(new GridLayout(0,1));
		
		JLabel headerText = new JLabel("Configuring Feature Mining for " + this.baseUrl);
		
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.PAGE_AXIS));
		headerPanel.add(headerText);
		headerPanel.add(Box.createVerticalStrut(15));
		headerPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		headerPanel.add(Box.createVerticalStrut(15));
		
		JLabel threadLabel = new JLabel("Number of Threads: ");
		threadLabel.setMaximumSize(new Dimension(threadLabel.getPreferredSize()));
		
		JLabel domainLabel = new JLabel("Domain specific: ");
		domainLabel.setToolTipText("Every Feature shall contain at least one Domain-specific Noun");
		domainLabel.setMaximumSize(new Dimension(domainLabel.getPreferredSize()));
		
		htmlLabel = new JLabel("Documentation Format: ");
		htmlLabel.setMaximumSize(new Dimension(htmlLabel.getPreferredSize()));
		
		hostLabel = new JLabel("hostName: ");
		hostLabel.setMaximumSize(new Dimension(hostLabel.getPreferredSize()));
		
		JLabel preprocessingLabel = new JLabel("Preprocessor: ");
		preprocessingLabel.setMaximumSize(new Dimension(preprocessingLabel.getPreferredSize()));
		
		NumberFormat amountFormat = NumberFormat.getNumberInstance();
		threadField = new JFormattedTextField(amountFormat);
		threadField.setMaximumSize(new Dimension(Integer.MAX_VALUE, threadField.getPreferredSize().height) );
		threadField.setValue(new Integer(settings.getThreadNum()));
		threadField.setHorizontalAlignment(JTextField.CENTER);
		
		hostField = new JTextField(20);
		hostField.setText(settings.getHostName());
		hostField.setMaximumSize(new Dimension(Integer.MAX_VALUE, hostField.getPreferredSize().height) );
		hostField.setHorizontalAlignment(JTextField.CENTER);
		
		String[] options = {"Html" , "Pdf", "Word"};
		preprocessingOptions = new JComboBox(options);
		preprocessingOptions.setSelectedItem(settings.getPreprocessingName());
		preprocessingOptions.setMaximumSize(new Dimension(Integer.MAX_VALUE, preprocessingOptions.getPreferredSize().height) );
		((JLabel)preprocessingOptions.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		preprocessingOptions.addItemListener(FeatureMining.getSingleton().getRootWindow().getGuiItemListener());
		
		String[] htmlOptions = {"General" , "Mixxx" , "Github"};
		htmlProcessingOptions = new JComboBox(htmlOptions);
		htmlProcessingOptions.setName("preprocessingOptions");
		htmlProcessingOptions.setSelectedItem(settings.getDocumentationType());
		htmlProcessingOptions.setMaximumSize(new Dimension(Integer.MAX_VALUE, htmlProcessingOptions.getPreferredSize().height) );
		((JLabel)htmlProcessingOptions.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		
		String[] domainOptions = {"True" , "False"};
		domainProcessingOptions = new JComboBox(domainOptions);
		if(settings.isDomainSpecific()){
			domainProcessingOptions.setSelectedIndex(0);
		}else{
			domainProcessingOptions.setSelectedIndex(1);
		}
		domainProcessingOptions.setMaximumSize(new Dimension(Integer.MAX_VALUE, domainProcessingOptions.getPreferredSize().height) );
		((JLabel)domainProcessingOptions.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		
		labelPane.add(threadLabel);
		labelPane.add(domainLabel);
		labelPane.add(preprocessingLabel);
		
		fieldPane.add(threadField);
		fieldPane.add(domainProcessingOptions);
		fieldPane.add(preprocessingOptions);
		
		fieldPane.setPreferredSize(new Dimension(labelPane.getPreferredSize().width + 80  ,labelPane.getPreferredSize().height));
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		
		JButton goButton = new JButton("Go");
		goButton.setName("featureMining");
		goButton.addActionListener(FeatureMining.getSingleton().getRootWindow().getGuiListener());
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setName("cancelOptions");
		cancelButton.addActionListener(FeatureMining.getSingleton().getRootWindow().getGuiListener());
		
		buttonPane.add(goButton);
		buttonPane.add(cancelButton);
		
		JPanel bottomPane = new JPanel();
		bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.PAGE_AXIS));
		
		bottomPane.add(Box.createVerticalStrut(15));
		bottomPane.add(new JSeparator(SwingConstants.HORIZONTAL));
		bottomPane.add(Box.createVerticalStrut(15));
		bottomPane.add(buttonPane);
		
		optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		optionsPanel.add(headerPanel, BorderLayout.NORTH);
		optionsPanel.add(labelPane, BorderLayout.CENTER);
		optionsPanel.add(fieldPane, BorderLayout.LINE_END);
		optionsPanel.add(bottomPane, BorderLayout.PAGE_END);
		
		this.setContentPane(optionsPanel);
		this.pack();
		
		if(preprocessingOptions.getSelectedItem() == "Html"){
			this.addHtmlOptions();
		}
		
	}

	public int getThreadNum() {
		return ((Number)this.threadField.getValue()).intValue();
	}

	public String getPreprocessor() {
		return (String)preprocessingOptions.getSelectedItem();
	}
	
	public String getDomainOptions(){
		return (String)domainProcessingOptions.getSelectedItem();
	}
	
	public String getHostName() {
		return hostField.getText();
	}

	public void addHtmlOptions() {
		labelPane.add(htmlLabel);
		fieldPane.add(htmlProcessingOptions);
		
		labelPane.add(hostLabel);
		fieldPane.add(hostField);
		
		this.pack();
		
	}

	public void removeHtmlOptions() {
		
		labelPane.remove(hostLabel);
		fieldPane.remove(htmlProcessingOptions);
		
		labelPane.remove(htmlLabel);
		fieldPane.remove(hostField);
		
		this.pack();
	}

	public String getDocumentationType() {
		if(this.getPreprocessor() == "Html"){
			return (String)htmlProcessingOptions.getSelectedItem();
		}else{
			return null;
		}
	}
}
