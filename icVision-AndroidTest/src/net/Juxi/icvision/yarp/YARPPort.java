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
 * NamedPort provides a simple access to a named
 * YARP port for an Android app via Sockets.
 * 
 * @author Juxi Leitner <juxi.leitner@gmail.com>
 */
public class YARPPort {
	/**
	 * A network socket to connect to the YARP nameserver.
	 */
	protected Socket socket = null;
	protected BufferedReader socketReader = null;
	protected BufferedWriter socketWriter = null;
	String ip;
	int port;
	
	public YARPPort(String ip, int port) throws IOException, InterruptedException {
		this.ip = ip;
		this.port = port;
		
		InetAddress serverAddr = InetAddress.getByName(ip);
		InetSocketAddress yarpAddr = new InetSocketAddress(serverAddr, port);
		socket = new Socket();
		socket.connect(yarpAddr, YARPCommunicationThread.TIMEOUT);
		socketWriter = new BufferedWriter(new OutputStreamWriter(
				this.socket.getOutputStream()));
		socketReader = new BufferedReader(new InputStreamReader(
				this.socket.getInputStream()));
		
		send(new Bottle("CONNECT icVisionTablet"));		
	}
	
	public Bottle send(Bottle b_in) throws IOException, InterruptedException {
		// debug info, not really necessary
		String req = b_in.toString();
		//addToTextBox("> " + req);
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
		socket.setSoTimeout(YARPCommunicationThread.TIMEOUT);
		
	    InputStream instr = socket.getInputStream();
	    int buffSize = socket.getReceiveBufferSize();
	    if (buffSize > 0) {
	        byte[] buff = new byte[buffSize];
	        int ret_read = instr.read(buff);
	        return new Bottle(new String(buff, 0, ret_read));
	    } else {
	    	//addToTextBox("!! buf not > 0 !!");
	    }
	
		return new Bottle();
	}
}	