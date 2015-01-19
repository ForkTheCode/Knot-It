package utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class LoadLargePics extends AsyncTask<String, Integer, Drawable> {
	
	Context context;
	public LoadLargePics(Context context){
		this.context = context;
	}
	public onImageLoaded delegate;
	public interface onImageLoaded{
		public void setImage(Drawable d);
	}
	@Override
	protected Drawable doInBackground(String... params) {
		String image_path = params[0];
		if(image_path!=null) {
            Bitmap b = tools.decodeSampledBitmapFromSource(image_path, (int) tools.convertDpToPixel(200, context), (int) tools.convertDpToPixel(tools.getWidth(context), context));
            if (b!=null && (b.getHeight() >= 4096 || b.getWidth() >= 4096)) {
                b = Bitmap.createScaledBitmap(b, b.getWidth() / 2, b.getHeight() / 2, true);

            }
            if (b != null) {
                Drawable d = new BitmapDrawable(context.getResources(), b);
                return d;
            }
            else
                return  null;
        }
		else{
			return null;
		}
	}
	@Override
	protected void onPostExecute(Drawable result) {
		super.onPostExecute(result);
		if(delegate!=null){
			delegate.setImage(result);
		}
	}
	
	

}
