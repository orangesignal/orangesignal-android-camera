/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */


#include "GLES20Shader.h"

#include "GLES20Utils.h"

namespace orangesignal {

const std::string GLES20Shader::ATTRIB_POSITION("aPosition");
const std::string GLES20Shader::ATTRIB_TEXTURE_COORDINATE("aTextureCoord");
const std::string GLES20Shader::UNIFORM_SAMPLER("sTexture");

const char GLES20Shader::DEFAULT_VERTEX_SHADER[] =
		"attribute vec4 aPosition;\n"
		"attribute vec4 aTextureCoord;\n"
		"varying highp vec2 vTextureCoord;\n"
		"void main() {\n"
		"    gl_Position = aPosition;\n"
		"    vTextureCoord = aTextureCoord.xy;\n"
		"}\n";

const char GLES20Shader::DEFAULT_FRAGMENT_SHADER[] =
		"precision mediump float;\n"
		"varying highp vec2 vTextureCoord;\n"
		"uniform lowp sampler2D sTexture;\n"
		"void main() {\n"
		"    gl_FragColor = texture2D(sTexture, vTextureCoord);\n"
		"}\n";

const float GLES20Shader::VERTICES_DATA[] = {
		// X, Y, Z, U, V
		-1.0f,  1.0f, 0.0f, 0.0f, 1.0f,	// 左上
		 1.0f,  1.0f, 0.0f, 1.0f, 1.0f,	// 右上
		-1.0f, -1.0f, 0.0f, 0.0f, 0.0f,	// 左下
		 1.0f, -1.0f, 0.0f, 1.0f, 0.0f	// 右下
	};
const GLint GLES20Shader::VERTICES_DATA_POS_SIZE = 3;
const GLint GLES20Shader::VERTICES_DATA_UV_SIZE = 2;
const GLsizei GLES20Shader::VERTICES_DATA_STRIDE_BYTES = (VERTICES_DATA_POS_SIZE + VERTICES_DATA_UV_SIZE) * sizeof(float);
const GLint GLES20Shader::VERTICES_DATA_POS_OFFSET = 0;
const GLint GLES20Shader::VERTICES_DATA_UV_OFFSET = VERTICES_DATA_POS_SIZE * sizeof(float);

GLES20Shader::GLES20Shader() {
	mVertexShaderSource = DEFAULT_VERTEX_SHADER;
	mFragmentShaderSource = DEFAULT_FRAGMENT_SHADER;
	mProgram = 0;
	mVertexShader = 0;
	mFragmentShader = 0;
	mVertexBufferName = 0;
}

GLES20Shader::GLES20Shader(const char* vertexShaderSource, const char* fragmentShaderSource) {
	mVertexShaderSource = vertexShaderSource;
	mFragmentShaderSource = fragmentShaderSource;
	mProgram = 0;
	mVertexShader = 0;
	mFragmentShader = 0;
	mVertexBufferName = 0;
}

GLES20Shader::~GLES20Shader() {
}

void GLES20Shader::setup() {
	release();
	mVertexShader     = GLES20Utils::loadShader(GL_VERTEX_SHADER,   mVertexShaderSource);
	mFragmentShader   = GLES20Utils::loadShader(GL_FRAGMENT_SHADER, mFragmentShaderSource);
	mProgram          = GLES20Utils::createProgram(mVertexShader, mFragmentShader);
	mVertexBufferName = GLES20Utils::createBuffer(VERTICES_DATA);
}

void GLES20Shader::setFrameSize(const GLsizei width, const GLsizei height) {
}

void GLES20Shader::release() {
	glDeleteProgram(mProgram);
	mProgram = 0;
	glDeleteShader(mVertexShader);
	mVertexShader = 0;
	glDeleteShader(mFragmentShader);
	mFragmentShader = 0;
	glDeleteBuffers(1, &mVertexBufferName);
	mVertexBufferName = 0;

	mHandleMap.clear();
}

void GLES20Shader::draw(const GLuint texName, GLES20FramebufferObject fbo) {
	useProgram();

	glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferName);
	glEnableVertexAttribArray(getHandle(ATTRIB_POSITION));
	glVertexAttribPointer(getHandle(ATTRIB_POSITION), VERTICES_DATA_POS_SIZE, GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_POS_OFFSET);
	glEnableVertexAttribArray(getHandle(ATTRIB_TEXTURE_COORDINATE));
	glVertexAttribPointer(getHandle(ATTRIB_TEXTURE_COORDINATE), VERTICES_DATA_UV_SIZE, GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, (GLvoid *) VERTICES_DATA_UV_OFFSET);

	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, texName);
	glUniform1i(getHandle(UNIFORM_SAMPLER), 0);

	onDraw();

	glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

	glDisableVertexAttribArray(getHandle(ATTRIB_POSITION));
	glDisableVertexAttribArray(getHandle(ATTRIB_TEXTURE_COORDINATE));
	glBindTexture(GL_TEXTURE_2D, 0);
	glBindBuffer(GL_ARRAY_BUFFER, 0);
}

void GLES20Shader::onDraw() {
}

void GLES20Shader::useProgram() {
	glUseProgram(mProgram);
}

GLuint GLES20Shader::getVertexBufferName() {
	return mVertexBufferName;
}

GLuint GLES20Shader::getHandle(const std::string name) {
	if (mHandleMap.count(name) > 0) {
		return mHandleMap[name];
	}

	const char* _name = name.c_str();
	GLuint location = glGetAttribLocation(mProgram, _name);
	if (location == -1) {
		location = glGetUniformLocation(mProgram, _name);
	}
//	if (location == -1) {
//		throw new IllegalStateException("Could not get attrib or uniform location for " + name);
//	}
	if (location != -1) {
		mHandleMap[name] = location;
	}

	return location;
}

} /* namespace orangesignal */
