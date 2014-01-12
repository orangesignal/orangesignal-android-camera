/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#include "Matrix.h"

namespace orangesignal {

Matrix::Matrix() {}

void Matrix::multiplyMM(float result[16], const float lhs[16], const float rhs[16]) {
	result[0]  = lhs[0] * rhs[0]  + lhs[4] * rhs[1]  + lhs[8]  * rhs[2]  + lhs[12] * rhs[3];
	result[1]  = lhs[1] * rhs[0]  + lhs[5] * rhs[1]  + lhs[9]  * rhs[2]  + lhs[13] * rhs[3];
	result[2]  = lhs[2] * rhs[0]  + lhs[6] * rhs[1]  + lhs[10] * rhs[2]  + lhs[14] * rhs[3];
	result[3]  = lhs[3] * rhs[0]  + lhs[7] * rhs[1]  + lhs[11] * rhs[2]  + lhs[15] * rhs[3];

	result[4]  = lhs[0] * rhs[4]  + lhs[4] * rhs[5]  + lhs[8]  * rhs[6]  + lhs[12] * rhs[7];
	result[5]  = lhs[1] * rhs[4]  + lhs[5] * rhs[5]  + lhs[9]  * rhs[6]  + lhs[13] * rhs[7];
	result[6]  = lhs[2] * rhs[4]  + lhs[6] * rhs[5]  + lhs[10] * rhs[6]  + lhs[14] * rhs[7];
	result[7]  = lhs[3] * rhs[4]  + lhs[7] * rhs[5]  + lhs[11] * rhs[6]  + lhs[15] * rhs[7];

	result[8]  = lhs[0] * rhs[8]  + lhs[4] * rhs[9]  + lhs[8]  * rhs[10] + lhs[12] * rhs[11];
	result[9]  = lhs[1] * rhs[8]  + lhs[5] * rhs[9]  + lhs[9]  * rhs[10] + lhs[13] * rhs[11];
	result[10] = lhs[2] * rhs[8]  + lhs[6] * rhs[9]  + lhs[10] * rhs[10] + lhs[14] * rhs[11];
	result[11] = lhs[3] * rhs[8]  + lhs[7] * rhs[9]  + lhs[11] * rhs[10] + lhs[15] * rhs[11];

	result[12] = lhs[0] * rhs[12] + lhs[4] * rhs[13] + lhs[8]  * rhs[14] + lhs[12] * rhs[15];
	result[13] = lhs[1] * rhs[12] + lhs[5] * rhs[13] + lhs[9]  * rhs[14] + lhs[13] * rhs[15];
	result[14] = lhs[2] * rhs[12] + lhs[6] * rhs[13] + lhs[10] * rhs[14] + lhs[14] * rhs[15];
	result[15] = lhs[3] * rhs[12] + lhs[7] * rhs[13] + lhs[11] * rhs[14] + lhs[15] * rhs[15];
}

} // namespace orangesignal
