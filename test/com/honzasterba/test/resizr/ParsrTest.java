package com.honzasterba.test.resizr;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.honzasterba.resizr.img.Parsr;

public class ParsrTest extends TestCase {

	private TestParsr p;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		p = new TestParsr();
	}

	public void testHFlip() throws Exception {
		p.parse("hflip");
		assertEquals("hflip", p.calls.get(0)[0]);
	}

	public void testVFlip() throws Exception {
		p.parse("vflip");
		assertEquals("vflip", p.calls.get(0)[0]);
	}

	public void testResize() throws Exception {
		p.parse("resize,10,20");
		assertEquals("resize", p.calls.get(0)[0]);
	}
}

class TestParsr extends Parsr {
	List<Object[]> calls = new ArrayList<Object[]>();

	@Override
	protected void onCrop(int x, int y, int radius) {
		calls.add(new Object[] { "crop3", x, y, radius });
	}

	@Override
	protected void onCrop(int x, int y, int with, int height) {
		calls.add(new Object[] { "crop4", x, y, with, height });
	}

	@Override
	protected void onHFlip() {
		calls.add(new Object[] { "hflip" });
	}

	@Override
	protected void onResize(int width, int height) {
		calls.add(new Object[] { "resize", width, height });
	}

	@Override
	protected void onRotate(int angle) {
		calls.add(new Object[] { "rotate", angle });
	}

	@Override
	protected void onVFlip() {
		calls.add(new Object[] { "vflip" });
	}

}