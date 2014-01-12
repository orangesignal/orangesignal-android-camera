/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

#include "YuvDataUtils.h"

namespace orangesignal {

#ifndef max
#define max(a,b) ({typeof(a) _a = (a); typeof(b) _b = (b); _a > _b ? _a : _b; })
#define min(a,b) ({typeof(a) _a = (a); typeof(b) _b = (b); _a < _b ? _a : _b; })
#endif

YuvDataUtils::YuvDataUtils() {}

void YuvDataUtils::toRGB(const unsigned char* yuv420sp, const int width, const int height, unsigned char* rgb) {
	toRGB(yuv420sp, yuv420sp + width * height, width, height, rgb);
}

static const int bytes_per_pixel = 2;

void YuvDataUtils::toRGB(const unsigned char* y, const unsigned char* uv, const int width, const int height, unsigned char* rgb) {
	int i, j;
	int nR, nG, nB;
	int nY, nU, nV;
	int offset = 0;
	// YUV 4:2:0
	for (i = 0; i < height; i++) {
		for (j = 0; j < width; j++) {
			nY = *(y + i * width + j);
			nV = *(uv + (i / 2) * width + bytes_per_pixel * (j / 2));
			nU = *(uv + (i / 2) * width + bytes_per_pixel * (j / 2) + 1);

			// Yuv Convert
			nY -= 16;
			nU -= 128;
			nV -= 128;

			if (nY < 0) {
				nY = 0;
			}

			// nR = (int)(1.164 * nY + 2.018 * nU);
			// nG = (int)(1.164 * nY - 0.813 * nV - 0.391 * nU);
			// nB = (int)(1.164 * nY + 1.596 * nV);

			nB = (int) (1192 * nY + 2066 * nU);
			nG = (int) (1192 * nY -  833 * nV - 400 * nU);
			nR = (int) (1192 * nY + 1634 * nV);

			nR = min(262143, max(0, nR));
			nG = min(262143, max(0, nG));
			nB = min(262143, max(0, nB));

			nR >>= 10; nR &= 0xff;
			nG >>= 10; nG &= 0xff;
			nB >>= 10; nB &= 0xff;
			rgb[offset++] = (unsigned char) nR;
			rgb[offset++] = (unsigned char) nG;
			rgb[offset++] = (unsigned char) nB;
		}
	}
}

void YuvDataUtils::toRGBA(const unsigned char* yuv420sp, const int width, const int height, unsigned char* rgba) {
	const unsigned char* y = yuv420sp;
	const unsigned char* uv = yuv420sp + width * height;
	int i, j;
	int nR, nG, nB;
	int nY, nU, nV;
	int offset = 0;
	// YUV 4:2:0
	for (i = 0; i < height; i++) {
		for (j = 0; j < width; j++) {
			nY = *(y + i * width + j);
			nV = *(uv + (i / 2) * width + bytes_per_pixel * (j / 2));
			nU = *(uv + (i / 2) * width + bytes_per_pixel * (j / 2) + 1);

			// Yuv Convert
			nY -= 16;
			nU -= 128;
			nV -= 128;

			if (nY < 0) {
				nY = 0;
			}

			// nR = (int)(1.164 * nY + 2.018 * nU);
			// nG = (int)(1.164 * nY - 0.813 * nV - 0.391 * nU);
			// nB = (int)(1.164 * nY + 1.596 * nV);

			nB = (int) (1192 * nY + 2066 * nU);
			nG = (int) (1192 * nY -  833 * nV - 400 * nU);
			nR = (int) (1192 * nY + 1634 * nV);

			nR = min(262143, max(0, nR));
			nG = min(262143, max(0, nG));
			nB = min(262143, max(0, nB));

			nR >>= 10; nR &= 0xff;
			nG >>= 10; nG &= 0xff;
			nB >>= 10; nB &= 0xff;
			rgba[offset++] = (unsigned char) nR;
			rgba[offset++] = (unsigned char) nG;
			rgba[offset++] = (unsigned char) nB;
			rgba[offset++] = (unsigned char) 255;
		}
	}
}

void YuvDataUtils::toARGB(const unsigned char* yuv420sp, const int width, const int height, unsigned char* arbg) {
	const unsigned char* y = yuv420sp;
	const unsigned char* uv = yuv420sp + width * height;
	int i, j;
	int nR, nG, nB;
	int nY, nU, nV;
	int offset = 0;
	// YUV 4:2:0
	for (i = 0; i < height; i++) {
		for (j = 0; j < width; j++) {
			nY = *(y + i * width + j);
			nV = *(uv + (i / 2) * width + bytes_per_pixel * (j / 2));
			nU = *(uv + (i / 2) * width + bytes_per_pixel * (j / 2) + 1);

			// Yuv Convert
			nY -= 16;
			nU -= 128;
			nV -= 128;

			if (nY < 0) {
				nY = 0;
			}

			// nR = (int)(1.164 * nY + 2.018 * nU);
			// nG = (int)(1.164 * nY - 0.813 * nV - 0.391 * nU);
			// nB = (int)(1.164 * nY + 1.596 * nV);

			nB = (int) (1192 * nY + 2066 * nU);
			nG = (int) (1192 * nY -  833 * nV - 400 * nU);
			nR = (int) (1192 * nY + 1634 * nV);

			nR = min(262143, max(0, nR));
			nG = min(262143, max(0, nG));
			nB = min(262143, max(0, nB));

			nR >>= 10; nR &= 0xff;
			nG >>= 10; nG &= 0xff;
			nB >>= 10; nB &= 0xff;
			arbg[offset++] = (unsigned char) 255;
			arbg[offset++] = (unsigned char) nR;
			arbg[offset++] = (unsigned char) nG;
			arbg[offset++] = (unsigned char) nB;
		}
	}
}

} // namespace orangesignal
