LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_LIB_TYPE:=STATIC
OPENCV_INSTALL_MODULES:=on

include /home/vqd/workspace/sources/OpenCV-2.4.6-android-sdk/sdk/native/jni/OpenCV.mk
ifeq ("$(wildcard $(OPENCV_MK_PATH))","")
	#try to load OpenCV.mk from default install location
	include /home/vqd/workspace/sources/OpenCV-2.4.6-android-sdk/sdk/native/jni/OpenCV.mk
else
	include $(OPENCV_MK_PATH)
endif

LOCAL_MODULE    := ImageProcessing
LOCAL_SRC_FILES := ImageProcessing.cpp
#LOCAL_LDLIBS +=  -llog -ldl
LOCAL_LDLIBS +=  -llog -ldl -ljnigraphics

include $(BUILD_SHARED_LIBRARY)
