package net.Juxi.icvision.yarp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import net.Juxi.icvision.test.R;
import net.Juxi.icvision.test.VisionGUI;
import net.Juxi.icvision.test.updateUIThread;

/**
 * YARPCommunicationThread provides a simple access to a
 * YARP port for an Android app via Sockets.
 * 
 * @author Juxi Leitner <juxi.leitner@gmail.com>
 */
public class YARPCommunicationThread implements Runnable {

	/**
	 * A network socket to connect to the icVision core.
	 */
	protected Socket socket = null;
	protected BufferedReader socketReader = null;
	protected BufferedWriter socketWriter = null;

	private String yarp_ip;
	private int yarp_port;
	protected VisionGUI gui;
	
	public static int TIMEOUT = 4000;

	/**
	 * The flag to indicate whether the app is connected to the yarp name server or
	 * not.
	 */
	public boolean isConnectedToYARPServer = false;

	public YARPCommunicationThread(String network_ip, int network_port,
			VisionGUI parent) {
		this.yarp_ip = network_ip;
		this.yarp_port = network_port;
		this.gui = parent;
	}

	// connectToYARPPortViaTCP();

	public void run() {
		// showDebugMessage("in the run!");
		if( connect() ) {
			while (!Thread.currentThread().isInterrupted())
				actionLoop();
		
			isConnectedToYARPServer = false;
			socket = null;
			socketReader = null;
			socketWriter = null;
		}
	}

	protected void showDebugMessage(String msg) {
		gui.updateUIHandler.post(new updateUIThread(gui, updateUIThread.DEBUG,
				msg));
	}

	protected void addToTextBox(String msg) {
		gui.updateUIHandler.post(new updateUIThread(gui, updateUIThread.TEXT,
				msg));
	}

	protected boolean connectToYARPServer() {
		try {
			InetAddress serverAddr = InetAddress.getByName(yarp_ip);
			InetSocketAddress yarpAddr = new InetSocketAddress(serverAddr, yarp_port);
			socket = new Socket();
			socket.connect(yarpAddr, TIMEOUT);
			socketWriter = new BufferedWriter(new OutputStreamWriter(
					this.socket.getOutputStream()));
			socketReader = new BufferedReader(new InputStreamReader(
					this.socket.getInputStream()));
//			OutputStreamWriter os = new OutputStreamWriter(
//					this.socket.getOutputStream());
//			InputStreamReader is = new InputStreamReader(
//					this.socket.getInputStream());
			//CharBuffer buf = CharBuffer.allocate(100);
			// int i = is.read(buf);
			// addToTextBox("bla: " + Integer.toString(i));
			// addToTextBox("bla: " + buf.toString());

			// first command sent needs to be CONNECT
			addToTextBox("<recvd> " + sendRequest(new Bottle("CONNECT icVisionTablet")));

//			s = sendRequest("help");
//			addToTextBox("> " + s);
			
			// we are connected :)
			isConnectedToYARPServer = true;
			addToTextBox("\nYARP Server connected!");
			//Thread.sleep(100);
			showDebugMessage(gui.getString(R.string.info_success));
		} catch (Exception e) {
			isConnectedToYARPServer = false;
			socket = null;
			socketReader = null;

			showDebugMessage(gui.getString(R.string.info_fail));
			addToTextBox("\n\n" + "Exception error is: " + e.toString());
			return false;
		}
		
		return true;		
	}
	
	protected Bottle sendRequest(Bottle b_in) throws IOException, InterruptedException {
		// debug info, not really necessary
		String req = b_in.toString();
		addToTextBox("> " + req);
		if(! req.endsWith("\n")) req += "\n";
		// YARP over TCP protocol
		// we need to send d first to talk to the owner of the port)
		// http://wiki.icub.org/yarpdoc/yarp_without_yarp.html

		if(! req.startsWith("CONNECT ")) {
			socketWriter.write("d\n");
			socketWriter.flush();
		}

		socketWriter.write(req);
		socketWriter.flush();
		socket.setSoTimeout(TIMEOUT);
		
        InputStream instr = socket.getInputStream();
        int buffSize = socket.getReceiveBufferSize();
        if (buffSize > 0) {
            byte[] buff = new byte[buffSize];
            int ret_read = instr.read(buff);
            return new Bottle(new String(buff, 0, ret_read));
        }else addToTextBox("!! buf not > 0 !!");

		return new Bottle();
	}
//	char[] buf = new char[4096];
//	int chars_read = 0;
//	do {
//	   chars_read = socketReader.read();
//	   s = s + chars_read;
//	   //buf.toString();
//	} while(chars_read != -1);
	
//	do {
//		if(socketReader.ready()) {
//			s += t;
//			t = socketReader.readLine();
//			addToTextBox("<recvd> " + t);
//		}
////		Thread.sleep(10);	// sleep for 10 ms
////		else Thread.sleep(100);
//	}while ( t != null );
//	}catch(IOException e) {
//		addToTextBox("\nException: " + e.toString());
//		return "";
//	}	
// debug info, not really necessary
//	socketWriter.write("help\n");socketWriter.flush();
//	do {
//		s = socketReader.readLine();
//		addToTextBox("<recvd> " + s);
//	} while (!s.isEmpty());
	
//	addToTextBox("<recvd> " + s);
//	addToTextBox("\n\n");
	


	protected boolean connect() {
		return connectToYARPServer();
	}

	protected void actionLoop() {
		showDebugMessage("test");
	}

}
