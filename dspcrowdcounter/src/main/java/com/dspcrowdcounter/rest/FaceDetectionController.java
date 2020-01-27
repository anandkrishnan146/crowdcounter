package com.dspcrowdcounter.rest;

import org.springframework.web.bind.annotation.RestController;

import com.dspcrowdcounter.OpenCVUtil.FaceDetectorVid;
import com.dspcrowdcounter.OpenCVUtil.PeopleDetect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class FaceDetectionController {
	
@Autowired 
FaceDetectorVid FaceDetectorVid;




	@RequestMapping("/crowdcounter/feed")
	public String videoFeeder() {
		//PeopleDetect peopleDetect = new PeopleDetect();
		//peopleDetect.detect("C:\\Users\\Administrator\\Downloads\\videoplayback.mp4", "");
		
		FaceDetectorVid.detectFace();
		return "Feeding the live video to server";
	}

}
