package net.Juxi.icvision.test;

import net.Juxi.icvision.test.util.SystemUiHider;
import net.Juxi.icvision.yarp.YARPCommunicationThread;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.view.*;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class VisionGUI extends Activity {

	public static final String EXTRA_MESSAGE = "net.Juxi.icvision.MESSAGE";

	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3500;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	// private static final int HIDER_FLAGS =
	// SystemUiHider.FLAG_HIDE_NAVIGATION;

	public Handler updateUIHandler = null;
	
	private ImageButton robotButton;
	private TextView robotButtonText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vision_gui);
		updateUIHandler = new Handler();
		
		TextView txtLog = (TextView) findViewById(R.id.txtLog);
		txtLog.setMovementMethod(new ScrollingMovementMethod());
		
		// disable robot for now
		robotButton = (ImageButton) findViewById(R.id.btnRobot);
		robotButton.setVisibility(View.VISIBLE);
		//robotButton.setVisibility(View.INVISIBLE);
		robotButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openModuleList();
			}
		});
		robotButtonText = (TextView) findViewById(R.id.txtRobotButton);
		robotButtonText.setVisibility(View.VISIBLE);
		//robotButton.setVisibility(View.INVISIBLE);
		robotButtonText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openModuleList();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.icvision_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_refresh:
			connectToCore();
			return true;
		case R.id.action_settings:
			openSettings(item.getActionView());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void openSettings(View view) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	public void openInfo() {
		// TODO improvement

	}

	public void openModuleList() {
		// start the moduleListActivity
		Intent intent = new Intent(this, moduleListActivity.class);
		startActivity(intent);
	}

	// ic_media_pause

	public boolean alreadyConnected() {
		if (icVision == null)
			return false;
		if (icVision.isConnectedToCore) {
			// TODO check if we are really connected
			return true;
		}
		return false;
	}

	public boolean networkAvailable() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	public boolean updateIP() {
		WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

		String hostaddr = "n/a";
		try {
			byte[] ipAddress = BigInteger.valueOf(wm.getDhcpInfo().ipAddress)
					.toByteArray();
			InetAddress myaddr;
			myaddr = InetAddress.getByAddress(ipAddress);
			hostaddr = myaddr.getHostAddress();
		} catch (UnknownHostException e) {
		}

		return true;
	}

	/**
	 * YARPCommunicationThread
	 */
	//private YARPCommunicationThread yarp = null;
	private icVisionCommThread icVision = null;

	/** Called when the user clicks the Send button */
	public void connectToCore() {
		// Do something in response to button press
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;

		// check if we are connected already
		if (!alreadyConnected()) {
			if (networkAvailable()) {
				addToTextBox("Network is available!");
				// get local ip for debug only
				// updateIP();

				// inform the user of what's going on
				SharedPreferences sharedPref = PreferenceManager
						.getDefaultSharedPreferences(context);
				String network_ip = sharedPref.getString("yarp_ip", "n/a");
				int network_port = Integer.parseInt(sharedPref.getString(
						"yarp_port", "n/a"));
				String text = getString(R.string.info_connecting);
				text += " (" + network_ip + ":" + network_port + ") ...";
				Toast toast = Toast.makeText(context, text, duration);
				toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
				toast.show();

				// try to connect to the icVision core
				//yarp = new YARPCommunicationThread(network_ip, network_port, this);
				icVision = new icVisionCommThread(network_ip, network_port, this);
				try {
					new Thread(icVision).start();
				} catch (Exception e) {
					// addToTextBox("lala\n\n");
					addToTextBox(e.toString());
				}

				// need to give it time!
				// the thread will let the user know .. hopefully :)
				// // let the user know
				// if(yarp.isConnectedToCore) text =
				// getString(R.string.info_success);
				// else text = getString(R.string.info_fail);
				//
				// toast = Toast.makeText(context, text, duration);
				// toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
				// toast.show();

			} else {
				CharSequence text = getString(R.string.info_no_network);
				Toast toast = Toast.makeText(context, text, duration);
				toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
				toast.show();
			}
		}
	}

	protected void addToTextBox(String msg) {
		TextView text = (TextView) findViewById(R.id.txtLog);
		if (!msg.endsWith("\n"))
			msg += "\n";
		text.setFocusable(true);
		text.setText(text.getText() + msg);
		
		// do we need to scroll down in the text

		// find the amount we need to scroll.  This works by
	    // asking the TextView's internal layout for the position
	    // of the final line and then subtracting the TextView's height
	    final int scrollAmount = text.getLayout().getLineTop(text.getLineCount())
	    		- text.getHeight();
	    
	    // if there is no need to scroll, scrollAmount will be <=0
	    if (scrollAmount > 0)
	        text.scrollTo(0, scrollAmount);
	    else
	        text.scrollTo(0, 0);
	}

	protected void showDebugMessage(String msg) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, msg, duration);
		toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
		toast.show();
	}

	public void onRobotUpdate() {
		robotButtonText.setText(icVision.getRobotName());
		// 	update the image?		
		robotButton.setImageDrawable(getResources().getDrawable( R.drawable.ic_icsim ));
		if(icVision.getRobotName().startsWith("icub")) {
			robotButton.setImageDrawable(getResources().getDrawable( R.drawable.ic_icub ));
			addToTextBox("HA>A:");			
		}
		robotButton.setVisibility(View.VISIBLE);
		robotButtonText.setVisibility(View.VISIBLE);
	}
}