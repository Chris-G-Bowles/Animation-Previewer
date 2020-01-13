//Animation Previewer Panel

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class AnimationPreviewerPanel extends JPanel implements ActionListener, KeyListener, MouseListener {
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private String directoryLocation;
	private ArrayList<BufferedImage> patterns;
	private int patternLength;
	private int paletteSize;
	private int grayscaleAmount;
	private int patternDisplayLength = 64;
	private int minimumColorValue = 0;
	private int maximumColorValue;
	private int[] defaultColorValues;
	private int[] redValues;
	private int[] greenValues;
	private int[] blueValues;
	private int minimumTintValue;
	private int maximumTintValue;
	private int defaultTintValue = 0;
	private int redTintValue;
	private int greenTintValue;
	private int blueTintValue;
	private Color[] colors;
	private Color[] inverseColors;
	private Color[] tintedColors;
	private Color[] inverseTintedColors;
	private int defaultPatternIndex = 0;
	private int patternIndex;
	private int defaultDelay = -1;
	private int delay;
	private int minimumDelayLength = 1;
	private int maximumDelayLength = 60;
	private int defaultDelayLength = 10;
	private int delayLength;
	private int minimumZoomFactor = 1;
	private int maximumZoomFactor;
	private int defaultZoomFactor;
	private int zoomFactor;
	private boolean defaultAnimationEnabled = true;
	private boolean animationEnabled;
	private boolean defaultBorderEnabled = true;
	private boolean borderEnabled;
	private boolean defaultXAxisEnabled = false;
	private boolean xAxisFlipEnabled;
	private boolean defaultYAxisEnabled = false;
	private boolean yAxisFlipEnabled;
	private boolean isRendering = false;
	private boolean isUpdating = false;
	private JFileChooser fileChooser;
	private JPanel titlePanel;
	private JLabel titleLabel;
	private JPanel patternListPanel;
	private JLabel patternListLabel;
	private JPanel imagePanel;
	private JLabel imageLabel;
	private JPanel[] colorPanels;
	private JLabel[] redLabels;
	private JTextField[] redTextFields;
	private JLabel[] greenLabels;
	private JTextField[] greenTextFields;
	private JLabel[] blueLabels;
	private JTextField[] blueTextFields;
	private JTextField redTintTextField;
	private JTextField greenTintTextField;
	private JTextField blueTintTextField;
	private JTextField delayLengthTextField;
	private JButton zoomInButton;
	private JButton zoomOutButton;
	private JCheckBox animationCheckBox;
	private JCheckBox borderCheckBox;
	private JCheckBox xAxisFlipCheckBox;
	private JCheckBox yAxisFlipCheckBox;
	private JButton addNewPatternButton;
	private JButton removeSelectedPatternButton;
	private JButton moveSelectedPatternButton;
	private JTextField moveSelectedPatternTextField;
	
	public AnimationPreviewerPanel(JFrame frame, String directoryLocation, ArrayList<BufferedImage> patterns,
			int patternLength, int paletteSize, int grayscaleAmount) {
		//Initialize Panel Data
		this.frame = frame;
		this.directoryLocation = directoryLocation;
		this.patterns = patterns;
		this.patternLength = patternLength;
		this.paletteSize = paletteSize;
		this.grayscaleAmount = grayscaleAmount;
		maximumColorValue = paletteSize - 1;
		defaultColorValues = new int[paletteSize];
		for (int i = 0; i < defaultColorValues.length; i++) {
			defaultColorValues[i] = i;
		}
		redValues = new int[paletteSize];
		greenValues = new int[paletteSize];
		blueValues = new int[paletteSize];
		minimumTintValue = 1 - paletteSize;
		maximumTintValue = paletteSize - 1;
		colors = new Color[paletteSize];
		inverseColors = new Color[paletteSize];
		tintedColors = new Color[paletteSize];
		inverseTintedColors = new Color[paletteSize];
		maximumZoomFactor = 256 / patternLength;
		defaultZoomFactor = 128 / patternLength;
		fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		initializeSettings();
		//Set Panel Properties
		setLayout(new BorderLayout());
		addKeyListener(this);
		setFocusable(true);
		//Top Portion
		JPanel topPanel = new JPanel(new GridLayout(2, 1));
		//Title Row
		titlePanel = new JPanel(new GridLayout(1, 1));
		titleLabel = new JLabel("", SwingConstants.CENTER);
		titleLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
		titlePanel.add(titleLabel);
		topPanel.add(titlePanel);
		//Pattern List Row
		patternListPanel = new JPanel(new GridBagLayout());
		patternListLabel = new JLabel("", SwingConstants.CENTER);
		patternListLabel.addMouseListener(this);
		patternListPanel.add(patternListLabel);
		topPanel.add(patternListPanel);
		add(topPanel, BorderLayout.NORTH);
		//Image Portion
		imagePanel = new JPanel(new GridBagLayout());
		imageLabel = new JLabel("", SwingConstants.CENTER);
		imagePanel.add(imageLabel);
		add(imagePanel, BorderLayout.CENTER);
		//Bottom Portion
		JPanel bottomPanel = new JPanel(new GridLayout((paletteSize / 2) + 4, 1));
		//Color Rows
		colorPanels = new JPanel[paletteSize];
		redLabels = new JLabel[paletteSize];
		redTextFields = new JTextField[paletteSize];
		greenLabels = new JLabel[paletteSize];
		greenTextFields = new JTextField[paletteSize];
		blueLabels = new JLabel[paletteSize];
		blueTextFields = new JTextField[paletteSize];
		for (int i = 0; i < paletteSize; i += 2) {
			JPanel twoColorPanel = new JPanel(new GridLayout(1, 2));
			for (int j = i; j < i + 2; j++) {
				colorPanels[j] = new JPanel(new GridLayout(1, 4));
				JButton updateColorButton = new JButton("Update Color " + j);
				updateColorButton.setActionCommand("Update Color " + j);
				updateColorButton.addActionListener(this);
				colorPanels[j].add(updateColorButton);
				JPanel redPanel = new JPanel(new GridLayout(1, 2));
				redPanel.setOpaque(false);
				redLabels[j] = new JLabel("Red:", SwingConstants.CENTER);
				redPanel.add(redLabels[j]);
				redTextFields[j] = new JTextField();
				redTextFields[j].setHorizontalAlignment(SwingConstants.CENTER);
				redTextFields[j].setText("" + redValues[j]);
				redPanel.add(redTextFields[j]);
				colorPanels[j].add(redPanel);
				JPanel greenPanel = new JPanel(new GridLayout(1, 2));
				greenPanel.setOpaque(false);
				greenLabels[j] = new JLabel("Green:", SwingConstants.CENTER);
				greenPanel.add(greenLabels[j]);
				greenTextFields[j] = new JTextField();
				greenTextFields[j].setHorizontalAlignment(SwingConstants.CENTER);
				greenTextFields[j].setText("" + greenValues[j]);
				greenPanel.add(greenTextFields[j]);
				colorPanels[j].add(greenPanel);
				JPanel bluePanel = new JPanel(new GridLayout(1, 2));
				bluePanel.setOpaque(false);
				blueLabels[j] = new JLabel("Blue:", SwingConstants.CENTER);
				bluePanel.add(blueLabels[j]);
				blueTextFields[j] = new JTextField();
				blueTextFields[j].setHorizontalAlignment(SwingConstants.CENTER);
				blueTextFields[j].setText("" + blueValues[j]);
				bluePanel.add(blueTextFields[j]);
				colorPanels[j].add(bluePanel);
				twoColorPanel.add(colorPanels[j]);
			}
			bottomPanel.add(twoColorPanel);
		}
		//Text Field Row
		JPanel textFieldPanel = new JPanel(new GridLayout(1, 6));
		JButton updateTintButton = new JButton("Update Tint");
		updateTintButton.setActionCommand("Update Tint");
		updateTintButton.addActionListener(this);
		textFieldPanel.add(updateTintButton);
		JPanel redTintPanel = new JPanel(new GridLayout(1, 2));
		redTintPanel.add(new JLabel("Red:", SwingConstants.CENTER));
		redTintTextField = new JTextField();
		redTintTextField.setHorizontalAlignment(SwingConstants.CENTER);
		redTintTextField.setText("" + redTintValue);
		redTintPanel.add(redTintTextField);
		textFieldPanel.add(redTintPanel);
		JPanel greenTintPanel = new JPanel(new GridLayout(1, 2));
		greenTintPanel.add(new JLabel("Green:", SwingConstants.CENTER));
		greenTintTextField = new JTextField();
		greenTintTextField.setHorizontalAlignment(SwingConstants.CENTER);
		greenTintTextField.setText("" + greenTintValue);
		greenTintPanel.add(greenTintTextField);
		textFieldPanel.add(greenTintPanel);
		JPanel blueTintPanel = new JPanel(new GridLayout(1, 2));
		blueTintPanel.add(new JLabel("Blue:", SwingConstants.CENTER));
		blueTintTextField = new JTextField();
		blueTintTextField.setHorizontalAlignment(SwingConstants.CENTER);
		blueTintTextField.setText("" + blueTintValue);
		blueTintPanel.add(blueTintTextField);
		textFieldPanel.add(blueTintPanel);
		JButton updateDelayLengthButton = new JButton("Update Delay Length");
		updateDelayLengthButton.setActionCommand("Update Delay Length");
		updateDelayLengthButton.addActionListener(this);
		textFieldPanel.add(updateDelayLengthButton);
		delayLengthTextField = new JTextField();
		delayLengthTextField.setHorizontalAlignment(SwingConstants.CENTER);
		delayLengthTextField.setText("" + delayLength);
		textFieldPanel.add(delayLengthTextField);
		bottomPanel.add(textFieldPanel);
		//Display Row
		JPanel displayPanel = new JPanel(new GridLayout(1, 6));
		zoomInButton = new JButton();
		zoomInButton.setActionCommand("Zoom In");
		zoomInButton.addActionListener(this);
		displayPanel.add(zoomInButton);
		zoomOutButton = new JButton();
		zoomOutButton.setActionCommand("Zoom Out");
		zoomOutButton.addActionListener(this);
		displayPanel.add(zoomOutButton);
		animationCheckBox = new JCheckBox("Toggle Animation");
		animationCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		animationCheckBox.setSelected(animationEnabled);
		animationCheckBox.setActionCommand("Toggle Animation");
		animationCheckBox.addActionListener(this);
		displayPanel.add(animationCheckBox);
		borderCheckBox = new JCheckBox("Toggle Border");
		borderCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		borderCheckBox.setSelected(borderEnabled);
		borderCheckBox.setActionCommand("Toggle Border");
		borderCheckBox.addActionListener(this);
		displayPanel.add(borderCheckBox);
		xAxisFlipCheckBox = new JCheckBox("Toggle X Axis Flip");
		xAxisFlipCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		xAxisFlipCheckBox.setSelected(xAxisFlipEnabled);
		xAxisFlipCheckBox.setActionCommand("Toggle X Axis Flip");
		xAxisFlipCheckBox.addActionListener(this);
		displayPanel.add(xAxisFlipCheckBox);
		yAxisFlipCheckBox = new JCheckBox("Toggle Y Axis Flip");
		yAxisFlipCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		yAxisFlipCheckBox.setSelected(yAxisFlipEnabled);
		yAxisFlipCheckBox.setActionCommand("Toggle Y Axis Flip");
		yAxisFlipCheckBox.addActionListener(this);
		displayPanel.add(yAxisFlipCheckBox);
		bottomPanel.add(displayPanel);
		//Pattern List Control Row
		JPanel patternListControlPanel = new JPanel(new GridLayout(1, 4));
		addNewPatternButton = new JButton();
		addNewPatternButton.setActionCommand("Add New Pattern");
		addNewPatternButton.addActionListener(this);
		patternListControlPanel.add(addNewPatternButton);
		removeSelectedPatternButton = new JButton();
		removeSelectedPatternButton.setActionCommand("Remove Selected Pattern");
		removeSelectedPatternButton.addActionListener(this);
		patternListControlPanel.add(removeSelectedPatternButton);
		moveSelectedPatternButton = new JButton();
		moveSelectedPatternButton.setActionCommand("Move Selected Pattern");
		moveSelectedPatternButton.addActionListener(this);
		patternListControlPanel.add(moveSelectedPatternButton);
		moveSelectedPatternTextField = new JTextField();
		moveSelectedPatternTextField.setHorizontalAlignment(SwingConstants.CENTER);
		moveSelectedPatternTextField.setText("1");
		patternListControlPanel.add(moveSelectedPatternTextField);
		bottomPanel.add(patternListControlPanel);
		//Settings Row
		JPanel settingsPanel = new JPanel(new GridLayout(1, 3));
		JButton loadSettingsButton = new JButton("Load Settings");
		loadSettingsButton.setActionCommand("Load Settings");
		loadSettingsButton.addActionListener(this);
		settingsPanel.add(loadSettingsButton);
		JButton saveSettingsButton = new JButton("Save Settings");
		saveSettingsButton.setActionCommand("Save Settings");
		saveSettingsButton.addActionListener(this);
		settingsPanel.add(saveSettingsButton);
		JButton resetSettingsButton = new JButton("Reset Settings back to Default");
		resetSettingsButton.setActionCommand("Reset Settings");
		resetSettingsButton.addActionListener(this);
		settingsPanel.add(resetSettingsButton);
		bottomPanel.add(settingsPanel);
		add(bottomPanel, BorderLayout.SOUTH);
		//Create a new thread for the refresh to run on
		Thread thread = new Thread() {
			public void run() {
				refreshLoop();
			}
		};
		thread.start();
	}
	
	private void initializeSettings() {
		for (int i = 0; i < paletteSize; i++) {
			redValues[i] = defaultColorValues[i];
			greenValues[i] = defaultColorValues[i];
			blueValues[i] = defaultColorValues[i];
		}
		redTintValue = defaultTintValue;
		greenTintValue = defaultTintValue;
		blueTintValue = defaultTintValue;
		generateColorObjects();
		patternIndex = defaultPatternIndex;
		delay = defaultDelay;
		delayLength = defaultDelayLength;
		zoomFactor = defaultZoomFactor;
		animationEnabled = defaultAnimationEnabled;
		borderEnabled = defaultBorderEnabled;
		xAxisFlipEnabled = defaultXAxisEnabled;
		yAxisFlipEnabled = defaultYAxisEnabled;
		fileChooser.setCurrentDirectory(new File(directoryLocation));
	}
	
	private void generateColorObjects() {
		for (int i = 0; i < paletteSize; i++) {
			colors[i] = new Color(redValues[i] * grayscaleAmount, greenValues[i] * grayscaleAmount,
					blueValues[i] * grayscaleAmount);
			inverseColors[i] = new Color((maximumColorValue - redValues[i]) * grayscaleAmount,
					(maximumColorValue - greenValues[i]) * grayscaleAmount,
					(maximumColorValue - blueValues[i]) * grayscaleAmount);
			int tintedRedValue = redValues[i] + redTintValue;
			if (tintedRedValue > maximumColorValue) {
				tintedRedValue = maximumColorValue;
			} else if (tintedRedValue < minimumColorValue) {
				tintedRedValue = minimumColorValue;
			}
			int tintedGreenValue = greenValues[i] + greenTintValue;
			if (tintedGreenValue > maximumColorValue) {
				tintedGreenValue = maximumColorValue;
			} else if (tintedGreenValue < minimumColorValue) {
				tintedGreenValue = minimumColorValue;
			}
			int tintedBlueValue = blueValues[i] + blueTintValue;
			if (tintedBlueValue > maximumColorValue) {
				tintedBlueValue = maximumColorValue;
			} else if (tintedBlueValue < minimumColorValue) {
				tintedBlueValue = minimumColorValue;
			}
			tintedColors[i] = new Color(tintedRedValue * grayscaleAmount, tintedGreenValue * grayscaleAmount,
					tintedBlueValue * grayscaleAmount);
			inverseTintedColors[i] = new Color((maximumColorValue - tintedRedValue) * grayscaleAmount,
					(maximumColorValue - tintedGreenValue) * grayscaleAmount,
					(maximumColorValue - tintedBlueValue) * grayscaleAmount);
		}
	}
	
	private void refreshLoop() {
		boolean isRunning = true;
		long beginTime;
		long timeElapsed;
		long totalInterval = 16666666;
		long renderInterval = 0;
		long sleepInterval = 0;
		long slowdownInterval = 0;
		long loopInterval = 0;
		if (AnimationPreviewer.OPERATING_SYSTEM.indexOf("mac") >= 0) {
			renderInterval = 200000;
			sleepInterval = 1400000;
			slowdownInterval = 1000;
			loopInterval = 1000;
		} else if (AnimationPreviewer.OPERATING_SYSTEM.indexOf("win") >= 0) {
			renderInterval = 5000000;
			sleepInterval = 2000000;
			slowdownInterval = 1000;
			loopInterval = 20000;
		}
		while (isRunning) {
			//The beginning of the cycle
			beginTime = System.nanoTime();
			//Panel variables handled here
			if (animationEnabled) {
				delay++;
				if (delay >= delayLength) {
					delay = 0;
					patternIndex++;
					if (patternIndex >= patterns.size()) {
						patternIndex = 0;
					}
				}
			}
			//Refresh the panel if there is enough time to
			timeElapsed = System.nanoTime() - beginTime;
			if (timeElapsed < (totalInterval - renderInterval) - loopInterval) {
				if (!isRendering && !isUpdating) {
					Thread thread = new Thread() {
						public void run() {
							refreshPanel();
						}
					};
					thread.start();
				}
				//Delay the execution by putting the current thread to sleep
				timeElapsed = System.nanoTime() - beginTime;
				while (isRendering || (timeElapsed < (totalInterval - sleepInterval) - loopInterval)) {
					Thread.yield();
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						System.out.println("Error: Thread.sleep() was interrupted.");
					}
					timeElapsed = System.nanoTime() - beginTime;
				}
			}
			//Delay the execution further to achieve the desired frame rate
			while (timeElapsed < (totalInterval - slowdownInterval) - loopInterval) {
				timeElapsed = System.nanoTime() - beginTime;
			}
		}
	}
	
	private void refreshPanel() {
		isRendering = true;
		titlePanel.setBackground(colors[0]);
		String title;
		if (animationEnabled) {
			title = "[Animating] ";
		} else {
			title = "[Idle] ";
		}
		title += "Image " + String.format("%02d/%02d", patternIndex + 1, patterns.size());
		title += ", Pixels: " + patternLength + "x" + patternLength + ", Colors: " + paletteSize;
		title += ", Zoom Factor: " + zoomFactor;
		titleLabel.setText(title);
		titleLabel.setForeground(inverseColors[0]);
		patternListPanel.setBackground(colors[0]);
		BufferedImage patternList = new BufferedImage(patternDisplayLength * patterns.size(),
				patternDisplayLength, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < patterns.size(); i++) {
			for (int y = 0; y < patternDisplayLength; y++) {
				for (int x = 0; x < patternDisplayLength; x++) {
					long argbValue = patterns.get(i).getRGB((x * patternLength) / patternDisplayLength,
							(y * patternLength) / patternDisplayLength);
					if (argbValue < 0) {
						argbValue += 4294967296L;
					}
					int colorIndex = (int)(argbValue % 256) / grayscaleAmount;
					if (i == patternIndex &&
							(x == 0 || x == patternDisplayLength - 1 || y == 0 || y == patternDisplayLength - 1)) {
						patternList.setRGB(x + (patternDisplayLength * i), y, inverseColors[colorIndex].getRGB());
					} else {
						patternList.setRGB(x + (patternDisplayLength * i), y, colors[colorIndex].getRGB());
					}
				}
			}
		}
		patternListLabel.setIcon(new ImageIcon(patternList));
		imagePanel.setBackground(colors[0]);
		BufferedImage image = new BufferedImage(patternLength * zoomFactor, patternLength * zoomFactor,
				BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int sourceX;
				if (!xAxisFlipEnabled) {
					sourceX = x;
				} else {
					sourceX = (image.getWidth() - 1) - x;
				}
				int sourceY;
				if (!yAxisFlipEnabled) {
					sourceY = y;
				} else {
					sourceY = (image.getHeight() - 1) - y;
				}
				long argbValue = patterns.get(patternIndex).getRGB(sourceX / zoomFactor, sourceY / zoomFactor);
				if (argbValue < 0) {
					argbValue += 4294967296L;
				}
				int colorIndex = (int)(argbValue % 256) / grayscaleAmount;
				if (colorIndex != 0) {
					if (borderEnabled &&
							(x == 0 || x == image.getWidth() - 1 || y == 0 || y == image.getHeight() - 1)) {
						image.setRGB(x, y, inverseTintedColors[colorIndex].getRGB());
					} else {
						image.setRGB(x, y, tintedColors[colorIndex].getRGB());
					}
				} else {
					if (borderEnabled &&
							(x == 0 || x == image.getWidth() - 1 || y == 0 || y == image.getHeight() - 1)) {
						image.setRGB(x, y, inverseColors[colorIndex].getRGB());
					} else {
						image.setRGB(x, y, colors[colorIndex].getRGB());
					}
				}
			}
		}
		imageLabel.setIcon(new ImageIcon(image));
		for (int i = 0; i < paletteSize; i++) {
			colorPanels[i].setBackground(colors[i]);
			redLabels[i].setForeground(inverseColors[i]);
			greenLabels[i].setForeground(inverseColors[i]);
			blueLabels[i].setForeground(inverseColors[i]);
		}
		if (zoomFactor < maximumZoomFactor) {
			zoomInButton.setText("Zoom In");
		} else {
			zoomInButton.setText("-----");
		}
		if (zoomFactor > minimumZoomFactor) {
			zoomOutButton.setText("Zoom Out");
		} else {
			zoomOutButton.setText("-----");
		}
		if (!animationEnabled && patterns.size() < AnimationPreviewer.MAXIMUM_LIST_SIZE) {
			addNewPatternButton.setText("Add New Pattern");
		} else {
			addNewPatternButton.setText("-----");
		}
		if (!animationEnabled && patterns.size() > AnimationPreviewer.MINIMUM_LIST_SIZE) {
			removeSelectedPatternButton.setText("Remove Selected Pattern");
		} else {
			removeSelectedPatternButton.setText("-----");
		}
		if (!animationEnabled) {
			moveSelectedPatternButton.setText("Move Selected Pattern to this Position:");
		} else {
			moveSelectedPatternButton.setText("-----");
		}
		isRendering = false;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		while (isRendering) {
			Thread.yield();
			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
				System.out.println("Error: Thread.sleep() was interrupted.");
			}
		}
		isUpdating = true;
		if (e.getActionCommand().length() > 13 && e.getActionCommand().substring(0, 13).equals("Update Color ")) {
			updateColor(Integer.parseInt(e.getActionCommand().substring(13)));
		} else if (e.getActionCommand().equals("Update Tint")) {
			updateTint();
		} else if (e.getActionCommand().equals("Update Delay Length")) {
			updateDelayLength();
		} else if (e.getActionCommand().equals("Zoom In")) {
			zoomIn();
		} else if (e.getActionCommand().equals("Zoom Out")) {
			zoomOut();
		} else if (e.getActionCommand().equals("Toggle Animation")) {
			toggleAnimation();
		} else if (e.getActionCommand().equals("Toggle Border")) {
			toggleBorder();
		} else if (e.getActionCommand().equals("Toggle X Axis Flip")) {
			toggleXAxisFlip();
		} else if (e.getActionCommand().equals("Toggle Y Axis Flip")) {
			toggleYAxisFlip();
		} else if (e.getActionCommand().equals("Add New Pattern")) {
			addNewPattern();
		} else if (e.getActionCommand().equals("Remove Selected Pattern")) {
			removeSelectedPattern();
		} else if (e.getActionCommand().equals("Move Selected Pattern")) {
			moveSelectedPattern();
		} else if (e.getActionCommand().equals("Load Settings")) {
			loadSettings();
		} else if (e.getActionCommand().equals("Save Settings")) {
			saveSettings();
		} else if (e.getActionCommand().equals("Reset Settings")) {
			resetSettings();
		}
		requestFocusInWindow();
		isUpdating = false;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public void keyPressed(KeyEvent e) {
		while (isRendering) {
			Thread.yield();
			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
				System.out.println("Error: Thread.sleep() was interrupted.");
			}
		}
		isUpdating = true;
		if (!animationEnabled && e.getKeyCode() == KeyEvent.VK_LEFT) {
			patternIndex--;
			if (patternIndex < 0) {
				patternIndex = patterns.size() - 1;
			}
		} else if (!animationEnabled && e.getKeyCode() == KeyEvent.VK_RIGHT) {
			patternIndex++;
			if (patternIndex > patterns.size() - 1) {
				patternIndex = 0;
			}
		}
		isUpdating = false;
	}
	
	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {
		while (isRendering) {
			Thread.yield();
			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
				System.out.println("Error: Thread.sleep() was interrupted.");
			}
		}
		isUpdating = true;
		if (!animationEnabled && e.getSource().equals(patternListLabel)) {
			patternIndex = e.getX() / patternDisplayLength;
		}
		isUpdating = false;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	private void updateColor(int paletteIndex) {
		if (AnimationPreviewer.isValidInteger(redTextFields[paletteIndex].getText())) {
			int newRedValue = evaluateInput(Integer.parseInt(redTextFields[paletteIndex].getText()),
					minimumColorValue, maximumColorValue, redValues[paletteIndex]);
			redValues[paletteIndex] = newRedValue;
			redTextFields[paletteIndex].setText("" + newRedValue);
		} else {
			redTextFields[paletteIndex].setText("" + redValues[paletteIndex]);
		}
		if (AnimationPreviewer.isValidInteger(greenTextFields[paletteIndex].getText())) {
			int newGreenValue = evaluateInput(Integer.parseInt(greenTextFields[paletteIndex].getText()),
					minimumColorValue, maximumColorValue, greenValues[paletteIndex]);
			greenValues[paletteIndex] = newGreenValue;
			greenTextFields[paletteIndex].setText("" + newGreenValue);
		} else {
			greenTextFields[paletteIndex].setText("" + greenValues[paletteIndex]);
		}
		if (AnimationPreviewer.isValidInteger(blueTextFields[paletteIndex].getText())) {
			int newBlueValue = evaluateInput(Integer.parseInt(blueTextFields[paletteIndex].getText()),
					minimumColorValue, maximumColorValue, blueValues[paletteIndex]);
			blueValues[paletteIndex] = newBlueValue;
			blueTextFields[paletteIndex].setText("" + newBlueValue);
		} else {
			blueTextFields[paletteIndex].setText("" + blueValues[paletteIndex]);
		}
		generateColorObjects();
	}
	
	private void updateTint() {
		if (AnimationPreviewer.isValidInteger(redTintTextField.getText())) {
			int newRedTintValue = evaluateInput(Integer.parseInt(redTintTextField.getText()),
					minimumTintValue, maximumTintValue, redTintValue);
			redTintValue = newRedTintValue;
			redTintTextField.setText("" + newRedTintValue);
		} else {
			redTintTextField.setText("" + redTintValue);
		}
		if (AnimationPreviewer.isValidInteger(greenTintTextField.getText())) {
			int newGreenTintValue = evaluateInput(Integer.parseInt(greenTintTextField.getText()),
					minimumTintValue, maximumTintValue, greenTintValue);
			greenTintValue = newGreenTintValue;
			greenTintTextField.setText("" + newGreenTintValue);
		} else {
			greenTintTextField.setText("" + greenTintValue);
		}
		if (AnimationPreviewer.isValidInteger(blueTintTextField.getText())) {
			int newBlueTintValue = evaluateInput(Integer.parseInt(blueTintTextField.getText()),
					minimumTintValue, maximumTintValue, blueTintValue);
			blueTintValue = newBlueTintValue;
			blueTintTextField.setText("" + newBlueTintValue);
		} else {
			blueTintTextField.setText("" + blueTintValue);
		}
		generateColorObjects();
	}
	
	private void updateDelayLength() {
		if (AnimationPreviewer.isValidInteger(delayLengthTextField.getText())) {
			int newDelayLength = evaluateInput(Integer.parseInt(delayLengthTextField.getText()),
					minimumDelayLength, maximumDelayLength, delayLength);
			delayLength = newDelayLength;
			delayLengthTextField.setText("" + newDelayLength);
			if (animationEnabled) {
				patternIndex = defaultPatternIndex;
				delay = defaultDelay;
			}
		} else {
			delayLengthTextField.setText("" + delayLength);
		}
	}
	
	private void zoomIn() {
		if (zoomFactor < maximumZoomFactor) {
			zoomFactor++;
		}
	}
	
	private void zoomOut() {
		if (zoomFactor > minimumZoomFactor) {
			zoomFactor--;
		}
	}
	
	private void toggleAnimation() {
		animationEnabled = animationCheckBox.isSelected();
		if (animationEnabled) {
			patternIndex = defaultPatternIndex;
			delay = defaultDelay;
		}
	}
	
	private void toggleBorder() {
		borderEnabled = borderCheckBox.isSelected();
	}
	
	private void toggleXAxisFlip() {
		xAxisFlipEnabled = xAxisFlipCheckBox.isSelected();
	}
	
	private void toggleYAxisFlip() {
		yAxisFlipEnabled = yAxisFlipCheckBox.isSelected();
	}
	
	private void addNewPattern() {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (!animationEnabled && patterns.size() < AnimationPreviewer.MAXIMUM_LIST_SIZE &&
				fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				BufferedImage pattern = ImageIO.read(file);
				if (pattern.getWidth() == patternLength && pattern.getHeight() == patternLength) {
					for (int y = 0; y < pattern.getHeight(); y++) {
						for (int x = 0; x < pattern.getWidth(); x++) {
							long argbValue = pattern.getRGB(x, y);
							if (argbValue < 0) {
								argbValue += 4294967296L;
							}
							long alphaValue = argbValue / 16777216;
							long redValue = (argbValue / 65536) % 256;
							long greenValue = (argbValue / 256) % 256;
							long blueValue = argbValue % 256;
							if (alphaValue != 255 || redValue != greenValue || redValue != blueValue ||
									redValue % grayscaleAmount != 0) {
								displayMessage("Error", file.getPath() + " has an invalid " +
										"ARGB value at (" + x + ", " + y + "): [" + alphaValue +
										", " + redValue + ", " + greenValue + ", " + blueValue + "].");
								return;
							}
						}
					}
					patterns.add(pattern);
				} else {
					displayMessage("Error", file.getPath() + " has an invalid resolution of " + pattern.getWidth() +
							"x" + pattern.getHeight() + " pixels.");
				}
			} catch (Exception e) {
				displayMessage("Error", file.getPath() + " does not contain a readable pattern.");
			}
		}
	}
	
	private void removeSelectedPattern() {
		if (!animationEnabled && patterns.size() > AnimationPreviewer.MINIMUM_LIST_SIZE) {
			patterns.remove(patternIndex);
			if (patternIndex > patterns.size() - 1) {
				patternIndex = patterns.size() - 1;
			}
		}
	}
	
	private void moveSelectedPattern() {
		if (!animationEnabled) {
			if (AnimationPreviewer.isValidInteger(moveSelectedPatternTextField.getText())) {
				int newPatternIndex = evaluateInput(Integer.parseInt(moveSelectedPatternTextField.getText()) - 1,
						0, patterns.size() - 1, patternIndex);
				BufferedImage temporary = patterns.get(patternIndex);
				patterns.set(patternIndex, patterns.get(newPatternIndex));
				patterns.set(newPatternIndex, temporary);
				patternIndex = newPatternIndex;
				moveSelectedPatternTextField.setText("" + (newPatternIndex + 1));
			} else {
				moveSelectedPatternTextField.setText("" + (patternIndex + 1));
			}
		}
	}
	
	private void loadSettings() {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				FileInputStream inputStream = new FileInputStream(fileChooser.getSelectedFile());
				int token;
				ArrayList<Integer> inputSettingsFile = new ArrayList<>();
				do {
					try {
						token = inputStream.read();
					} catch (Exception e) {
						token = -1;
					}
					if (token >= 0 && token <= 255) {
						inputSettingsFile.add(token);
					}
				} while (token >= 0 && token <= 255);
				inputStream.close();
				int inputSettingsFileSize = (paletteSize * 3) + 11;
				if (inputSettingsFile.size() == inputSettingsFileSize) {
					if (patternLength == inputSettingsFile.get(0)) {
						if (paletteSize == inputSettingsFile.get(1)) {
							for (int i = 0; i < paletteSize; i++) {
								redValues[i] = evaluateInput(inputSettingsFile.get((i * 3) + 2),
										minimumColorValue, maximumColorValue, defaultColorValues[i]);
								greenValues[i] = evaluateInput(inputSettingsFile.get((i * 3) + 3),
										minimumColorValue, maximumColorValue, defaultColorValues[i]);
								blueValues[i] = evaluateInput(inputSettingsFile.get((i * 3) + 4),
										minimumColorValue, maximumColorValue, defaultColorValues[i]);
							}
							redTintValue = evaluateInput(inputSettingsFile.get((paletteSize * 3) + 2) - 128,
									minimumTintValue, maximumTintValue, defaultTintValue);
							greenTintValue = evaluateInput(inputSettingsFile.get((paletteSize * 3) + 3) - 128,
									minimumTintValue, maximumTintValue, defaultTintValue);
							blueTintValue = evaluateInput(inputSettingsFile.get((paletteSize * 3) + 4) - 128,
									minimumTintValue, maximumTintValue, defaultTintValue);
							delayLength = evaluateInput(inputSettingsFile.get((paletteSize * 3) + 5),
									minimumDelayLength, maximumDelayLength, defaultDelayLength);
							zoomFactor = evaluateInput(inputSettingsFile.get((paletteSize * 3) + 6),
									minimumZoomFactor, maximumZoomFactor, defaultZoomFactor);
							animationEnabled = evaluateInput(inputSettingsFile.get((paletteSize * 3) + 7),
									0, 1, defaultAnimationEnabled ? 1 : 0) == 1;
							borderEnabled = evaluateInput(inputSettingsFile.get((paletteSize * 3) + 8),
									0, 1, defaultBorderEnabled ? 1 : 0) == 1;
							xAxisFlipEnabled = evaluateInput(inputSettingsFile.get((paletteSize * 3) + 9),
									0, 1, defaultXAxisEnabled ? 1 : 0) == 1;
							yAxisFlipEnabled = evaluateInput(inputSettingsFile.get((paletteSize * 3) + 10),
									0, 1, defaultYAxisEnabled ? 1 : 0) == 1;
							generateColorObjects();
							if (animationEnabled) {
								patternIndex = defaultPatternIndex;
								delay = defaultDelay;
							}
							for (int i = 0; i < paletteSize; i++) {
								redTextFields[i].setText("" + redValues[i]);
								greenTextFields[i].setText("" + greenValues[i]);
								blueTextFields[i].setText("" + blueValues[i]);
							}
							redTintTextField.setText("" + redTintValue);
							greenTintTextField.setText("" + greenTintValue);
							blueTintTextField.setText("" + blueTintValue);
							delayLengthTextField.setText("" + delayLength);
							animationCheckBox.setSelected(animationEnabled);
							borderCheckBox.setSelected(borderEnabled);
							xAxisFlipCheckBox.setSelected(xAxisFlipEnabled);
							yAxisFlipCheckBox.setSelected(yAxisFlipEnabled);
						} else {
							displayMessage("Error", fileChooser.getSelectedFile().getPath() + " contains settings " +
									"that are incompatible with " + paletteSize + "-color palettes.");
						}
					} else {
						displayMessage("Error", fileChooser.getSelectedFile().getPath() + " contains settings that " +
								"are incompatible with " + patternLength + "x" + patternLength + "-pixel patterns.");
					}
				} else {
					displayMessage("Error", fileChooser.getSelectedFile().getPath() + " is not " +
							inputSettingsFileSize + " bytes in size.");
				}
			} catch (Exception e) {
				displayMessage("Error", fileChooser.getSelectedFile().getPath() +
						" does not contain readable binary data.");
			}
		}
	}
	
	private void saveSettings() {
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				FileOutputStream outputStream = new FileOutputStream(fileChooser.getSelectedFile());
				outputStream.write(patternLength);
				outputStream.write(paletteSize);
				for (int i = 0; i < paletteSize; i++) {
					outputStream.write(redValues[i]);
					outputStream.write(greenValues[i]);
					outputStream.write(blueValues[i]);
				}
				outputStream.write(redTintValue + 128);
				outputStream.write(greenTintValue + 128);
				outputStream.write(blueTintValue + 128);
				outputStream.write(delayLength);
				outputStream.write(zoomFactor);
				outputStream.write(animationEnabled ? 1 : 0);
				outputStream.write(borderEnabled ? 1 : 0);
				outputStream.write(xAxisFlipEnabled ? 1 : 0);
				outputStream.write(yAxisFlipEnabled ? 1 : 0);
				outputStream.close();
				displayMessage("Success", fileChooser.getSelectedFile().getPath() + " was created!");
			} catch (Exception e) {
				displayMessage("Error", "Could not create " + fileChooser.getSelectedFile().getPath() + ".");
			}
		}
	}
	
	private void resetSettings() {
		initializeSettings();
		for (int i = 0; i < paletteSize; i++) {
			redTextFields[i].setText("" + redValues[i]);
			greenTextFields[i].setText("" + greenValues[i]);
			blueTextFields[i].setText("" + blueValues[i]);
		}
		redTintTextField.setText("" + redTintValue);
		greenTintTextField.setText("" + greenTintValue);
		blueTintTextField.setText("" + blueTintValue);
		delayLengthTextField.setText("" + delayLength);
		animationCheckBox.setSelected(animationEnabled);
		borderCheckBox.setSelected(borderEnabled);
		xAxisFlipCheckBox.setSelected(xAxisFlipEnabled);
		yAxisFlipCheckBox.setSelected(yAxisFlipEnabled);
		moveSelectedPatternTextField.setText("1");
	}
	
	private int evaluateInput(int input, int lowerBoundary, int upperBoundary, int defaultOutput) {
		if (input >= lowerBoundary && input <= upperBoundary) {
			return input;
		} else {
			return defaultOutput;
		}
	}
	
	private void displayMessage(String title, String message) {
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
}
