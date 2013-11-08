#include <jni.h>

#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc_c.h>

#include <android/log.h>
#include <android/bitmap.h>

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>

using namespace std;
using namespace cv;

Mat * mCanny = NULL;
#define DEBUG_TAG "NDK_AndroidNDK1SampleActivity"
#define  LOG_TAG    "ImageProcessing"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

IplImage* img = NULL;

static int rgb_clamp(int value) {
  if(value > 255) {
    return 255;
  }
  if(value < 0) {
    return 0;
  }
  return value;
}


static void brightness(AndroidBitmapInfo* info, void* pixels, float brightnessValue){
	int xx, yy, red, green, blue;
	uint32_t* line;

	for(yy = 0; yy < info->height; yy++){
			line = (uint32_t*)pixels;
			for(xx =0; xx < info->width; xx++){

			  //extract the RGB values from the pixel
				red = (int) ((line[xx] & 0x00FF0000) >> 16);
				green = (int)((line[xx] & 0x0000FF00) >> 8);
				blue = (int) (line[xx] & 0x00000FF );

        //manipulate each value
        red = rgb_clamp((int)(red * brightnessValue));
        green = rgb_clamp((int)(green * brightnessValue));
        blue = rgb_clamp((int)(blue * brightnessValue));

        // set the new pixel back in
        line[xx] =
          ((red << 16) & 0x00FF0000) |
          ((green << 8) & 0x0000FF00) |
          (blue & 0x000000FF);
			}

			pixels = (char*)pixels + info->stride;
		}
}

void drawCircle1( IplImage* img, int x, int y)
{
	//cvRectangle( img, cvPoint(rect.x, rect.y), cvPoint(box.x+box.width,box.y+box.height), cvScalar(0xff,0x00,0x00),CV_FILLED );
	cvCircle(img, cvPoint(x, y), 10, cvScalar(0xff,0x00,0x00),CV_FILLED );
}

void drawCircle(AndroidBitmapInfo* info, unsigned char* pixels,  jint x, jint y){

	IplImage *image;

	IplImage *rgb565 = cvCreateImage(cvSize(info->width,info->height),  //size
	                                     IPL_DEPTH_8U,                                //depth
	                                     2);                                          //channels

}

extern "C"

jboolean
Java_my_project_MyRealTimeImageProcessing_TouchView_brightness(JNIEnv * env, jobject  obj, jobject bitmap, jfloat brightnessValue)
{
    AndroidBitmapInfo  info;
    int ret;
    void* pixels;
    unsigned char *rgb565Data;
    cv::Mat* mat;

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
            LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
            return false;
        }
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888 !");
        return false;
    }

    IplImage *rgb565 = cvCreateImage(cvSize(info.width,info.height),  //size
    	                                     IPL_DEPTH_8U,                                //depth
    	                                     2);

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }

    //memcpy(pixels, mat->data, info.height * info.width * 4);

    AndroidBitmap_lockPixels(env,bitmap,(void**)&rgb565Data);
    //memcpy((void*)rgb565->imageData,rgb565Data,rgb565->imageSize);


    brightness(&info,pixels, brightnessValue);

    AndroidBitmap_unlockPixels(env, bitmap);
    return true;
}

extern "C"
jboolean
Java_my_project_MyRealTimeImageProcessing_TouchView_HightLight(JNIEnv * env, jobject  obj, jobject bitmap, jint x, jint y, jbyteArray imgData, jintArray outPixels )
	{
	__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK: HightLight");

		AndroidBitmapInfo  info;
		int ret;
		void* pixels;

		if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
		            LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		            return false;
        }
		__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "1");
		__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "h-w [%i]x[%i] \t[%i], [%i]", info.height, info.width, x, y);
		__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "2");

		if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
		        LOGE("Bitmap format is not RGBA_8888 !");
		        return false;
	    }

		if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	    }

	/*	jbyte * pImageData = env->GetByteArrayElements(imgData, 0); */
 	    Mat myuv(info.height, info.width, CV_8UC4, (unsigned char *) pixels);
   	    img = new IplImage(myuv);


		/*IplImage* img = cvCreateImage(cvSize(info.width,info.height), IPL_DEPTH_8U, 4);
		img->imageData =(char *) pixels;*/

		cvCircle(img, cvPoint(x, y), 40, cvScalar(0,0,255),CV_FILLED );

		AndroidBitmap_unlockPixels(env, bitmap);
		return true;
	}

extern "C"
jboolean
Java_my_project_MyRealTimeImageProcessing_TouchView_HightLight1(JNIEnv* env, jobject object, jint height, jint width, jint x, jint y, jbyteArray imgData, jintArray outPixels )
	{
		__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK: HightLight h-w [%i]x[%i] \t[%i], [%i]", height, width, x, y);
		jbyte * pImageData = env->GetByteArrayElements(imgData, 0);
		jint * poutPixels = env->GetIntArrayElements(outPixels, 0);

		if (img == NULL) {
    	    Mat myuv(height, width, CV_8UC1, (unsigned char *) pImageData);
    	    img = new IplImage(myuv);
		}

		cvCircle(img, cvPoint(x, y), 10, cvScalar(0xff,0x00,0x00),CV_FILLED );

		Mat mResult(height, width, CV_8UC4, (unsigned char *)poutPixels);
		IplImage ResultImg = mResult;
		mResult = img;

		env->ReleaseByteArrayElements(imgData, pImageData, 0);
		env->ReleaseIntArrayElements(outPixels, poutPixels, 0);
		return true;
	}

jboolean
Java_my_project_MyRealTimeImageProcessing_CameraPreview_ImageProcessing(
		JNIEnv* env, jobject thiz,
		jint width, jint height,
		jbyteArray NV21FrameData, jintArray outPixels)
{
	__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:Ray Vo");
	jbyte * pNV21FrameData = env->GetByteArrayElements(NV21FrameData, 0);
	jint * poutPixels = env->GetIntArrayElements(outPixels, 0);

	if ( mCanny == NULL )
	{
		mCanny = new Mat(height, width, CV_8UC1);
	}

	Mat mGray(height, width, CV_8UC1, (unsigned char *)pNV21FrameData);
	Mat mResult(height, width, CV_8UC4, (unsigned char *)poutPixels);
	IplImage srcImg = mGray;
	IplImage CannyImg = *mCanny;
	IplImage ResultImg = mResult;

	cvCanny(&srcImg, &CannyImg, 80, 100, 3);
	cvCvtColor(&CannyImg, &ResultImg, CV_GRAY2BGRA);

	env->ReleaseByteArrayElements(NV21FrameData, pNV21FrameData, 0);
	env->ReleaseIntArrayElements(outPixels, poutPixels, 0);
	return true;
}







