package net.Juxi.icvision.test;

import java.io.IOException;

import net.Juxi.icvision.yarp.Bottle;
import net.Juxi.icvision.yarp.YARPCommunicationThread;

/**
 * icVisionCommThread provides the communication with the 
 * icVision Core module via YARP (over TCP)
 * 
 * @author Juxi Leitner <juxi.leitner@gmail.com>
 */
public class icVisionCommThread extends YARPCommunicationThread {
	public enum state{ IDLE, UPDATE_MODULES };
	private state s;

	/**
	 * The flag to indicate whether the app is connected to the icVision core or
	 * not.
	 */
	public boolean isConnectedToCore = false;

	public icVisionCommThread(String network_ip, int network_port, VisionGUI visionGUI) {
		super(network_ip, network_port, visionGUI);
	}

	@Override
	protected boolean connect() {
		if(connectToYARPServer() && connectToicVisionCore()) {
			isConnectedToCore = true;
		} else {
			isConnectedToCore = false;
		}
		
		return isConnectedToCore;		
	}
	
	private boolean connectToicVisionCore() {
		// s = sendRequest("help");	// addToTextBox("> " + s);
		try {
			// communication with the nameserver -->
			// > d   
			// > query /icVision/rpc:i
			// < registration name /icVision/rpc:i ip 192.168.27.203 port 10011 type tcp
			Bottle b = sendRequest(new Bottle("query /icVision/rpc:i"));
			if(b.isEmpty()) return false;
			String ip = b.getElementAt(5);
			int port = b.getElementAsInt(7);
		}catch(Exception e) { return false; }
		return true;
	}
	
	@Override
	protected void actionLoop() {
		addToTextBox("myla\n\n");
		showDebugMessage("icVisionTest");
		try {
			socketWriter.write("help"); socketWriter.newLine();
			showDebugMessage(socketReader.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
