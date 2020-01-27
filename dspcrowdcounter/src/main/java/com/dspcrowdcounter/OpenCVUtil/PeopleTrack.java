package com.dspcrowdcounter.OpenCVUtil;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.Point;

class PeopleTrack {

	@SuppressWarnings("unchecked")
	public static int countNewPersons(MatOfRect currentDetection,
			HeapList<MatOfRect> previousDetections, MatOfRect facesDetection) {
		int counter = 0;

		for (Rect person : currentDetection.toList()) {
			if (isNewPerson(person,
					(HeapList<MatOfRect>) previousDetections.clone())) {
				if (personHasFace(person, facesDetection)) {
					counter--;
				} else {
					counter++;
				}

			}
		}

		return counter;
	}

	public static boolean isNewPerson(Rect person,
			HeapList<MatOfRect> previousDetections) {
		boolean result = true;

		for (MatOfRect selectedDetection : previousDetections) {
			List<Rect> rectList = selectedDetection.toList();
			for (Rect previousPerson : rectList) {
				if (isNear(person, previousPerson, 1)) {
					result = false;
					rectList.remove(previousPerson);
					break;
				}
			}
		}

		return result;
	}

	private static boolean isNear(Rect person, Rect previousPerson, double ratio) {
		Point personCenter = new Point(person.x + person.width / 2, person.y
				+ person.height / 2);
		Point previousCenter = new Point(previousPerson.x
				+ previousPerson.width / 2, previousPerson.y
				+ previousPerson.height / 2);

		Point maxAllowed = new Point(ratio
				* (person.width + previousPerson.width) / 2, ratio
				* (person.height + previousPerson.height) / 2);

		Point movement = new Point(previousCenter.x - personCenter.x,
				previousCenter.y - personCenter.y);

		if (movement.x < maxAllowed.x && movement.y < maxAllowed.y) {
			return true;
		}

		return false;
	}

	private static boolean personHasFace(Rect person, MatOfRect facesDetected) {
		double valsOrigin[] = {0,0};
		double valsEnd[] = {0,0};
		
		Point origin = new Point();
		Point end = new Point();
		
		for (Rect face : facesDetected.toList()) {
			valsOrigin[0] = face.x;
			valsOrigin[1] = face.y;
			
			valsEnd[0] = face.x + face.width;
			valsEnd[1] = face.y + face.height;
			
			origin.set(valsOrigin);
			end.set(valsEnd);
			
			if (origin.inside(person) && end.inside(person)) {
				facesDetected.toList().remove(face);
			}
			
		}
		
		
		return false;
	}
}
