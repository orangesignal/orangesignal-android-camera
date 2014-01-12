/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#include "GLES20Utils.h"

#include <stdlib.h>
#include <android/log.h>

namespace orangesignal {

#define LOG_TAG "GLES20Utils"
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,  LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

GLES20Utils::GLES20Utils() {}

GLuint GLES20Utils::createProgram(const char* vertexSource, const char* fragmentSource) {
	// バーテックスシェーダをコンパイルします。
	const GLuint vertexShader = loadShader(GL_VERTEX_SHADER, vertexSource);
	if (!vertexShader) {
		LOGE("vertex missed!");
		return 0;
	}

	// フラグメントシェーダをコンパイルします。
	const GLuint pixelShader = loadShader(GL_FRAGMENT_SHADER, fragmentSource);
	if (!pixelShader) {
		LOGE("fragment missed!");
		return 0;
	}

	return createProgram(vertexShader, pixelShader);
}

GLuint GLES20Utils::createProgram(const GLuint vertexShader, const GLuint pixelShader) {
	// プログラムを生成して、プログラムへバーテックスシェーダとフラグメントシェーダを関連付けます。
	GLuint program = glCreateProgram();
	if (program) {
		// プログラムへバーテックスシェーダを関連付けます。
		glAttachShader(program, vertexShader);
		// プログラムへフラグメントシェーダを関連付けます。
		glAttachShader(program, pixelShader);

		glLinkProgram(program);
		GLint linkStatus = GL_FALSE;
		glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);
		if (linkStatus != GL_TRUE) {
			GLint bufLength = 0;
			glGetProgramiv(program, GL_INFO_LOG_LENGTH, &bufLength);
			if (bufLength) {
				char* buf = (char*) malloc(bufLength);
				if (buf) {
					glGetProgramInfoLog(program, bufLength, NULL, buf);
					LOGE("Could not link program:\n%s\n", buf);
					free(buf);
				}
			}
			LOGE("program missed!");
			glDeleteProgram(program);
			program = 0;
		}
	}
	return program;
}

GLuint GLES20Utils::loadShader(const GLenum shaderType, const char* source) {
	const GLuint shader = glCreateShader(shaderType);
	if (shader != 0) {
		glShaderSource(shader, 1, &source, NULL);
		glCompileShader(shader);
		GLint compiled = 0;
		glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
		if (!compiled) {
			GLint infoLen = 0;
			glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
			if (infoLen) {
				char* buf = (char*) malloc(infoLen);
				if (buf) {
					glGetShaderInfoLog(shader, infoLen, NULL, buf);
					LOGE("Could not compile shader %d:\n%s\n", shaderType, buf);
					free(buf);
				}
			}
			glDeleteShader(shader);
		}
	}
	return shader;
}

void GLES20Utils::checkGlError(const char* op) {
	for (GLint error = glGetError(); error; error = glGetError()) {
//		jclass clazz = env->FindClass("java/lang/IllegalArgumentException");
//		env->ThrowNew(clazz, "Exception: Hello world.");
		LOGE("after %s() glError (0x%x)\n", op, error);
	}
}

void GLES20Utils::setupSampler(const GLenum target, const GLfloat mag, const GLfloat min) {
	// テクスチャを拡大/縮小する方法を設定します。
	glTexParameterf(target, GL_TEXTURE_MAG_FILTER, mag);		// 拡大するときピクセルの中心付近の線形で補完
	glTexParameterf(target, GL_TEXTURE_MIN_FILTER, min);		// 縮小するときピクセルの中心に最も近いテクスチャ要素で補完
	// テクスチャの繰り返し方法を設定します。
	glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
}

GLuint GLES20Utils::createBuffer(const GLfloat* data) {
	GLuint bufferName;
	glGenBuffers(1, &bufferName);
	updateBufferData(bufferName, data);
	return bufferName;
}

void GLES20Utils::updateBufferData(const GLuint bufferName, const GLfloat* data) {
	glBindBuffer(GL_ARRAY_BUFFER, bufferName);
	glBufferData(GL_ARRAY_BUFFER, sizeof(data), data, GL_STATIC_DRAW);
	glBindBuffer(GL_ARRAY_BUFFER, 0);
}

} // namespace orangesignal
