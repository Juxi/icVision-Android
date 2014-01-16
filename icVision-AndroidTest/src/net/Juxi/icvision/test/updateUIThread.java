package net.Juxi.icvision.test;

public class updateUIThread implements Runnable {
	private String msg;
	private VisionGUI gui;
	private int type;
	
	public static final int DEBUG = 1;
	public static final int TEXT  = 2;
	public static final int ROBOTUPDATE  = 3;
	
	public updateUIThread(VisionGUI gui, int type) { 
		this.gui = gui; this.msg = ""; this.type = type;
	}

	public updateUIThread(VisionGUI gui, int type, String str) { 
		this.gui = gui; this.msg = str; this.type = type;
	}
	
	@Override
	public void run() {
		switch(type) {
			case DEBUG:
				gui.showDebugMessage(msg);
				break;
			case TEXT:
				gui.addToTextBox(msg + "\n");
				break;
			case ROBOTUPDATE:
				gui.onRobotUpdate();
				break;
		}
	}
}
