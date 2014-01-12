/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#include "Mutex.h"

namespace orangesignal {

inline Mutex::Mutex() {
	pthread_mutex_init(&mMutex, NULL);
}

inline Mutex::Mutex(const char* name) {
	pthread_mutex_init(&mMutex, NULL);
}

inline Mutex::Mutex(int type, const char* name) {
	if (type == SHARED) {
		pthread_mutexattr_t attr;
		pthread_mutexattr_init(&attr);
		pthread_mutexattr_settype(&attr, PTHREAD_PROCESS_SHARED);
		pthread_mutex_init(&mMutex, &attr);
		pthread_mutexattr_destroy(&attr);
	} else {
		pthread_mutex_init(&mMutex, NULL);
	}
}

inline Mutex::~Mutex() {
	pthread_mutex_destroy(&mMutex);
}

inline void Mutex::lock() {
	pthread_mutex_lock(&mMutex);
}

inline void Mutex::unlock() {
	pthread_mutex_unlock(&mMutex);
}

inline void Mutex::tryLock() {
	pthread_mutex_trylock(&mMutex);
}

} // namespace orangesignal
