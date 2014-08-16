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

/* These variables need to be global, so we can used them onResume and onPause method to
    stop the listener */
 TelephonyManager        Tel;
 MyPhoneStateListener    MyListener;
 String IMEI;
 String operator;
 List<NeighboringCellInfo> NeighboringList;
 int cellID;
 int lac;
 
  
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
	   	operator = Tel.getNetworkOperatorName();
	   	cellID = cellLocation.getCid();
	    lac = cellLocation.getLac();
	        		
	    /*This text box provides IMEI of the device*/
	   	TextView dID = (TextView)findViewById(R.id.deviceID);
	   	dID.setText("The IMEI is "+IMEI);
	   	
	   	/*This text box provides Operator name*/
	   	TextView opr = (TextView)findViewById(R.id.operator);
	   	opr.setText("The Operator name is "+operator);
	   	
	   	/*This text box provides LAC and Cell ID*/
	   	TextView cal = (TextView)findViewById(R.id.cal);
	   	cal.setText("The Cell ID is "+ cellID + " & LAC is "+ lac);
	   	
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
	       
	       /*This text box provides NEIGHBOURING cel sites with signal strength*/
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
	       /*PROVIDES LIST OF APPS installed - IGNORE this for now*/
		   appList.setText(listapp);
		   
	      	  	  
		   
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
       
       /*THIS LOOP PROVIDES THE CURRENT SIGNAL STRENGTH EVERY TIME THE SIGNAL CHANGES*/
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


}
