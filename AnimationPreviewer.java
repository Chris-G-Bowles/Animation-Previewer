//Animation Previewer

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class AnimationPreviewer {
	public static final int MINIMUM_LIST_SIZE = 1;
	public static final int MAXIMUM_LIST_SIZE = 16;
	public static final String OPERATING_SYSTEM = System.getProperty("os.name").toLowerCase();
	private static ArrayList<BufferedImage> patterns = new ArrayList<>();
	private static int patternLength;
	private static int grayscaleAmount;
	
	public static void main(String[] args) {
		System.out.println("* Animation Previewer *");
		if (args.length != 0 && args.length != 3) {
			error("This program's usage is as follows:\n" +
					"java AnimationPreviewer\n" +
					"java AnimationPreviewer <directory location> <pattern resolution option> <palette size option>");
		}
		Scanner input = new Scanner(System.in);
		String directoryLocation;
		if (args.length == 0) {
			System.out.print("Enter a directory location containing between " + MINIMUM_LIST_SIZE + " to " +
					MAXIMUM_LIST_SIZE + " patterns: ");
			directoryLocation = input.nextLine();
		} else {
			directoryLocation = args[0];
		}
		String patternResolutionString;
		if (args.length == 0) {
			System.out.println("Select the pattern resolution to use:");
			System.out.println("1) 8x8 pixels");
			System.out.println("2) 16x16 pixels");
			System.out.println("3) 32x32 pixels");
			System.out.println("4) 64x64 pixels");
			System.out.print("Pattern resolution option: ");
			patternResolutionString = input.nextLine();
		} else {
			patternResolutionString = args[1];
		}
		String paletteSizeString;
		if (args.length == 0) {
			System.out.println("Select the palette size to use:");
			System.out.println("1) 4 colors");
			System.out.println("2) 6 colors");
			System.out.println("3) 8 colors");
			System.out.print("Palette size option: ");
			paletteSizeString = input.nextLine();
		} else {
			paletteSizeString = args[2];
		}
		input.close();
		File directory = new File(directoryLocation);
		if (directory.isDirectory()) {
			if (isValidInteger(patternResolutionString) && Integer.parseInt(patternResolutionString) >= 1 &&
					Integer.parseInt(patternResolutionString) <= 4) {
				patternLength = (int)Math.pow(2, Integer.parseInt(patternResolutionString) + 2);
				if (isValidInteger(paletteSizeString) && Integer.parseInt(paletteSizeString) >= 1 &&
						Integer.parseInt(paletteSizeString) <= 3) {
					int paletteSize = (Integer.parseInt(paletteSizeString) * 2) + 2;
					grayscaleAmount = 255 / ((Integer.parseInt(paletteSizeString) * 2) + 1);
					System.out.println("(Please wait a few seconds for the patterns to load.)");
					addPatternsFromDirectory(directory);
					if (patterns.size() >= MINIMUM_LIST_SIZE) {
						JFrame frame = new JFrame("Animation Previewer");
						AnimationPreviewerPanel panel = new AnimationPreviewerPanel(frame, directoryLocation,
								patterns, patternLength, paletteSize, grayscaleAmount);
						frame.setContentPane(panel);
						int frameWidth = 1280;
						int frameHeight = 720;
						if (OPERATING_SYSTEM.indexOf("mac") >= 0) {
							frameHeight += 22;
						} else if (OPERATING_SYSTEM.indexOf("win") >= 0) {
							frameWidth += 16;
							frameHeight += 39;
						}
						frame.setSize(frameWidth, frameHeight);
						frame.setLocationRelativeTo(null);
						frame.setResizable(false);
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						frame.setVisible(true);
					} else {
						System.out.println("Error: " + directoryLocation + " contains less than " +
								MINIMUM_LIST_SIZE + " pattern.");
					}
				} else {
					System.out.println("Error: Invalid palette size option.");
				}
			} else {
				System.out.println("Error: Invalid pattern resolution option.");
			}
		} else {
			System.out.println("Error: " + directoryLocation + " is not a valid directory.");
		}
	}
	
	private static void addPatternsFromDirectory(File directory) {
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length && patterns.size() < MAXIMUM_LIST_SIZE; i++) {
			if (files[i].isFile()) {
				addPatternFromFile(files[i]);
			} else if (files[i].isDirectory()) {
				addPatternsFromDirectory(files[i]);
			}
		}
	}
	
	private static void addPatternFromFile(File file) {
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
							System.out.println("Error: " + file.getPath() + " has an invalid " +
									"ARGB value at (" + x + ", " + y + "): [" + alphaValue +
									", " + redValue + ", " + greenValue + ", " + blueValue + "].");
							return;
						}
					}
				}
				patterns.add(pattern);
			} else {
				System.out.println("Error: " + file.getPath() + " has an invalid resolution of " +
						pattern.getWidth() + "x" + pattern.getHeight() + " pixels.");
			}
		} catch (Exception e) {
			System.out.println("Error: " + file.getPath() + " does not contain a readable pattern.");
		}
	}
	
	private static void error(String message) {
		System.out.println("Error: " + message);
		System.exit(1);
	}
}
