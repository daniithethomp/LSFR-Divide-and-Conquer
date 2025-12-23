package lab1;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DivideAndConquer {

	// cached keystream to avoid reopening the file repeatedly
	private static String cachedKeystream = null;

	// combining function f as specified
	public static int f(int x1, int x2, int x3) {
		int code = (x1 << 2) | (x2 << 1) | x3;
		switch (code) {
			case 0: // 000
				return 0;
			case 1: // 001
				return 1;
			case 2: // 010
				return 0;
			case 3: // 011
				return 1;
			case 4: // 100
				return 0;
			case 5: // 101
				return 0;
			case 6: // 110
				return 1;
			case 7: // 111
				return 1;
			default:
				return -1;
		}
	}

	private static String loadKeystream() {
		if (cachedKeystream != null) return cachedKeystream;
		File keystream = new File("StreamFile.txt");
		try (Scanner scanner = new Scanner(keystream)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().replace(" ", "").trim();
				if (!line.isEmpty()) {
					cachedKeystream = line;
					return cachedKeystream;
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred reading StreamFile.txt.");
			e.printStackTrace();
		}
		return null;
	}

	public int agreeability(String bitstream) {
		// compare using char arrays and minimum length
		String target = loadKeystream();
		if (target == null) return -1;
		int len = Math.min(bitstream.length(), target.length());
		int agreeCount = 0;
		char[] a = bitstream.toCharArray();
		char[] b = target.toCharArray();
		for (int i = 0; i < len; i++) {
			if (a[i] == b[i]) agreeCount++;
		}
		return agreeCount;
	}

	private static String formatState(int value, int width) {
		String s = Integer.toBinaryString(value);
		if (s.length() < width) {
			return "0".repeat(width - s.length()) + s;
		}
		return s;
	}

	private String findLsfr2State(String target) {
		int maxState = (1 << 11) - 1; // 2^11 possible states
		int bestAgree = 0;
		String bestState = null;

		for (int s2 = 0; s2 <= maxState; s2++) {
			String state2 = formatState(s2, 11);
			LSFR lsfr2 = new LSFR(11, state2, new int[]{10, 1});

			int agreeCount = 0;
			for (int i =0; i < Math.min(2000, target.length()); i++) {
				int bit2 = lsfr2.getNextBit();
				int targetBit = Character.getNumericValue(target.charAt(i));
				if (bit2 == targetBit) {
					agreeCount++;
				}
			}
			if (agreeCount > bestAgree) {
				bestAgree = agreeCount;
				bestState = state2;
			}
		}
		System.out.println("Best LSFR2 State: " + bestState + " with agreeability: " + bestAgree);
		return bestState;
	}

	private String findLsfr3State(String target) {
		int maxState = (1 << 13) - 1; // 2^13 possible states
		int bestAgree = 0;
		String bestState = null;

		for (int s3 = 0; s3 <= maxState; s3++) {
			String state3 = formatState(s3, 13);
			LSFR lsfr3 = new LSFR(13, state3, new int[]{0, 2, 3, 12});

			int agreeCount = 0;
			for (int i =0; i < Math.min(2000, target.length()); i++) {
				int bit3 = lsfr3.getNextBit();
				int targetBit = Character.getNumericValue(target.charAt(i));
				if (bit3 == targetBit) {
					agreeCount++;
				}
			}

			if (agreeCount > bestAgree) {
				bestAgree = agreeCount;
				bestState = state3;
			}
		}
		System.out.println("Best LSFR3 State: " + bestState + " with agreeability: " + bestAgree);
		return bestState;
	}

	private String findLsfr1State(String target, String state2, String state3) {
		int maxState = (1 << 7) - 1; // 2^7 possible states
		int bestAgree = 0;
		String bestState = null;

		for (int s1 = 0; s1 <= maxState; s1++) {
			String state1 = formatState(s1, 7);
			LSFR lsfr1 = new LSFR(7, state1, new int[]{0, 6});
			LSFR lsfr2 = new LSFR(11, state2, new int[]{10, 1});
			LSFR lsfr3 = new LSFR(13, state3, new int[]{0, 2, 3, 12});

			int agreeCount = 0;
			for (int i =0; i < Math.min(2000, target.length()); i++) {
				int bit1 = lsfr1.getNextBit();
				int bit2 = lsfr2.getNextBit();
				int bit3 = lsfr3.getNextBit();
				int outputBit = f(bit1, bit2, bit3);
				int targetBit = Character.getNumericValue(target.charAt(i));
				if (outputBit == targetBit) {
					agreeCount++;
				}
			}

			if (agreeCount > bestAgree) {
				bestAgree = agreeCount;
				bestState = state1;
			}
		}
		System.out.println("Best LSFR1 State: " + bestState + " with agreeability: " + bestAgree);
		return bestState;
	}

	public static void main(String[] args) {
		DivideAndConquer dac = new DivideAndConquer();
		String target = loadKeystream();
		if (target == null) {
			System.out.println("Failed to load keystream.");
			return;
		}

		String bestState2 = dac.findLsfr2State(target);
		String bestState3 = dac.findLsfr3State(target);
		String bestState1 = dac.findLsfr1State(target, bestState2, bestState3);

		System.out.println("Recovered States:");
		System.out.println("LSFR1: " + bestState1);
		System.out.println("LSFR2: " + bestState2);
		System.out.println("LSFR3: " + bestState3);
	}
}