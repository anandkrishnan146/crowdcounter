package com.dspcrowdcounter.OpenCVUtil;

public class Benchmark {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = null;
		final String outputFile = "/home/bwhisp/softwares/install-opencv/output/campus4c0.avi";
		// Check how many arguments were passed in
		if (args.length == 0) {
			// If no arguments were passed then default to local file
			url = "/home/bwhisp/Resource/campus4-c0.avi";
		} else {
			url = args[0];
		}
		
		PeopleCount.detect(url, outputFile);
		/* 
		 * 1 . Liste de fichiers
		
		 */
		
	}

}
