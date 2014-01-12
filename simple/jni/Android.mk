#
# Copyright (c) 2012-2013 OrangeSignal.com All Rights Reserved.
#
 
LOCAL_PATH := $(call my-dir)

#include $(call all-subdir-makefiles)

#-----------------------------------------------------------------------------
include $(CLEAR_VARS)

LOCAL_MODULE    := orangesignal-camera-simple
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_SRC_FILES := ./src/SimplePreviewJNI.cpp

include $(BUILD_SHARED_LIBRARY)
#-----------------------------------------------------------------------------
