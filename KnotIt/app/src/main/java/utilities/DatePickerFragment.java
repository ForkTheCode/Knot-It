package utilities;

import java.util.Calendar;

import com.forkthecode.knotit.AddNew;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment
implements OnDateSetListener {
	
	public static int mYear;
	public static int mMonth;
	public static int mDay;
	public static TextView textView;

	@SuppressLint("ValidFragment")
    public DatePickerFragment(TextView view){
		textView = view;
	}

	
@Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
// Use the current date as the default date in the picker
final Calendar c = Calendar.getInstance();
int year = c.get(Calendar.YEAR);
int month = c.get(Calendar.MONTH);
int day = c.get(Calendar.DAY_OF_MONTH);

// Create a new instance of DatePickerDialog and return it
return new DatePickerDialog(getActivity(), this, year, month, day);
}

public void onDateSet(DatePicker view, int year, int month, int day) {
//
	mDay = day;
	mYear = year;
	mMonth = month + 1;
	textView.setText(mDay+"/"+mMonth+"/"+mYear);
	AddNew.datePicked =true;
}
}