#
# Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
#

LOCAL_PATH := $(call my-dir)

#include $(call all-subdir-makefiles)

#-----------------------------------------------------------------------------
include $(CLEAR_VARS)

LOCAL_MODULE    := orangesignal-gles20
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_SRC_FILES := ./src/GLES20CompatJNI.cpp ./src/GLES20PreviewTextureJNI.cpp ./src/GLES20UtilsJNI.cpp ./src/YuvDataUtils.cpp

#LOCAL_CPP_FEATURES += exceptions
#LOCAL_CPP_FEATURES += rtti

# see ${ANDROID_NDK_HOME}/docs/STABLE-APIS.html
LOCAL_LDLIBS += -llog			# ログ機能を使用するので Android-specific Log Support を追加します。
#LOCAL_LDLIBS += -ldl
LOCAL_LDLIBS += -lGLESv2		# OpenGL ES 2.0 を使用するので OpenGL ES 2.0 ライブラリを追加します。
#LOCAL_LDLIBS += -ljnigraphics	# Java ビットマップオブジェクトへアクセスするので jnigraphics ライブラリを追加します。(ソースコード上で使用するには <android/bitmap.h> ヘッダファイルをインクルードする必要があります)

include $(BUILD_SHARED_LIBRARY)

#-----------------------------------------------------------------------------
