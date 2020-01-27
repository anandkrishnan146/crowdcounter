package com.dspcrowdcounter.OpenCVUtil;

import java.io.IOException;
import java.util.ArrayList;
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
import org.opencv.objdetect.CascadeClassifier;

import com.dspcrowdcounter.OpenCVUtil.PeopleTrack;

final class PeopleCount {

	/**
	 * Logger.
	 */
	// CHECKSTYLE:OFF ConstantName - Logger is static final, not a constant
	private static final Logger logger = Logger.getLogger(PeopleDetect.class // NOPMD
			.getName());
	// CHECKSTYLE:ON ConstantName
	/* Load the OpenCV system library */
	static {
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // NOPMD
	}

	private PeopleCount() {
		throw new AssertionError();
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

	public static void detect(String url, final String outputFile) {
		// Custom logging properties via class loader
		try {
			LogManager.getLogManager().readConfiguration(
					PeopleDetect.class.getClassLoader().getResourceAsStream(
							"logging.properties"));
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

		// File infos
		logger.log(Level.INFO, String.format("Input file: %s", url));
		logger.log(Level.INFO, String.format("Output file: %s", outputFile));

		// Handlers for the video
		final VideoCapture videoCapture = new VideoCapture(url);
		final Size frameSize = new Size(
				(int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH),
				(int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT));

		logger.log(Level.INFO, String.format("Resolution: %s", frameSize));

		final FourCC fourCC = new FourCC("X264");
		final VideoWriter videoWriter = new VideoWriter(outputFile,
				fourCC.toInt(), videoCapture.get(Videoio.CAP_PROP_FPS),
				frameSize, true);
		Mat mat = new Mat();

		// Preliminaries for person detection
		final HOGDescriptor hog = new HOGDescriptor();
		final MatOfFloat descriptors = HOGDescriptor.getDefaultPeopleDetector();
		hog.setSVMDetector(descriptors);

		final MatOfRect foundPersons = new MatOfRect();
		final MatOfDouble foundWeights = new MatOfDouble();
		final Size winStride = new Size(8, 8);
		final Size padding = new Size(32, 32);
		final Point rectPoint1 = new Point();
		final Point rectPoint2 = new Point();
		final Point fontPoint = new Point(24, 24);

		
		// Preliminaries for faces detection
		CascadeClassifier faceDetector = new CascadeClassifier(
				"/home/bwhisp/Resource/haarcascade.xml");
		MatOfRect foundFaces = new MatOfRect();

		// Misc variables
		final Scalar rectColor = new Scalar(0, 255, 0);
		final Scalar faceColor = new Scalar(0, 0, 255);
		final Scalar fontColor = new Scalar(255, 255, 255);

		// Algorithm variables
		HeapList<MatOfRect> previousDetections = new HeapList<MatOfRect>(2);
		int framesNoPeople = 0;
		int soldePersons = 0;
		int faces = 0;
		int persons = 0;

		while (videoCapture.read(mat)) {

			// Persons detection
			hog.detectMultiScale(mat, foundPersons, foundWeights, 0.0,
					winStride, padding, 1.05, 2.0, false);

			// Faces detection
			faceDetector.detectMultiScale(mat, foundFaces);

			if (foundPersons.rows() > 0) {

				// if (framesNoPeople > 2) {
				// soldePersons++;
				// }
				// framesNoPeople = 0;

				List<Double> weightList = foundWeights.toList();
				List<Rect> rectList = foundPersons.toList();

				for (Rect rect : rectList) { // Draws rectangles around people
					rectPoint1.x = rect.x;
					rectPoint1.y = rect.y;
					rectPoint2.x = rect.x + rect.width;
					rectPoint2.y = rect.y + rect.height;
					// Draw rectangle around fond object
					Imgproc.rectangle(mat, rectPoint1, rectPoint2, rectColor, 2);
					// CHECKSTYLE:ON MagicNumber
				}
				soldePersons += PeopleTrack.countNewPersons(foundPersons,
						previousDetections, foundFaces);

				for (Rect rect : foundFaces.toArray()) {
					// Draw rectangles around faces
					rectPoint1.x = rect.x;
					rectPoint1.y = rect.y;
					rectPoint2.x = rect.x + rect.width;
					rectPoint2.y = rect.y + rect.height;

					Imgproc.rectangle(mat, rectPoint1, rectPoint2, faceColor);
				}

			} else {
				// framesNoPeople++;
			}

			Imgproc.putText(mat,
					String.format("People counted : %d", soldePersons),
					fontPoint, Core.FONT_HERSHEY_PLAIN, 0.8, fontColor, 2,
					Core.LINE_AA, false);

			previousDetections.queue(new MatOfRect(foundPersons));
			videoWriter.write(mat);
		}
		// CHECKSTYLE:ON MagicNumber

		System.out.println("Persons counted : " + soldePersons);

		/*
		 * // Release native memory videoCapture.free(); videoWriter.free(); hog.free();
		 * descriptors.free(); foundPersons.free(); foundWeights.free(); mat.free();
		 * 
		 */
		
		videoCapture.release();
		videoWriter.release();
		//hog.re
		descriptors.release();
		foundPersons.release();
		foundWeights.release();
		mat.release();
	}

}
