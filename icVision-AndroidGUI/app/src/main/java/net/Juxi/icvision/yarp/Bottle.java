package net.Juxi.icvision.yarp;
/**
 * Bottle is a very basic implementation of a YARP bottle
 * 
 * @author Juxi Leitner <juxi.leitner@gmail.com>
 */
public class Bottle {
	private String content;
	
	public Bottle() { content = ""; }
	public Bottle(String s) { content = s; }
	
	public boolean isEmpty() { return content.isEmpty(); }
	public String toString() { return content; }

	public String getElementAt(int at) {
		if(isEmpty()) return "";
		String tokens[] = content.split(" ");
		if((tokens.length - 1) < at) return "";
		return tokens[at];
	}
	public String getElement(int at) { return getElementAt(at); }
	
	public int getElementAsInt(int at) throws NumberFormatException {
		return Integer.parseInt(getElementAt(at));
	}
	public String getElementAsString(int at) {
		return getElementAt(at);
	}
//	public boolean getElementAsBoolean(int at) {
//		return Boolean.parseBoolean(getElementAt(at));
//	}

}
