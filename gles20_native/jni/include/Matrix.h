/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#ifndef ORANGESIGNAL_MATRIX_H_
#define ORANGESIGNAL_MATRIX_H_

namespace orangesignal {

/**
 * Matrix math utilities.
 *
 * @author 杉澤 浩二
 */
class Matrix {
private:

	/**
	 * インスタンス化できない事を強制します。
	 */
	Matrix();

public:

	/**
	 * Multiply two 4x4 matrices together and store the result in a third 4x4 matrix.
	 * In matrix notation: result = lhs x rhs. Due to the way matrix multiplication works, the result matrix will have the same effect as first multiplying by the rhs matrix, then multiplying by the lhs matrix.
	 * This is the opposite of what you might expect.
	 * The same float array may be passed for result, lhs, and/or rhs.
	 * However, the result element values are undefined if the result elements overlap either the lhs or rhs elements.
	 *
	 * @param result The float array that holds the result.
	 * @param lhs The float array that holds the left-hand-side matrix.
	 * @param rhs The float array that holds the right-hand-side matrix.
	 */
	static void multiplyMM(float result[16], const float lhs[16], const float rhs[16]);

};

} // namespace orangesignal
#endif // ORANGESIGNAL_MATRIX_H_
