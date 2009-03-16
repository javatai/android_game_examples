package com.stuffthathappens.games;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import static android.hardware.SensorManager.*;

/**
 * Detects the list of sensors supported by the device. Adapts
 * this list for display in a ListView.
 * 
 * @author Eric M. Burke
 */
public class SensorListAdapter extends BaseAdapter {
	private final LayoutInflater inflater;
	
	private final List<SensorInfo> sensors = new ArrayList<SensorInfo>();
	
	public SensorListAdapter(Context context) {
		// cache the inflater
		inflater = LayoutInflater.from(context);
		
		SensorManager sensorMgr = (SensorManager) 
				context.getSystemService(Context.SENSOR_SERVICE);
		int sensorIds = sensorMgr.getSensors();
		sensors.add(new SensorInfo("Accelerometer", 
				(sensorIds & SENSOR_ACCELEROMETER) == SENSOR_ACCELEROMETER));
		sensors.add(new SensorInfo("Light",
				(sensorIds & SENSOR_LIGHT) == SENSOR_LIGHT));
		sensors.add(new SensorInfo("Magnetic Field",
				(sensorIds & SENSOR_MAGNETIC_FIELD) == SENSOR_MAGNETIC_FIELD));
		sensors.add(new SensorInfo("Orientation",
				(sensorIds & SENSOR_ORIENTATION) == SENSOR_ORIENTATION));
		sensors.add(new SensorInfo("Orientation Raw",
				(sensorIds & SENSOR_ORIENTATION_RAW) == SENSOR_ORIENTATION_RAW));
		sensors.add(new SensorInfo("Proximity",
				(sensorIds & SENSOR_PROXIMITY) == SENSOR_PROXIMITY));
		sensors.add(new SensorInfo("Temperature",
				(sensorIds & SENSOR_TEMPERATURE) == SENSOR_TEMPERATURE));
		sensors.add(new SensorInfo("Tricorder",
				(sensorIds & SENSOR_TRICORDER) == SENSOR_TRICORDER));
	}

	public int getCount() {
		return sensors.size();
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}

	public Object getItem(int position) {
		return sensors.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {		
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_sensor, null);
			
			holder = new ViewHolder();
			holder.sensorName = (TextView) convertView.findViewById(R.id.sensor_name);
			holder.sensorSupported = (TextView) convertView.findViewById(R.id.sensor_supported);
			
			// store the holder for reuse later
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		SensorInfo si = sensors.get(position);
		holder.sensorName.setText(si.getName());
		holder.sensorSupported.setText(Boolean.toString(si.isSupported()));
		
		return convertView;
	}
	
	private static class ViewHolder {
		TextView sensorName;
		TextView sensorSupported;
	}

}
