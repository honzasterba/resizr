package com.honzasterba.resizr.img;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

public abstract class Parsr {

	public static final String RESIZE = "resize";

	public static final String CROP = "crop";

	public static final String ROTATE = "rotate";

	public static final String HFLIP = "hflip";

	public static final String VFLIP = "vflip";

	public static final String SEP = ",";

	private static final Pattern NUMBER_CHECK = Pattern.compile("\\d+");

	protected abstract void onResize(int width, int height);

	protected abstract void onCrop(int x, int y, int radius);

	protected abstract void onCrop(int x, int y, int with, int height);

	protected abstract void onRotate(int angle);

	protected abstract void onHFlip();

	protected abstract void onVFlip();
	
	public boolean parse(String opString) throws Exception {
		if (opString == null) {
			return false;
		}
		boolean res = false;
		StringTokenizer tk = new StringTokenizer(opString, SEP);
		String op = null;
		while (tk.hasMoreTokens()) {
			if (op == null) {
				op = tk.nextToken();
			}
			res = true;
			if (op.equals(RESIZE)) {
				op = parseResize(tk);
			} else if (op.equals(CROP)) {
				op = parseCrop(tk);
			} else if (op.equals(ROTATE)) {
				parseRotate(tk);
				op = null;
			} else if (op.equals(VFLIP)) {
				onVFlip();
				op = null;
			} else if (op.equals(HFLIP)) {
				onHFlip();
				op = null;
			} else {
				fail("Unknown operation " + op);
			}
		}
		return res;
	}
	
	private void parseRotate(StringTokenizer tk) throws Exception {
		int angle = nextNumberToken(tk, ROTATE);
		onRotate(angle);
	}

	private String parseCrop(StringTokenizer tk) throws Exception {
		int x = nextNumberToken(tk, RESIZE);
		int y = nextNumberToken(tk, RESIZE);
		int w = nextNumberToken(tk, RESIZE);
		if (tk.hasMoreTokens()) {
			String h = tk.nextToken();
			if (isNumber(h)) {
				onCrop(x, y, w, Integer.parseInt(h));
				return null;
			} else {
				onCrop(x, y, w);
				return h;
			}
		} else {
			onCrop(x, y, w);
			return null;
		}
	}

	private String parseResize(StringTokenizer tk) throws Exception {
		int w = nextNumberToken(tk, RESIZE);
		if (tk.hasMoreTokens()) {
			String h = tk.nextToken();
			if (isNumber(h)) {
				onResize(w, Integer.parseInt(h));
				return null;
			} else {
				onResize(w, w);
				return h;
			}
		} else {
			onResize(w, w);
			return null;
		}
	}

	private void checkHasToken(StringTokenizer tk, String op) throws Exception {
		if (!tk.hasMoreTokens()) {
			fail("Not enough params for " + op);
		}
	}

	private int nextNumberToken(StringTokenizer tk, String op) throws Exception {
		checkHasToken(tk, op);
		String v = tk.nextToken();
		if (!isNumber(v)) {
			fail("String found where " + op + " param expected.");
			return 0; // cannot be reached
		} else {
			return Integer.parseInt(v);
		}
	}

	private boolean isNumber(String num) {
		return NUMBER_CHECK.matcher(num).matches();
	}

	private void fail(String msg) throws Exception {
		throw new Exception(msg);
	}
}
