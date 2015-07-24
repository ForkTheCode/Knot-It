package utilities;

import java.util.ArrayList;

import models.Knot;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;

import com.forkthecode.knotit.MainActivity;

public class PopulateListTask extends AsyncTask<String, Integer, ArrayList<Knot>> {
	Context context;
	Fragment mFragment;
	public PopulateListTask(Context context,Fragment fragment){
		this.context = context;
		mFragment = fragment;
	}
	public interface onListPopulated{
		 void setAdapter(ArrayList<Knot> list);
	}
	
	public onListPopulated delegate;
	@Override
	protected ArrayList<Knot> doInBackground(String... params) {
		String tableName = params[0];
		ArrayList<Knot> newList = tools.getKnotsArrayList(context, tableName);

        int width = (int)(tools.getWidth(context));
        int height = (int)(tools.convertDpToPixel(200,context));
		 for(int i = 0;i<newList.size();i++){
	    	  Knot knot = newList.get(i);
             if(knot.imageSource!=null) {
                 Bitmap bitmap = tools.decodeSampledBitmapFromSource(knot.imageSource,width ,height );
                 if (bitmap != null) {
                     Drawable d = new BitmapDrawable(context.getResources(), bitmap);
                     MainActivity.hashMap.put(knot.timestamp, d);
                 }
             }
	       }
		 return newList;
	}
	@Override
	protected void onPostExecute(ArrayList<Knot> result) {
		super.onPostExecute(result);
		if(mFragment.isAdded()){
		if(delegate!=null){
			delegate.setAdapter(result);
		}
		}
	}
}