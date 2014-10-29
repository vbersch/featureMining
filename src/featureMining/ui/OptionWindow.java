package featureMining.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import featureMining.controller.FeatureMining;
import featureMining.feature.OptionTransferObject;
import featureMining.persistence.SettingsManager;



public class OptionWindow extends JFrame {

	private static final long serialVersionUID = -7014479869774484651L;
	private String baseUrl;
	private JFormattedTextField threadField;
	private JTextField hostField;
	private JComboBox preprocessingOptions;
	private JComboBox htmlProcessingOptions;
	private JComboBox domainProcessingOptions;
	private JComboBox stemmingComboBox;
	private JLabel hostLabel;
	private JPanel labelPane;
	private JPanel fieldPane;
	private JLabel htmlLabel;
	private OptionTransferObject settings;
	private JTextField featureBlField;
	private JTextField sentenceBlField;
	private ActionListener blacklistActionListener = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent sender) {
			if(sender.getSource() instanceof JButton){
				JButton button = (JButton) sender.getSource();
				String path = showFileDialog();
				if(button.getName().equals("sentenceBlacklist")){
					sentenceBlField.setText(path);
				}else if(button.getName().equals("featureBlacklist")){
					featureBlField.setText(path);
				}
			}
		}

		private String showFileDialog() {
			
			final JFileChooser fc = new JFileChooser();
			File dir = new File(System.getProperty("user.dir") + "/blacklists");
			if(!dir.exists()){
				dir.mkdir();
			}
			fc.setCurrentDirectory(dir);
			fc.setApproveButtonText("Choose"); 
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int ret = fc.showOpenDialog(null);
			if(ret == JFileChooser.APPROVE_OPTION){
				return fc.getSelectedFile().getAbsolutePath();
			}
			return null;
		}
			
		
	};
	
	public String getBaseUrl() {
		return baseUrl;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public OptionWindow(String url){
		init(url);
		
		JPanel optionsPanel = new JPanel(new BorderLayout());
		
		fieldPane = new JPanel(new GridLayout(0,1));
		
		JLabel headerText = new JLabel("Configuring Feature Mining for " + this.baseUrl);
		
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.PAGE_AXIS));
		headerPanel.add(headerText);
		headerPanel.add(Box.createVerticalStrut(15));
		headerPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		headerPanel.add(Box.createVerticalStrut(15));

		createLabels();
		createFields();
		JPanel bottomPane = createBottomPane();

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

	private JPanel createBottomPane() {
		JPanel bottomPane = new JPanel();
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
		bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.PAGE_AXIS));
		
		bottomPane.add(Box.createVerticalStrut(15));
		bottomPane.add(new JSeparator(SwingConstants.HORIZONTAL));
		bottomPane.add(Box.createVerticalStrut(15));
		bottomPane.add(buttonPane);
		return bottomPane;
	}

	private void createFields() {
		NumberFormat amountFormat = NumberFormat.getNumberInstance();
		threadField = new JFormattedTextField(amountFormat);
		threadField.setMaximumSize(new Dimension(Integer.MAX_VALUE, threadField.getPreferredSize().height) );
		threadField.setValue(new Integer(settings.getThreadNum()));
		threadField.setHorizontalAlignment(JTextField.CENTER);

		JPanel featureBlPicker = new JPanel();
		featureBlPicker.setLayout(new BoxLayout(featureBlPicker, BoxLayout.LINE_AXIS));
		featureBlField = new JTextField(20);
		JButton featureBlButton = new JButton("...");
		featureBlField.setText(settings.getFeatureBlacklistPath());
		featureBlButton.setName("featureBlacklist");
		featureBlButton.addActionListener(blacklistActionListener);
		featureBlButton.setPreferredSize(new Dimension(15,10));
		featureBlPicker.add(featureBlField);
		featureBlPicker.add(featureBlButton);
		
		JPanel sentenceBlPicker = new JPanel();
		sentenceBlPicker.setLayout(new BoxLayout(sentenceBlPicker, BoxLayout.LINE_AXIS));
		sentenceBlField = new JTextField(20);
		sentenceBlField.setText(settings.getSentenceBlacklistPath());
		JButton sentenceBlButton = new JButton("...");
		sentenceBlButton.addActionListener(blacklistActionListener);
		sentenceBlButton.setPreferredSize(new Dimension(15,10));
		sentenceBlButton.setName("sentenceBlacklist");
		sentenceBlPicker.add(sentenceBlField);
		sentenceBlPicker.add(sentenceBlButton);
		
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
		
		String[] stemmingOptions = {"True" , "False"};
		stemmingComboBox = new JComboBox(stemmingOptions);
		if(settings.isStemmingEnabled()){
			stemmingComboBox.setSelectedIndex(0);
		}else{
			stemmingComboBox.setSelectedIndex(1);
		}
		stemmingComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, stemmingComboBox.getPreferredSize().height) );
		((JLabel)stemmingComboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		
		fieldPane.add(threadField);
		fieldPane.add(featureBlPicker);
		fieldPane.add(sentenceBlPicker);
		fieldPane.add(stemmingComboBox);
		fieldPane.add(domainProcessingOptions);
		fieldPane.add(preprocessingOptions);
		
		fieldPane.setPreferredSize(new Dimension(labelPane.getPreferredSize().width + 80  ,labelPane.getPreferredSize().height));
		
	}

	private void createLabels() {
		labelPane = new JPanel(new GridLayout(0,1));
		
		JLabel threadLabel = new JLabel("Number of Threads: ");
		threadLabel.setMaximumSize(new Dimension(threadLabel.getPreferredSize()));
		
		JLabel featureBlacklistLabel = new JLabel("Blacklist Feature Filter: ");
		featureBlacklistLabel.setToolTipText("A .txt File containing a List of Words, that must not "
				+ "occur within a Feature. Features containing one of these words will be discarded.");
		featureBlacklistLabel.setMaximumSize(new Dimension(featureBlacklistLabel.getPreferredSize()));
		
		JLabel sentenceBlacklistLabel = new JLabel("Blacklist Sentence Filter: ");
		sentenceBlacklistLabel.setToolTipText("A .txt File containing a List of Words, that must not "
				+ "occur within a sentence containing a Feature.\nSentences containing of of these "
				+ "words will not be considered for Feature Mining.");
		sentenceBlacklistLabel.setMaximumSize(new Dimension(sentenceBlacklistLabel.getPreferredSize()));
		
		JLabel stemmingLabel = new JLabel("Enable Stemming: ");
		stemmingLabel.setMaximumSize(new Dimension(stemmingLabel.getPreferredSize()));
		
		JLabel domainLabel = new JLabel("Domain specific: ");
		domainLabel.setToolTipText("Every Feature shall contain at least one Domain-specific Noun");
		domainLabel.setMaximumSize(new Dimension(domainLabel.getPreferredSize()));
		
		htmlLabel = new JLabel("Documentation Format: ");
		htmlLabel.setMaximumSize(new Dimension(htmlLabel.getPreferredSize()));
		
		hostLabel = new JLabel("hostName: ");
		hostLabel.setMaximumSize(new Dimension(hostLabel.getPreferredSize()));
		
		JLabel preprocessingLabel = new JLabel("Preprocessor: ");
		preprocessingLabel.setMaximumSize(new Dimension(preprocessingLabel.getPreferredSize()));
		
		labelPane.add(threadLabel);
		labelPane.add(featureBlacklistLabel);
		labelPane.add(sentenceBlacklistLabel);
		labelPane.add(stemmingLabel);
		labelPane.add(domainLabel);
		labelPane.add(preprocessingLabel);
		
	}

	private void init(String url) {
		this.baseUrl = url;
		this.setSize(480 , 320);
		this.setVisible(true);
		this.setTitle("Options");
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		
		settings = SettingsManager.getOptions();
		
		if(settings.getHostName() == null){
			if(this.baseUrl != settings.getBaseUrl()){
				settings.setBaseUrl(this.baseUrl);
			}
			
			//Pattern hostPattern = Pattern.compile("//.*");
			//Matcher hostMatcher = hostPattern.matcher(this.baseUrl);
			//hostMatcher.find();
			//hostMatcher.group()
			settings.setHostName("hostName");
			settings.setDomainSpecific(true);
			settings.setDocumentationType("General");
			settings.setPreprocessingName("Html");
			settings.setThreadNum(4);
			settings.setEnableStemming(true);
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

	public String getFeatureBlacklistPath() {
		return this.featureBlField.getText();
	}

	public String getSentenceBlacklistPath() {
		return this.sentenceBlField.getText();
	}

	public String getEnableStemming() {
		return (String)stemmingComboBox.getSelectedItem();
	}
}
