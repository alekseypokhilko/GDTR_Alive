package org.happysanta.gdtralive.game.util;

public class FPMath {

	public static int HALF_PI = 0x19220; // 1,57080949111162
	public static int PI = 0x3243f; // 3,141588464179446

	private FPMath() {
	}

	public static int divide(int i, int j) {
		int res = (int) (((long) i << 32) / (long) j >> 16);
		return res;
	}

	public static int sin(int i) {
		float fi = i / (float) 0xFFFF;
		return (int) Math.round(Math.sin(fi) * 65536);
	}

	public static int _doII(int i) {
		return sin(HALF_PI - i);
	}

	public static int arctg(int i) {
		float fi = i / (float) 0xFFFF;
		return (int) Math.round(Math.atan(fi) * 65536);
	}

	public static int angle(int i, int j) {
		if ((j >= 0 ? j : -j) < 3)
			return (i <= 0 ? -1 : 1) * HALF_PI;
		int k = arctg(divide(i, j));
		if (i > 0)
			if (j > 0)
				return k;
			else
				return PI + k;
		if (j > 0)
			return k;
		else
			return k - PI;
	}
}
