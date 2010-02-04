package com.honzasterba.resizr;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.images.CompositeTransform;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.honzasterba.resizr.cache.Cachr;
import com.honzasterba.resizr.img.Fetchr;
import com.honzasterba.resizr.img.Parsr;

@SuppressWarnings("serial")
public class ImageResizerServlet extends HttpServlet {

	public static final String PARAM_URL = "url";

	public static final String PARAM_OP = "op";

	public static final Map<Image.Format, String> FORMAT_TO_TYPE = new HashMap<Image.Format, String>();

	static {
		FORMAT_TO_TYPE.put(Image.Format.BMP, "image/bitmap");
		FORMAT_TO_TYPE.put(Image.Format.GIF, "image/gif");
		FORMAT_TO_TYPE.put(Image.Format.JPEG, "image/jpeg");
		FORMAT_TO_TYPE.put(Image.Format.PNG, "image/png");
		FORMAT_TO_TYPE.put(Image.Format.TIFF, "image/tiff");
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String url = req.getParameter(PARAM_URL);
		String ops = req.getParameter(PARAM_OP);
		if (url == null) {
			fail(resp, "No image URL given.");
			return;
		}
		Image result = Cachr.get(url, ops);
		if (result != null) {
			render(resp, result);
		} else {
			result = fetchAndTransform(resp, url, ops);
			if (result != null) {
				Cachr.put(url, ops, result.getImageData());
			} else {
				// there was an error
				return;
			}
			render(resp, result);
		}
		
	}

	private Image fetchAndTransform(HttpServletResponse resp, String url,
			String ops) throws IOException {
		Image img = null;
		try {
			img = Fetchr.fetchImage(url);
		} catch (Exception e) {
			fail(resp, "Unable to load image from " + url + ".", e);
			return null;
		}
		CompositeTransform composite = ImagesServiceFactory
				.makeCompositeTransform();
		Parsr p = makeParsr(composite, img);
		boolean transformed = false;
		try {
			transformed = p.parse(ops);
		} catch (Exception e) {
			fail(resp, "Unable to parse params.", e);
			return null;
		}
		if (transformed) {
			return ImagesServiceFactory.getImagesService().applyTransform(
					composite, img);
		} else {
			return img;
		}
	}

	private void render(HttpServletResponse resp, Image img) throws IOException {
		resp.setContentType(FORMAT_TO_TYPE.get(img.getFormat()));
		resp.getOutputStream().write(img.getImageData());
	}

	private void fail(HttpServletResponse resp, String message)
			throws IOException {
		fail(resp, message, null);
	}

	private void fail(HttpServletResponse resp, String message, Exception cause)
			throws IOException {
		PrintWriter out = resp.getWriter();
		resp.setContentType("text/plain");
		resp.setStatus(400);
		out.println("ERROR:");
		out.println(message);
		out.println();
		if (cause != null) {
			cause.printStackTrace(out);
		}
	}

	private Parsr makeParsr(final CompositeTransform composite,
			final Image image) {
		return new Parsr() {
			@Override
			protected void onVFlip() {
				composite.concatenate(ImagesServiceFactory.makeVerticalFlip());
			}

			@Override
			protected void onRotate(int angle) {
				composite.concatenate(ImagesServiceFactory.makeRotate(angle));
			}

			@Override
			protected void onResize(int width, int height) {
				composite.concatenate(ImagesServiceFactory.makeResize(width,
						height));
			}

			@Override
			protected void onHFlip() {
				composite
						.concatenate(ImagesServiceFactory.makeHorizontalFlip());
			}

			@Override
			protected void onCrop(int x, int y, int with, int height) {
				crop(x, y, x + with, y + height);
			}

			@Override
			protected void onCrop(int x, int y, int radius) {
				crop(x - radius, y - radius, x + radius, y + radius);
			}

			private void crop(int ix0, int iy0, int ix1, int iy1) {
				double x0 = ix0 / ((double) image.getWidth());
				double y0 = iy0 / ((double) image.getHeight());
				double x1 = ix1 / ((double) image.getWidth());
				double y1 = iy1 / ((double) image.getHeight());
				if (x0 < 0) {
					x0 = 0;
				}
				if (y0 < 0) {
					y0 = 0;
				}
				if (x1 > 1) {
					x1 = 1;
				}
				if (y1 > 1) {
					y1 = 1;
				}
				composite.concatenate(ImagesServiceFactory.makeCrop(x0, y0, x1,
						y1));
			}
		};
	}

}
