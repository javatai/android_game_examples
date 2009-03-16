package com.stuffthathappens.games;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

/**
 * Shows a list of all Sensors on this phone.
 * 
 * @author Eric M. Burke
 */
public class Sensors extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensors);
        
		ListView sensorList = (ListView) findViewById(R.id.sensor_list);
		sensorList.setAdapter(new SensorListAdapter(this));
    }
}