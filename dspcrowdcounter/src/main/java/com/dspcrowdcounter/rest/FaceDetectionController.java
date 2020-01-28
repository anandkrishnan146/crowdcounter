package com.dspcrowdcounter.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dspcrowdcounter.OpenCVUtil.FaceDetectorVid;

@RestController
public class FaceDetectionController {

	@Autowired
	FaceDetectorVid FaceDetectorVid;

	@RequestMapping("/crowdcounter/feed")
	public String videoFeeder() {
		// PeopleDetect peopleDetect = new PeopleDetect();
		// peopleDetect.detect("C:\\Users\\Administrator\\Downloads\\videoplayback.mp4",
		// "");
		//FaceDetectorVid.detectFace();
		return "Feeding the live video to server";
	}


	 @MessageMapping("/news")
	 @SendTo("/topic/news") 
	 public String broadcastNews(@Payload String message) {
	 return message;
	 }
	 
	/*
	 * @MessageMapping("/news") public void broadcastNews(@Payload String message) {
	 * this.simpMessagingTemplate.convertAndSend("/topic/news", message) }
	 */

}
