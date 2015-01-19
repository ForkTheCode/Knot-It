package utilities;

import java.util.Calendar;

import com.forkthecode.knotit.AddNew;

import android.annotation.SuppressLint;
import android.app.Dialog;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;
@SuppressLint("ValidFragment")
public class TimePickerFragment extends DialogFragment
implements OnTimeSetListener {

	public static  int hour;
	public static  int min;
	public static TextView textView;
    @SuppressLint("ValidFragment")
	public TimePickerFragment(TextView view){
		textView = view;
	}
@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
		DateFormat.is24HourFormat(getActivity()));
	}
	
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	// Do something with the time chosen by the user
		hour = hourOfDay;
		min = minute;
		textView.setText(hourOfDay+":"+minute);
		AddNew.timePicked = true;
	}
	
}