package org.usfirst.frc.team2839.robot;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * This is a demo program showing the use of OpenCV to do vision processing. The
 * image is acquired from the USB camera, then a rectangle is put on the image and
 * sent to the dashboard. OpenCV has many methods for different types of
 * processing.
 */
public class Robot extends IterativeRobot {
	Thread visionThread;
	
	private boolean frontCamOn = true;  //added

	@Override
	public void robotInit() {
		visionThread = new Thread(() -> {
			// Get the UsbCamera from CameraServer
			
			UsbCamera selected = null;
			
			UsbCamera camera1 = CameraServer.getInstance().startAutomaticCapture();
			// Set the resolution
			camera1.setResolution(320, 240);
			
			camera1.setFPS(0);  //added
			
			UsbCamera camera2 = CameraServer.getInstance().startAutomaticCapture();
			// Set the resolution
			camera2.setResolution(320, 240);
			
			camera2.setFPS(0);  //added


			// Get a CvSink. This will capture Mats from the camera
			CvSink cvSink = null;  //was CvSink cvSink = 				CameraServer.getInstance().getVideo(); from FIRST
			cvSink.setSource(selected);
			// Setup a CvSource. This will send images back to the Dashboard
			CvSource outputStream = CameraServer.getInstance().putVideo("Rectangle", 320, 240);

			// Mats are very memory expensive. Lets reuse this Mat.
			Mat mat = new Mat();

			// This cannot be 'true'. The program will never exit if it is. This
			// lets the robot stop this thread when restarting robot code or
			// deploying.
			while (!Thread.interrupted()) {
				// Tell the CvSink to grab a frame from the camera and put it
				// in the source mat.  If there is an error notify the output.
				
					
				

				if (frontCamOn)  {   //added

					cvSink.setEnabled(false);
					camera2.setFPS(0);
					camera1.setFPS(30);
					cvSink.setSource(camera1);
					cvSink.setEnabled(true);
					selected=camera1;
					cvSink.grabFrame(mat);
					
					Imgproc.rectangle(mat, new Point(100, 100), new Point(400, 400),
							new Scalar(255, 255, 255), 5);
					outputStream.putFrame(mat);

				}
				else {
					cvSink.setEnabled(false);
					camera1.setFPS(0);
					camera2.setFPS(30);
					cvSink.setSource(camera2);
					cvSink.setEnabled(true);
					selected=camera2;
					cvSink.grabFrame(mat);
					
					Imgproc.rectangle(mat, new Point(100, 100), new Point(400, 400),
							new Scalar(255, 255, 255), 5);
					outputStream.putFrame(mat);

				}
				
				if (cvSink.grabFrame(mat) == 0) {		//original FIRST code
				 //Send the output the error.
				outputStream.notifyError(cvSink.getError());
				 //skip the rest of the current iteration
				continue;
				}
					
				// Put a rectangle on the image
				
				// Give the output stream a new image to display
				outputStream.putFrame(mat);
			}
		});
		visionThread.setDaemon(true);
		visionThread.start();
	}
}
