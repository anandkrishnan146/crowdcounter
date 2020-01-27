package com.dspcrowdcounter.OpenCVUtil;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.springframework.stereotype.Component;
@Component
public class PeopleDetect {

	/**
	 * Logger.
	 */
	// CHECKSTYLE:OFF ConstantName - Logger is static final, not a constant
	private static final Logger logger = Logger.getLogger(PeopleDetect.class // NOPMD
			.getName());
	// CHECKSTYLE:ON ConstantName
	/* Load the OpenCV system library */
	static {
	System.loadLibrary("opencv_java420"); // NOPMD
	}

	/**
	 * Suppress default constructor for noninstantiability.
	 */
	public PeopleDetect() {
	//	throw new AssertionError();
	}

	/**
	 * Create window, frame and set window to visible.
	 *
	 * args[0] = source file or will default to "../resources/walking.mp4" if no
	 * args passed.
	 *
	 * @param args
	 *            String array of arguments.
	 */
	public  void detect(String url, final String outputFile) {
		// Custom logging properties via class loader
		/*
		 * try { LogManager.getLogManager().readConfiguration(
		 * PeopleDetect.class.getClassLoader().getResourceAsStream(
		 * "logging.properties")); } catch (SecurityException | IOException e) {
		 * e.printStackTrace(); }
		 */
		/*
		 * logger.log(Level.INFO, String.format("OpenCV %s", Core.VERSION));
		 * logger.log(Level.INFO, String.format("Input file: %s", url));
		 * logger.log(Level.INFO, String.format("Output file: %s", outputFile));
		 */

		final VideoCapture videoCapture = new VideoCapture(url);
		final Size frameSize = new Size(
				(int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH),
				(int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT));

		//logger.log(Level.INFO, String.format("Resolution: %s", frameSize));

		final FourCC fourCC = new FourCC("X264");
		final VideoWriter videoWriter = new VideoWriter(outputFile,
				fourCC.toInt(), videoCapture.get(Videoio.CAP_PROP_FPS),
				frameSize, true);
		Mat mat = new Mat();
		// final HOGDescriptor hog = new HOGDescriptor(new Size(128, 64),
		// new Size(16, 16), new Size(8, 8), new Size(8, 8), 9, 0, -1, 0,
		// 0.2, false, 64);
		final HOGDescriptor hog = new HOGDescriptor();
		final MatOfFloat descriptors = HOGDescriptor.getDefaultPeopleDetector();
		hog.setSVMDetector(descriptors);
		final MatOfRect foundLocations = new MatOfRect();
		final MatOfDouble foundWeights = new MatOfDouble();
		final Size winStride = new Size(8, 8);
		final Size padding = new Size(32, 32);
		final Point rectPoint1 = new Point();
		final Point rectPoint2 = new Point();
		final Point fontPoint = new Point();
		int frames = 0;
		int framesWithPeople = 0;
		final Scalar rectColor = new Scalar(0, 255, 0);
		final Scalar fontColor = new Scalar(255, 255, 255);
		final long startTime = System.currentTimeMillis();

		int persons = 0;

		while (videoCapture.read(mat)) {
			if (frames % 2 == 1) {
				frames++;
				continue;
			}
			mat = mat.t();
			// CHECKSTYLE:OFF MagicNumber - Magic numbers here for illustration
			hog.detectMultiScale(mat, foundLocations, foundWeights, 0.0,
					winStride, padding, 1.05, 2.0, false);
			// CHECKSTYLE:ON MagicNumber
			if (foundLocations.rows() > 0) {
				framesWithPeople++;
				List<Double> weightList = foundWeights.toList();
				List<Rect> rectList = foundLocations.toList();
				int i = 0;
				for (Rect rect : rectList) {
					rectPoint1.x = rect.x;
					rectPoint1.y = rect.y;
					rectPoint2.x = rect.x + rect.width;
					rectPoint2.y = rect.y + rect.height;
					// Draw rectangle around fond object
					Imgproc.rectangle(mat, rectPoint1, rectPoint2, rectColor, 2);
					fontPoint.x = rect.x;
					// CHECKSTYLE:OFF MagicNumber - Magic numbers here for
					// illustration
					fontPoint.y = rect.y - 4;
					// CHECKSTYLE:ON MagicNumber
					// Print weight
					// CHECKSTYLE:OFF MagicNumber - Magic numbers here for
					// illustration
					Imgproc.putText(mat,
							String.format("%1.2f", weightList.get(i)),
							fontPoint, Core.FONT_HERSHEY_PLAIN, 1.5, fontColor,
							2, Core.LINE_AA, false);
					// CHECKSTYLE:ON MagicNumber
					i++;
				}
			}

			// ptit code maison
			// if ((frames + 1) % 30 == 0) {
			// persons += foundLocations.toList().size();
			// persons = persons / 30;
			// logger.log(Level.INFO, String.format(
			// "Second %d : %d persons per frame", frames / 30,
			// persons));
			// persons = 0;
			// } else {
			// persons += foundLocations.toList().size();
			// }

			videoWriter.write(mat);
			frames++;
		}

		final long estimatedTime = System.currentTimeMillis() - startTime;
		//logger.log(Level.INFO, String.format(
			//	"%d frames, %d frames with people", frames, framesWithPeople));
		// CHECKSTYLE:OFF MagicNumber - Magic numbers here for illustration
		//logger.log(Level.INFO, String.format("Elapsed time: %4.2f seconds",
			//	(double) estimatedTime / 1000));
		// CHECKSTYLE:ON MagicNumber
		// Release native memory
		/*
		 * videoCapture.free(); videoWriter.free(); hog.free(); descriptors.free();
		 * foundLocations.free(); foundWeights.free(); mat.free();
		 */
	}
}
