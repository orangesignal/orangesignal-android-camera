/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#ifndef ORANGESIGNAL_IMAGEVERTICES_H_
#define ORANGESIGNAL_IMAGEVERTICES_H_

#include <GLES2/gl2.h>

/**
 * デフォルトの頂点データです。(2つの連続した三角形で長方形を構成)
 *
 * (-1,1)           (1,1)
 *           │
 *           │
 *           │
 *           │
 *           │(0,0)
 * ─────┼─────
 *           │
 *           │
 *           │
 *           │
 *           │
 * (-1,-1)         (1,-1)
 */
static const GLfloat VERTEX_XYZ_DATA[12] = {
	-1.0f,  1.0f, 0.0f,	// 左上
	 1.0f,  1.0f, 0.0f,	// 右上
	-1.0f, -1.0f, 0.0f,	// 左下
	 1.0f, -1.0f, 0.0f	// 右下
};

/**
 * デフォルトの UV マッピングデータです。
 */
static const GLfloat TEXTURE_COORDINATE[8] = {
		0.0f, 1.0f,
		1.0f, 1.0f,
		0.0f, 0.0f,
		1.0f, 0.0f
	};
static const GLfloat TEXTURE_COORDINATE_ROTATION_90[8] = {
		1.0f, 1.0f,
		1.0f, 0.0f,
		0.0f, 1.0f,
		0.0f, 0.0f
	};
static const GLfloat TEXTURE_COORDINATE_ROTATION_180[8] = {
		1.0f, 0.0f,
		0.0f, 0.0f,
		1.0f, 1.0f,
		0.0f, 1.0f
	};
static const GLfloat TEXTURE_COORDINATE_ROTATION_270[8] = {
		0.0f, 0.0f,
		0.0f, 1.0f,
		1.0f, 0.0f,
		1.0f, 1.0f
	};
static const GLfloat TEXTURE_COORDINATE_FLIP_VERTICAL[8] = {
		0.0f, 0.0f,
		1.0f, 0.0f,
		0.0f, 1.0f,
		1.0f, 1.0f
	};
static const GLfloat TEXTURE_COORDINATE_FLIP_HORIZONTAL[8] = {
		1.0f, 1.0f,
		0.0f, 1.0f,
		1.0f, 0.0f,
		0.0f, 0.0f
	};

#endif // ORANGESIGNAL_IMAGEVERTICES_H_
