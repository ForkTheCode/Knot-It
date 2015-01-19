package utilities;

import java.util.List;

import models.Knot;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.forkthecode.knotit.MainActivity;
import com.forkthecode.knotit.R;

public class CustomArrayAdapter extends ArrayAdapter<Knot> {
	
	Context context;

	public CustomArrayAdapter(Context context, int resource,
			 List<Knot> objects) {
		super(context, resource,  objects);
		this.context = context;
	}
    ImageView image;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View output =  convertView;

		if(output == null){
			LayoutInflater inflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 output = inflater.inflate(com.forkthecode.knotit.R.layout.knot, null);
		}

		Knot knot = getItem(position);
		TextView title = (TextView)output.findViewById(com.forkthecode.knotit.R.id.knot_title);
		TextView description = (TextView)output.findViewById(com.forkthecode.knotit
                .R.id.knot_description);
		TextView date = (TextView)output.findViewById(com.forkthecode.knotit
                .R.id.knot_reminder_time);
		image = (ImageView) output.findViewById(com.forkthecode.knotit.R.id.knot_image);
		title.setText(knot.title);
		description.setText(knot.description);
		String reminderDate  = tools.epochToTime(context, knot.reminderTimestamp);
		date.setText(reminderDate);
        CheckBox checkBox = (CheckBox) output.findViewById(R.id.checkBoxknot);
        if(knot.isRepeating==1){
            checkBox.setVisibility(View.VISIBLE);
            if(knot.repeatingTime == 3600000l) {
                checkBox.setText(context.getString(R.string.repeat_every_hour));
            }
            else if(knot.repeatingTime == 86400000l) {
                checkBox.setText(context.getString(R.string.repeat_every_day));
            }
            else if(knot.repeatingTime == 604800000l) {
                checkBox.setText(context.getString(R.string.repeat_every_week));
            }
        }else{
            checkBox.setVisibility(View.GONE);
        }
		if(knot.imageSource == null){
			image.setImageDrawable(null);
		}
		else{

               Drawable d = MainActivity.hashMap.get(knot.timestamp);

                    image.setImageDrawable(d);


		}
		return output;
	}

}
