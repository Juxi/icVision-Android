package net.Juxi.icvision.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import net.Juxi.icvision.yarp.Bottle;
import net.Juxi.icvision.yarp.YARPCommunicationThread;
import net.Juxi.icvision.yarp.YARPPort;

/**
 * icVisionCommThread provides the communication with the icVision Core module
 * via YARP (over TCP)
 * 
 * @author Juxi Leitner <juxi.leitner@gmail.com>
 */
public class icVisionCommThread extends YARPCommunicationThread {
	public enum state {
		IDLE, UPDATE_MODULES
	};

	private state s;
	private YARPPort icVisionPort;
	private String robotName;

	/**
	 * The flag to indicate whether the app is connected to the icVision core or
	 * not.
	 */
	public boolean isConnectedToCore = false;

	public icVisionCommThread(String network_ip, int network_port,
			VisionGUI visionGUI) {
		super(network_ip, network_port, visionGUI);
		s = state.IDLE;
	}

	@Override
	protected boolean connect() {
		if (connectToYARPServer(false)) {
			if (connectToicVisionCore()) {
				isConnectedToCore = true;
			} else {
				isConnectedToCore = false;
			}
		} else
			isConnectedToCore = false;

		return isConnectedToCore;
	}

	private boolean connectToicVisionCore() {
		try {
			// communication with the nameserver -->
			// > d
			// > query /icVision/rpc:i
			// < registration name /icVision/rpc:i ip 192.168.27.203 port 10011
			// type tcp
			// Bottle s = sendRequest(new Bottle("help"));
			// addToTextBox("> " + s);

			Bottle b = sendRequest(new Bottle("query /icVision/rpc:i"));
			//Bottle b = yarp.send(new Bottle("query /icVision/rpc:i"));
			addToTextBox("<recvd> " + b);
			
			// sanity check of reply
			if (b.isEmpty()) {
				addToTextBox("empty reply");
				return false;
			}
			if (b.toString().startsWith("*** end")) {
				showDebugMessage(gui.getString(R.string.info_fail));
				addToTextBox("no icVision core found! start it up please :)");
				return false;
			}
			
			// parsing of reply
			String ip = b.getElementAt(4);
			int port = b.getElementAsInt(6);

			addToTextBox("icVision RPC found @" + ip + ":" + port);
			
			icVisionPort = new YARPPort(ip, port);

			b = icVisionPort.send(new Bottle("conf"));
			if(! b.isEmpty()) {
				// get robot name from conf
				// robotName = "icub"; 
				// is there more in the conf? no not yet..
				
				robotName = b.toString();
				addToTextBox("Robot found: " + b);

				// update the robot info in the gui (mainly name and show button)
				gui.updateUIHandler.post(new updateUIThread(gui, updateUIThread.ROBOTUPDATE));
			}
			
			//	s = state.UPDATE_MODULES;
			update_icVisionFilterList();
		
			showDebugMessage(gui.getString(R.string.info_success));
		} catch (Exception e) {
			showDebugMessage(gui.getString(R.string.info_fail));
			addToTextBox("connection exception: " + e);
			return false;
		}
		return true;
	}

	private boolean update_icVisionFilterList() {
		try {		
			Bottle b = icVisionPort.send(new Bottle("list"));
			addToTextBox("<recvd> " + b);
	
			if(! b.isEmpty()) {
				// TODO keep track of the objects in the list
	
			}

		}catch(Exception e) {
			showDebugMessage(gui.getString(R.string.info_fail));
			addToTextBox("exception: " + e);
			return false;			
		}
		return true;
	}

	
	public String getRobotName() {
		return robotName;
	}
	
	@Override
	protected void actionLoop() {
//		if(s == state.IDLE) return;
		
		// addToTextBox("in the action loop of icVisionComm\n\n");
		
//		if(s == state.UPDATE_MODULES)
//			update_icVisionFilterList();

//		showDebugMessage("icVisionTest");
//		try {
//			socketWriter.write("help");
//			socketWriter.newLine();
//			showDebugMessage(socketReader.readLine());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}
	
}
