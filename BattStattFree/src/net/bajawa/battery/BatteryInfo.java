package net.bajawa.battery;

import android.content.Context;
import android.content.res.Resources;

public class BatteryInfo {
	
	private int batteryLevel;
	private Context context;
	private Resources res;
	private String[] tens;
	private String[] numbers;
	
	private boolean full;
	private boolean empty;
	private String ten;
	private String number;
	private String string1;
	private String string2;
	
	public BatteryInfo(Context context, int batteryLevel) {
		this.context = context;
		this.res = context.getResources();
		tens = res.getStringArray(R.array.tens);
		numbers = res.getStringArray(R.array.numbers);
		
		setBatteryLevel(batteryLevel);
	}

	public int getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(int batteryLevel) {
		this.batteryLevel = batteryLevel;
		
		// Fully charged
		if (batteryLevel == 100) {
			full = true;
			string1 = context.getString(R.string.battery_full_1);
			string2 = context.getString(R.string.battery_full_2);
			
		// Empty
		} else if (batteryLevel == 0) {
			empty = true;
			string1 = context.getString(R.string.empty);
			
		} else {
			// If number is under 20 just pick the number from the numbers array
			if (batteryLevel < 20) {
				number = numbers[batteryLevel];
			
			// Number is above 20, we need to do a magic trick on it
			} else {
				// Get the ten part
				ten = tens[batteryLevel / 10]; 
				
				// Get number part
				if (batteryLevel % 10 != 0) {
					number = numbers[batteryLevel % 10];
				}
			}
		}
	}

	public boolean isFull() {
		return full;
	}

	public boolean isEmpty() {
		return empty;
	}

	public String getTen() {
		return ten;
	}

	public String getNumber() {
		return number;
	}

	public String getString1() {
		return string1;
	}

	public String getString2() {
		return string2;
	}
	
	public boolean isNumber() {
		return batteryLevel < 20;
	}
	
	public boolean isEvenTen() {
		return batteryLevel % 10 == 0;
	}
}