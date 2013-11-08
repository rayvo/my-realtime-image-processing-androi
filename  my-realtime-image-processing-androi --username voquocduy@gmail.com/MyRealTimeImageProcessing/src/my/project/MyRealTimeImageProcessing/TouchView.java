package my.project.MyRealTimeImageProcessing;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class TouchView extends ImageView {

	private String fileName = null;
	private int scale = 1;
	
	private int[] pixels = null;
	private Bitmap orgBitmap = null;
	private byte[] inputData = null;
	private boolean bProcessing = false;
	
	private int height;
	private int width;
	private int x = 0;
	private int y = 0;
	

	Handler mHandler = new Handler(Looper.getMainLooper());
	
	private Bitmap img = null;
	private HashMap<Integer, Path> fingerMap = new HashMap<Integer, Path>();
	private Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public TouchView(Context context) {
		super(context);
		init();
	}

	public TouchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		if (fileName != null) {
			img = BitmapFactory.decodeFile(fileName);
			
		}
		myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		myPaint.setColor(Color.CYAN);
	}

	private boolean flag = true;
	private Bitmap curBitmap = null;
	

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (flag) { // initialize
			if (fileName != null) {
				
				width = this.getWidth();
				height = this.getHeight();
				pixels = new int[width * height];				
				
				orgBitmap = ((BitmapDrawable) this.getDrawable()).getBitmap();
				
				curBitmap = orgBitmap.copy(Bitmap.Config.ARGB_8888, true);
				
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				curBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				inputData = stream.toByteArray();
				
				/*bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
				
				opencv.setSourceImage(pixels, width, height);
				opencv.extractSURFFeature();
				byte[] imageData = opencv.getSourceImage()
			
				bitmap.getPixels(inputData, 0, width, 0, 0, width, height);
				
				
				opencv.setSourceImage(pixels, width, height);
				opencv.extractSURFFeature();
				byte[] imageData = opencv.getSourceImage();
				bitmap = BitmapFactory.decodeByteArray(imageData, 0,
						imageData.length);*/
				
			
				flag=false;		
			}
		}
		//canvas.drawBitmap(proc_bitmap, 10, 10, null);
		
		//pixels = new int[width * height];
		
		
		//canvas.drawBitmap(curBitmap, 10, 10, null);
	
		
		if ( bProcessing ){
			//TODO inputData = 
			
			
			mHandler.post(DoImageProcessing);
		}
		//cvCircle(img, cvPoint(rect.x, rect.y), 10, cvScalar(0xff,0x00,0x00),CV_FILLED );
	}

	
	/*
	 * @Override public void setImageBitmap(android.graphics.Bitmap bm) {
	 * super.setImageBitmap(bm); };
	 */

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		// Log.d("CV", "Action ["+action+"]");
		switch (action) {
		case MotionEvent.ACTION_DOWN: {

			int id = event.getPointerId(0);
			Log.d("CV", "Pointer Down [" + event.getX() + "," + event.getY() +"]");
				
			fingerMap.put(id, createCirPath(event.getX(), event.getY(), id));
			x = (int) event.getX(); 
			y = (int) event.getY();
			bProcessing = true;
			break;
		}
		case MotionEvent.ACTION_MOVE: {

			int touchCounter = event.getPointerCount();
			for (int t = 0; t < touchCounter; t++) {
				int id = event.getPointerId(t);
				//fingerMap.remove(id);
				fingerMap.put(id,
						createCirPath(event.getX(t), event.getY(t), id));
				x = (int) event.getX(); 
				y = (int) event.getY();
				bProcessing = true;
			}
		}
		case MotionEvent.ACTION_POINTER_DOWN: {
			int id = event.getPointerId(getIndex(event));
			//Log.d("CV", "Other point down [" + id + "]");
			Log.d("CV", "Other point down [" + event.getX() + "," + event.getY() +"]");
			fingerMap.put(
					id,
					createCirPath(event.getX(getIndex(event)),
							event.getY(getIndex(event)), getIndex(event)));
			x = (int) event.getX(); 
			y = (int) event.getY();
			bProcessing = true;
			break;
		}
		case MotionEvent.ACTION_POINTER_UP: {
			int id = event.getPointerId(getIndex(event));
			//Log.d("CV", "Other point up [" + id + "]");
			Log.d("CV", "Other point up [" + event.getX() + "," + event.getY() +"]");
			//fingerMap.remove(id);
			break;
		}
		case MotionEvent.ACTION_UP: {
			int id = event.getPointerId(0);
			//Log.d("CV", "Pointer up [" + id + "]");
			Log.d("CV", "Pointer up [" + event.getX() + "," + event.getY() +"]");
//			//fingerMap.remove(id);
			break;
		}
		}

		invalidate();
		return true;

	}

	private static float i = 3;
	private Path createCirPath(float x, float y, int id) {
		Path p = new Path();
		
		//brightness(curBitmap, i);
	    //this.setImageBitmap(curBitmap);
		p.addCircle(x, y, 30, Path.Direction.CW);
		i++;
		return p;
	}

	private int getIndex(MotionEvent event) {
		int idx = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		return idx;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}
	
	//
	// Native JNI 
	//
	private native boolean HightLight(Bitmap bitmap, int x, int y, byte[] imgData, int[] outPixels);
	public native boolean brightness(Bitmap bmp, float brightness);
	static 
    {
        System.loadLibrary("ImageProcessing");
    }
       
    private Runnable DoImageProcessing = new Runnable() 
    {
        public void run() 
        {
        	Log.i("MyRealTimeImageProcessing", "DoImageProcessing():");
        	bProcessing = true;
        	
			HightLight(curBitmap, x, y, inputData, pixels);			
			/*bitmap.setPixels(pixels, 0, width, 0, 0, width, height);*/
			
			TouchView.this.setImageBitmap(curBitmap);	
        	
			
			bProcessing = false;
        }
    };   

}
