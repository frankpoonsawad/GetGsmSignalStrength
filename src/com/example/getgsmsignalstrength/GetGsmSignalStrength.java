package com.example.getgsmsignalstrength;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.telephony.NeighboringCellInfo;
import android.telephony.gsm.GsmCellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.net.TrafficStats;
import android.os.Process;

public class GetGsmSignalStrength extends Activity {

	/* This variables need to be global, so we can used them onResume and onPause method to
    stop the listener */
 TelephonyManager        Tel;
 MyPhoneStateListener    MyListener;
 String IMEI;
 String operator;
 List<NeighboringCellInfo> NeighboringList;
 int cellID;
 int lac;
 
 /*
  * Network type constants
  */
 public static final String NETWORK_CDMA = "CDMA: Either IS95A or IS95B (2G)";
 public static final String NETWORK_EDGE = "EDGE (2.75G)";
 public static final String NETWORK_GPRS = "GPRS (2.5G)";
 public static final String NETWORK_UMTS = "UMTS (3G)";
 public static final String NETWORK_EVDO_0 = "EVDO revision 0 (3G)";
 public static final String NETWORK_EVDO_A = "EVDO revision A (3G - Transitional)";
 public static final String NETWORK_EVDO_B = "EVDO revision B (3G - Transitional)";
 public static final String NETWORK_1X_RTT = "1xRTT  (2G - Transitional)";
 public static final String NETWORK_HSDPA = "HSDPA (3G - Transitional)";
 public static final String NETWORK_HSUPA = "HSUPA (3G - Transitional)";
 public static final String NETWORK_HSPA = "HSPA (3G - Transitional)";
 public static final String NETWORK_IDEN = "iDen (2G)";
 public static final String NETWORK_LTE = "LTE (4G)";
 public static final String NETWORK_EHRPD = "EHRPD (3G)";
 public static final String NETWORK_HSPAP = "HSPAP (3G)";
 public static final String NETWORK_UNKOWN = "Unknown";
 
