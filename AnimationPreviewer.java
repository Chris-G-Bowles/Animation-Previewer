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
		File directory = new File(directoryLocation);
		if (!directory.isDirectory()) {
			error(directoryLocation + " is not a valid directory.");
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
		Scanner lineInput1 = new Scanner(patternResolutionString);
		if (!lineInput1.hasNextInt()) {
			error("Invalid pattern resolution input.");
		}
		int patternResolutionOption = lineInput1.nextInt();
		if (patternResolutionOption < 1 || patternResolutionOption > 4) {
			error("Invalid pattern resolution option.");
		}
		lineInput1.close();
		patternLength = (int)Math.pow(2, patternResolutionOption + 2);
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
		Scanner lineInput2 = new Scanner(paletteSizeString);
		if (!lineInput2.hasNextInt()) {
			error("Invalid palette size input.");
		}
		int paletteSizeOption = lineInput2.nextInt();
		if (paletteSizeOption < 1 || paletteSizeOption > 3) {
			error("Invalid palette size option.");
		}
		lineInput2.close();
		int paletteSize = (paletteSizeOption * 2) + 2;
		grayscaleAmount = 255 / ((paletteSizeOption * 2) + 1);
		input.close();
		System.out.println("(Please wait a few seconds for the patterns to load.)");
		addPatternsFromDirectory(directory);
		if (patterns.size() < MINIMUM_LIST_SIZE) {
			error(directoryLocation + " contains less than " + MINIMUM_LIST_SIZE + " pattern.");
		}
		JFrame frame = new JFrame("Animation Previewer");
		AnimationPreviewerPanel panel = new AnimationPreviewerPanel(frame, directoryLocation, patterns, patternLength,
				paletteSize, grayscaleAmount);
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
	}
	
	private static void addPatternsFromDirectory(File directory) {
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				BufferedImage pattern;
				try {
					pattern = ImageIO.read(file);
				} catch (Exception e) {
					pattern = null;
				}
				if (pattern == null) {
					System.out.println(file.getPath() + " does not contain a readable pattern, and is being skipped.");
					continue;
				}
				if (pattern.getWidth() != patternLength || pattern.getHeight() != patternLength) {
					System.out.println(file.getPath() + " has an invalid resolution of " +
							pattern.getWidth() + "x" + pattern.getHeight() + " pixels, and is being skipped.");
					continue;
				}
				if (!isValidPattern(pattern, file.getPath())) {
					continue;
				}
				patterns.add(pattern);
			} else if (file.isDirectory()) {
				addPatternsFromDirectory(file);
			}
			if (patterns.size() == MAXIMUM_LIST_SIZE) {
				break;
			}
		}
	}
	
	private static boolean isValidPattern(BufferedImage pattern, String fileLocation) {
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
					System.out.println(fileLocation + " has an invalid ARGB value at (" + x + ", " + y + "): [" +
							alphaValue + ", " + redValue + ", " + greenValue + ", " + blueValue +
							"], and is being skipped.");
					return false;
				}
			}
		}
		return true;
	}
	
	private static void error(String message) {
		System.out.println("Error: " + message);
		System.exit(1);
	}
}
