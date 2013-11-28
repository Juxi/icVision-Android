package net.Juxi.icvision.test;
import android.widget.TextView;

public class updateUIThread implements Runnable {
	private String msg;
	private VisionGUI gui;
	private int type;
	
	public static final int DEBUG = 1;
	public static final int TEXT  = 2;
	
	public updateUIThread(VisionGUI gui, int type, String str) { this.gui = gui; this.msg = str; this.type = type;}
	
	@Override
	public void run() {
		if(type == DEBUG) gui.showDebugMessage(msg);
		if(type == TEXT)  gui.addToTextBox(msg + "\n");
	}
}
