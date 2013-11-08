package my.project.MyRealTimeImageProcessing;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MyRealTimeImageProcessing extends Activity 
{
	private static final String TAG = MyRealTimeImageProcessing.class.getSimpleName();
	private static final int CAMERA_REQUEST = 1888;
	private TouchView imageView;
	private File file = null;
	private FrameLayout mainLayout;
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {		
		Log.i(TAG, "Called onResume");
	    super.onResume();
	}

	public void onDestroy() {
		super.onDestroy();
		finish();
	}

	public void onCameraViewStarted(int width, int height) {
		
	}

	public void onCameraViewStopped() {
		
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		//Set this APK Full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
				 			WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Set this APK no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.main);
        
        //
        // Create my camera preview 
        //
        
        mainLayout = (FrameLayout) findViewById(R.id.frameLayout1);
        imageView = new TouchView(this); 
        
		//this.imageView = (TouchView)this.findViewById(R.id.imageView1);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String fileName = Environment.getExternalStorageDirectory().getPath() +
                               "/wai_" + currentDateandTime + ".jpg";
        file = new File(fileName);
        try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Uri outputFileUri = Uri.fromFile(file);
        
        
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		
        startActivityForResult(cameraIntent, CAMERA_REQUEST); 
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {  
    		//Bitmap img = BitmapFactory.decodeFile(file.getAbsolutePath());
        	DisplayMetrics metrics = new DisplayMetrics();
        	getWindowManager().getDefaultDisplay().getMetrics(metrics);

        	
        	
            int targetW = metrics.widthPixels;;
            int targetH = metrics.heightPixels;
            
        	BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            
            BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            int photoW = bmOptions.outWidth;
        	int photoH = bmOptions.outHeight;
        	
        	int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        	
        	imageView.setScale(scaleFactor);
        	
        	bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);

       	
        	imageView.setFileName(file.getAbsolutePath());        	 
    		imageView.setImageBitmap(bitmap);
    		
    		mainLayout.addView(imageView, new LayoutParams(targetW, targetH));
        }  
    }
	
}