 /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_get_gsm_signal_strength);

      /* Update the listener, and start it */
      MyListener   = new MyPhoneStateListener();
      
      Tel = ( TelephonyManager )getSystemService(Context.TELEPHONY_SERVICE);
          
      GsmCellLocation cellLocation = (GsmCellLocation) Tel.getCellLocation();
      
      Tel.listen(MyListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
      
      /*Variables here along with function values*/
      	
   		IMEI = Tel.getDeviceId();
	   	//net_type = mapNetworkTypeToName(Tel.getNetworkType());
	   	operator = Tel.getNetworkOperatorName();
	   	cellID = cellLocation.getCid();
	    lac = cellLocation.getLac();
	        		
	    
	   	TextView dID = (TextView)findViewById(R.id.deviceID);
	   	dID.setText("The IMEI is "+IMEI);
	   	
	   	TextView opr = (TextView)findViewById(R.id.operator);
	   	opr.setText("The Operator name is "+operator);
	   	
	   	TextView cal = (TextView)findViewById(R.id.cal);
	   	cal.setText("The Cell ID is "+ cellID + " & LAC is "+ lac);
	   	
	   	TextView network = (TextView) findViewById(R.id.network_type);
        network.setText("Network Type: " + mapNetworkTypeToName(Tel.getNetworkType()));
	   	
	   	TextView Neighbouring = (TextView)findViewById(R.id.neighbouring);
	    NeighboringList = Tel.getNeighboringCellInfo();
	    
	    String stringNeighboring = "Neighboring List- Lac : Cid : RSSI\n";
	       for(int i=0; i < NeighboringList.size(); i++){
	         
	        String dBm;
	        int rssi = NeighboringList.get(i).getRssi();
	        if(rssi == NeighboringCellInfo.UNKNOWN_RSSI){
	         dBm = "Unknown RSSI";
	        }else{
	         dBm = String.valueOf(-113 + 2 * rssi) + " dBm";
	        }
	 
	        stringNeighboring = stringNeighboring
	         + String.valueOf(NeighboringList.get(i).getLac()) +" : "
	         + String.valueOf(NeighboringList.get(i).getCid()) +" : "
	         + rssi +"\n";
	       }
	       
	       Neighbouring.setText(stringNeighboring);
	       
	       /*data use */
	       TextView appList = (TextView)findViewById(R.id.applist);
	       
		   final PackageManager packageManager = getPackageManager();
		   List<ApplicationInfo> installedApplications = 
		      packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
		   
		  		  
		   String listapp = "Application Name";
		   for (ApplicationInfo appInfo : installedApplications)
		   {
			   
		      // appList.setText("Package name : " + appInfo.packageName + " Name " + appInfo.loadLabel(packageManager)+ "\n");
		       //Log.d("OUTPUT", "Name: " + appInfo.loadLabel(packageManager));
			   
			   listapp = listapp 
					   //+ String.valueOf(appInfo.packageName)+":"
					   + appInfo.loadLabel(packageManager)+ "\n";
			   
		   } 
	       
		   appList.setText(listapp);
		   
	       /*Application Data USage Code*/
	       
	       TextView appdata = (TextView)findViewById(R.id.datause);
	       
	       TrafficStats stats = new TrafficStats();
	       TrafficStats.getMobileRxBytes();
	       TrafficStats.getTotalRxBytes();
	       
	       int uid = android.os.Process.myUid();
	       long txBytesInitial = TrafficStats.getUidTxBytes(uid);
	       long rxBytesInitial = TrafficStats.getUidRxBytes(uid);
	       
	       int myUid = Process.myUid();
		   ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			
		   for (RunningAppProcessInfo processInfo : activityManager.getRunningAppProcesses()) {
				appdata.setText(processInfo.processName + " hase used " + TrafficStats.getUidRxBytes(processInfo.uid) + " bytes");
			}//Data usage for this app ends here
		   
		   
		  	  
		   
  }

  /* Called when the application is minimized */
  @Override
 protected void onPause()
  {
    super.onPause();
    Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);
 }

  /* Called when the application resumes */
 @Override
 protected void onResume()
 {
    super.onResume();
    Tel.listen(MyListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
 }

 /* —————————– */
  /* Start the PhoneState listener */
 /* —————————– */
  private class MyPhoneStateListener extends PhoneStateListener
  {
    /* Get the Signal strength from the provider, each time there is an update */
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength)
    {
       super.onSignalStrengthsChanged(signalStrength);
       
       if(signalStrength.getGsmSignalStrength() > 30)
       {
    	   Toast.makeText(getApplicationContext(), "The GSM Arbitrary Signal Unit (ASU) is good= "
    		         + String.valueOf(signalStrength.getGsmSignalStrength()), Toast.LENGTH_SHORT).show(); 
       }
       else if(signalStrength.getGsmSignalStrength() > 20 && signalStrength.getGsmSignalStrength() < 30)
       {
    	   Toast.makeText(getApplicationContext(), "The GSM Arbitrary Signal Unit (ASU) is bad= "
    		         + String.valueOf(signalStrength.getGsmSignalStrength()), Toast.LENGTH_SHORT).show();
    	   
       }
       else if(signalStrength.getGsmSignalStrength() < 20)
       {
    	   Toast.makeText(getApplicationContext(), "The GSM Arbitrary Signal Unit (ASU) is ugly= "
    		         + String.valueOf(signalStrength.getGsmSignalStrength()), Toast.LENGTH_SHORT).show();
       }
      
    }

  };/* End of private Class */

  /**
   * Returns a string describing the network type.
   */
  public static String mapNetworkTypeToName(int networkType) {

      switch (networkType) {
          case TelephonyManager.NETWORK_TYPE_CDMA:
              return NETWORK_CDMA;
          case TelephonyManager.NETWORK_TYPE_EDGE:
              return NETWORK_EDGE;
          case TelephonyManager.NETWORK_TYPE_GPRS:
              return NETWORK_EDGE;
          case TelephonyManager.NETWORK_TYPE_UMTS:
              return NETWORK_UMTS;
          case TelephonyManager.NETWORK_TYPE_EVDO_0:
              return NETWORK_EVDO_0;
          case TelephonyManager.NETWORK_TYPE_EVDO_A:
              return NETWORK_EVDO_A;
          case TelephonyManager.NETWORK_TYPE_EVDO_B:
              return NETWORK_EVDO_B;
          case TelephonyManager.NETWORK_TYPE_1xRTT:
              return NETWORK_1X_RTT;
          case TelephonyManager.NETWORK_TYPE_HSDPA:
              return NETWORK_HSDPA;
          case TelephonyManager.NETWORK_TYPE_HSPA:
              return NETWORK_HSPA;
          case TelephonyManager.NETWORK_TYPE_HSUPA:
              return NETWORK_HSUPA;
          case TelephonyManager.NETWORK_TYPE_IDEN:
              return NETWORK_IDEN;
          case TelephonyManager.NETWORK_TYPE_LTE:
              return NETWORK_LTE;
          case TelephonyManager.NETWORK_TYPE_EHRPD:
              return NETWORK_EHRPD;
//          case TelephonyManager.NETWORK_TYPE_HSPAP:
//              return NETWORK_HSPAP;
          case TelephonyManager.NETWORK_TYPE_UNKNOWN:
          default:
              return NETWORK_UNKOWN;
      }
  }
}
