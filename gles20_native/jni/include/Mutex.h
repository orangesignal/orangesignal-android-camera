/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

#ifndef ORANGESIGNAL_MUTEX_H_
#define ORANGESIGNAL_MUTEX_H_
/*
#include <stdint.h>
#include <sys/types.h>
#include <time.h>
*/
#include <pthread.h>

namespace orangesignal {

/**
 * Simple mutex class.  The implementation is system-dependent.
 *
 * The mutex must be unlocked by the thread that locked it.  They are not
 * recursive, i.e. the same thread can't lock it multiple times.
 */
class Mutex {
private:

	/**
	 * ミューテックスオブジェクトを保持します。
	 */
	pthread_mutex_t mMutex;

	// A mutex cannot be copied
	Mutex(const Mutex&);
	Mutex& operator = (const Mutex&);

public:

	enum {
		PRIVATE = 0,
		SHARED = 1
	};

	/**
	 * デフォルトコンストラクタです。
	 */
	Mutex();
	Mutex(const char* name);
	Mutex(int type, const char* name = NULL);

	/**
	 * デストラクタです。
	 */
	~Mutex();

	/**
	 * ロックを取得します。
	 */
	void lock();

	/**
	 * このロックを解除します。
	 */
	void unlock();

	void tryLock();

	/**
	 * Manages the mutex automatically. It'll be locked when Autolock is
	 * constructed and released when Autolock goes out of scope.
	 */
	class Autolock {
	private:
		Mutex& mLock;
	public:
		inline Autolock(Mutex& mutex) : mLock(mutex)  { mLock.lock(); }
		inline Autolock(Mutex* mutex) : mLock(*mutex) { mLock.lock(); }
		inline ~Autolock() { mLock.unlock(); }
	};
};

typedef Mutex::Autolock AutoMutex;

} // namespace orangesignal
#endif // ORANGESIGNAL_MUTEX_H_
