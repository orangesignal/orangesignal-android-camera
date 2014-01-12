/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#ifndef ORANGESIGNAL_YUVDATAUTILS_H_
#define ORANGESIGNAL_YUVDATAUTILS_H_

namespace orangesignal {

/**
 * YUV データを RGB や RGBA へ変換するためのユーティリティを提供します。
 *
 * @author 杉澤 浩二
 */
class YuvDataUtils {
private:

	/**
	 * インスタンス化できない事を強制します。
	 */
	YuvDataUtils();

public:

	static void toRGB(const unsigned char* yuv420sp, const int width, const int height, unsigned char* rgb);
	static void toRGB(const unsigned char* y, const unsigned char* uv, const int width, const int height, unsigned char* rgb);
	static void toRGBA(const unsigned char* yuv420sp, const int width, const int height, unsigned char* rgba);
	static void toARGB(const unsigned char* yuv420sp, const int width, const int height, unsigned char* arbg);

};

} // namespace orangesignal
#endif // ORANGESIGNAL_YUVDATAUTILS_H_
