/*
 * Copyright (c) 2013 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.opengl;

import static com.orangesignal.android.opengl.GLSurfaceView.DEBUG_CHECK_GL_ERROR;
import static com.orangesignal.android.opengl.GLSurfaceView.DEBUG_LOG_GL_CALLS;
import static com.orangesignal.android.opengl.GLSurfaceView.RENDERMODE_CONTINUOUSLY;
import static com.orangesignal.android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;
import static javax.microedition.khronos.egl.EGL10.EGL_BAD_NATIVE_WINDOW;
import static javax.microedition.khronos.egl.EGL10.EGL_DEFAULT_DISPLAY;
import static javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT;
import static javax.microedition.khronos.egl.EGL10.EGL_NO_DISPLAY;
import static javax.microedition.khronos.egl.EGL10.EGL_NO_SURFACE;
import static javax.microedition.khronos.egl.EGL10.EGL_SUCCESS;
import static javax.microedition.khronos.egl.EGL11.EGL_CONTEXT_LOST;

import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLDebugHelper;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import com.orangesignal.android.opengl.GLSurfaceView.EGLConfigChooser;
import com.orangesignal.android.opengl.GLSurfaceView.EGLContextFactory;
import com.orangesignal.android.opengl.GLSurfaceView.EGLWindowSurfaceFactory;
import com.orangesignal.android.opengl.GLSurfaceView.GLWrapper;
import com.orangesignal.android.opengl.GLSurfaceView.Renderer;

/**
 * OpenGL ES 向けの {@link TextureView} を提供します。
 * 
 * @author 杉澤 浩二
 * @deprecated 実装中
 */
@Deprecated
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class GLTextureView extends TextureView implements TextureView.SurfaceTextureListener {

	private final static String TAG = "GLTextureView";

	private final static boolean LOG_ATTACH_DETACH = false;
	private final static boolean LOG_THREADS = false;
	private final static boolean LOG_PAUSE_RESUME = false;
	private final static boolean LOG_SURFACE = false;
	private final static boolean LOG_RENDERER = false;
	private final static boolean LOG_RENDERER_DRAW_FRAME = false;
	private final static boolean LOG_EGL = false;

	private static int sGLESVersion;

	private SurfaceTextureListener mSurfaceTextureListener;

	//////////////////////////////////////////////////////////////////////////
	// コンストラクタ

	/**
	 * Creates a new GLTextureView.
	 * 
	 * @param context The context to associate this view with.
	 */
	public GLTextureView(final Context context) {
		super(context);
		init(context);
	}

	/**
	 * Creates a new GLTextureView.
	 * 
	 * @param context The context to associate this view with.
	 * @param attrs The attributes of the XML tag that is inflating the view.
	 */
	public GLTextureView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * Creates a new GLTextureView.
	 * 
	 * @param context The context to associate this view with.
	 * @param attrs The attributes of the XML tag that is inflating the view.
	 * @param defStyle he default style to apply to this view.
	 *         If 0, no style will be applied (beyond what is included in the theme).
	 *         This may either be an attribute resource, whose value will be retrieved from the current theme, or an explicit style resource.
	 */
	public GLTextureView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(final Context context) {
		if (sGLESVersion == 0) {
			final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			sGLESVersion = am.getDeviceConfigurationInfo().reqGlEsVersion;
		}
		super.setSurfaceTextureListener(this);
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			if (mGLThread != null) {
				// GLThread may still be running if this view was never
				// attached to a window.
				mGLThread.requestExitAndWait();
			}
		} finally {
			super.finalize();
		}
	}

	//////////////////////////////////////////////////////////////////////////
	// Override method

	@Override
	public void setSurfaceTextureListener(final SurfaceTextureListener listener) {
		mSurfaceTextureListener = listener;
	}

	//////////////////////////////////////////////////////////////////////////
	// public method

	/**
	 * Set the glWrapper. If the glWrapper is not null, its
	 * {@link GLWrapper#wrap(GL)} method is called
	 * whenever a surface is created. A GLWrapper can be used to wrap
	 * the GL object that's passed to the renderer. Wrapping a GL
	 * object enables examining and modifying the behavior of the
	 * GL calls made by the renderer.
	 * <p>
	 * Wrapping is typically used for debugging purposes.
	 * <p>
	 * The default value is null.
	 * @param glWrapper the new GLWrapper
	 */
	public void setGLWrapper(final GLWrapper glWrapper) {
		mGLWrapper = glWrapper;
	}

	/**
	 * Set the debug flags to a new value. The value is
	 * constructed by OR-together zero or more
	 * of the DEBUG_CHECK_* constants. The debug flags take effect
	 * whenever a surface is created. The default value is zero.
	 * @param debugFlags the new debug flags
	 * @see #DEBUG_CHECK_GL_ERROR
	 * @see #DEBUG_LOG_GL_CALLS
	 */
	public void setDebugFlags(final int debugFlags) {
		mDebugFlags = debugFlags;
	}

	/**
	 * Get the current value of the debug flags.
	 * @return the current value of the debug flags.
	 */
	public int getDebugFlags() {
		return mDebugFlags;
	}

	/**
	 * Control whether the EGL context is preserved when the GLSurfaceView is paused and
	 * resumed.
	 * <p>
	 * If set to true, then the EGL context may be preserved when the GLSurfaceView is paused.
	 * Whether the EGL context is actually preserved or not depends upon whether the
	 * Android device that the program is running on can support an arbitrary number of EGL
	 * contexts or not. Devices that can only support a limited number of EGL contexts must
	 * release the  EGL context in order to allow multiple applications to share the GPU.
	 * <p>
	 * If set to false, the EGL context will be released when the GLSurfaceView is paused,
	 * and recreated when the GLSurfaceView is resumed.
	 * <p>
	 *
	 * The default is false.
	 *
	 * @param preserveOnPause preserve the EGL context when paused
	 */
	public void setPreserveEGLContextOnPause(final boolean preserveOnPause) {
		mPreserveEGLContextOnPause = preserveOnPause;
	}

	/**
	 * @return true if the EGL context will be preserved when paused
	 */
	public boolean getPreserveEGLContextOnPause() {
		return mPreserveEGLContextOnPause;
	}

	/**
	 * Set the renderer associated with this view. Also starts the thread that
	 * will call the renderer, which in turn causes the rendering to start.
	 * <p>This method should be called once and only once in the life-cycle of
	 * a GLSurfaceView.
	 * <p>The following GLSurfaceView methods can only be called <em>before</em>
	 * setRenderer is called:
	 * <ul>
	 * <li>{@link #setEGLConfigChooser(boolean)}
	 * <li>{@link #setEGLConfigChooser(EGLConfigChooser)}
	 * <li>{@link #setEGLConfigChooser(int, int, int, int, int, int)}
	 * </ul>
	 * <p>
	 * The following GLSurfaceView methods can only be called <em>after</em>
	 * setRenderer is called:
	 * <ul>
	 * <li>{@link #getRenderMode()}
	 * <li>{@link #onPause()}
	 * <li>{@link #onResume()}
	 * <li>{@link #queueEvent(Runnable)}
	 * <li>{@link #requestRender()}
	 * <li>{@link #setRenderMode(int)}
	 * </ul>
	 *
	 * @param renderer the renderer to use to perform OpenGL drawing.
	 */
	public void setRenderer(final Renderer renderer) {
		checkRenderThreadState();
		if (mEGLConfigChooser == null) {
			mEGLConfigChooser = new DefaultConfigChooser(true, mEGLContextClientVersion);
		}
		if (mEGLContextFactory == null) {
			mEGLContextFactory = new DefaultContextFactory(mEGLContextClientVersion);
		}
		if (mEGLWindowSurfaceFactory == null) {
			mEGLWindowSurfaceFactory = new DefaultWindowSurfaceFactory();
		}
		mRenderer = renderer;
		mGLThread = new GLThread(mThisWeakRef);
		mGLThread.start();
	}

	/**
	 * Install a custom EGLContextFactory.
	 * <p>If this method is
	 * called, it must be called before {@link #setRenderer(Renderer)}
	 * is called.
	 * <p>
	 * If this method is not called, then by default
	 * a context will be created with no shared context and
	 * with a null attribute list.
	 */
	public void setEGLContextFactory(final EGLContextFactory factory) {
		checkRenderThreadState();
		mEGLContextFactory = factory;
	}

	/**
	 * Install a custom EGLWindowSurfaceFactory.
	 * <p>If this method is
	 * called, it must be called before {@link #setRenderer(Renderer)}
	 * is called.
	 * <p>
	 * If this method is not called, then by default
	 * a window surface will be created with a null attribute list.
	 */
	public void setEGLWindowSurfaceFactory(final EGLWindowSurfaceFactory factory) {
		checkRenderThreadState();
		mEGLWindowSurfaceFactory = factory;
	}

	/**
	 * Install a custom EGLConfigChooser.
	 * <p>If this method is
	 * called, it must be called before {@link #setRenderer(Renderer)}
	 * is called.
	 * <p>
	 * If no setEGLConfigChooser method is called, then by default the
	 * view will choose an EGLConfig that is compatible with the current
	 * android.view.Surface, with a depth buffer depth of
	 * at least 16 bits.
	 * @param configChooser
	 */
	public void setEGLConfigChooser(final EGLConfigChooser configChooser) {
		checkRenderThreadState();
		mEGLConfigChooser = configChooser;
	}

	/**
	 * Install a config chooser which will choose a config
	 * as close to 16-bit RGB as possible, with or without an optional depth
	 * buffer as close to 16-bits as possible.
	 * <p>If this method is
	 * called, it must be called before {@link #setRenderer(Renderer)}
	 * is called.
	 * <p>
	 * If no setEGLConfigChooser method is called, then by default the
	 * view will choose an RGB_888 surface with a depth buffer depth of
	 * at least 16 bits.
	 *
	 * @param needDepth
	 */
	public void setEGLConfigChooser(final boolean needDepth) {
		setEGLConfigChooser(new DefaultConfigChooser(needDepth, mEGLContextClientVersion));
	}

	/**
	 * Install a config chooser which will choose a config
	 * with at least the specified depthSize and stencilSize,
	 * and exactly the specified redSize, greenSize, blueSize and alphaSize.
	 * <p>If this method is
	 * called, it must be called before {@link #setRenderer(Renderer)}
	 * is called.
	 * <p>
	 * If no setEGLConfigChooser method is called, then by default the
	 * view will choose an RGB_565 / RGB_888 surface with a depth buffer depth of
	 * at least 16 bits.
	 *
	 */
	public void setEGLConfigChooser(final int redSize, final int greenSize, final int blueSize, final int alphaSize, final int depthSize, final int stencilSize) {
		setEGLConfigChooser(new DefaultConfigChooser(redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize, mEGLContextClientVersion));
	}

	/**
	 * Inform the default EGLContextFactory and default EGLConfigChooser
	 * which EGLContext client version to pick.
	 * <p>Use this method to create an OpenGL ES 2.0-compatible context.
	 * Example:
	 * <pre class="prettyprint">
	 *     public MyView(Context context) {
	 *         super(context);
	 *         setEGLContextClientVersion(2); // Pick an OpenGL ES 2.0 context.
	 *         setRenderer(new MyRenderer());
	 *     }
	 * </pre>
	 * <p>Note: Activities which require OpenGL ES 2.0 should indicate this by
	 * setting @lt;uses-feature android:glEsVersion="0x00020000" /> in the activity's
	 * AndroidManifest.xml file.
	 * <p>If this method is called, it must be called before {@link #setRenderer(Renderer)}
	 * is called.
	 * <p>This method only affects the behavior of the default EGLContexFactory and the
	 * default EGLConfigChooser. If
	 * {@link #setEGLContextFactory(EGLContextFactory)} has been called, then the supplied
	 * EGLContextFactory is responsible for creating an OpenGL ES 2.0-compatible context.
	 * If
	 * {@link #setEGLConfigChooser(EGLConfigChooser)} has been called, then the supplied
	 * EGLConfigChooser is responsible for choosing an OpenGL ES 2.0-compatible config.
	 * 
	 * @param version The EGLContext client version to choose. Use 2 for OpenGL ES 2.0
	 */
	public void setEGLContextClientVersion(final int version) {
		checkRenderThreadState();
		mEGLContextClientVersion = version;
	}

	/**
	 * Set the rendering mode. When renderMode is
	 * RENDERMODE_CONTINUOUSLY, the renderer is called
	 * repeatedly to re-render the scene. When renderMode
	 * is RENDERMODE_WHEN_DIRTY, the renderer only rendered when the surface
	 * is created, or when {@link #requestRender} is called. Defaults to RENDERMODE_CONTINUOUSLY.
	 * <p>
	 * Using RENDERMODE_WHEN_DIRTY can improve battery life and overall system performance
	 * by allowing the GPU and CPU to idle when the view does not need to be updated.
	 * <p>
	 * This method can only be called after {@link #setRenderer(Renderer)}
	 *
	 * @param renderMode one of the RENDERMODE_X constants
	 * @see #RENDERMODE_CONTINUOUSLY
	 * @see #RENDERMODE_WHEN_DIRTY
	 */
	public void setRenderMode(final int renderMode) {
		mGLThread.setRenderMode(renderMode);
	}

	/**
	 * Get the current rendering mode. May be called
	 * from any thread. Must not be called before a renderer has been set.
	 * 
	 * @return the current rendering mode.
	 * @see #RENDERMODE_CONTINUOUSLY
	 * @see #RENDERMODE_WHEN_DIRTY
	 */
	public int getRenderMode() {
		return mGLThread.getRenderMode();
	}

	/**
	 * Request that the renderer render a frame.
	 * This method is typically used when the render mode has been set to
	 * {@link #RENDERMODE_WHEN_DIRTY}, so that frames are only rendered on demand.
	 * May be called
	 * from any thread. Must not be called before a renderer has been set.
	 */
	public void requestRender() {
		mGLThread.requestRender();
	}

	/**
	 * This method is part of the SurfaceHolder.Callback interface, and is
	 * not normally called or subclassed by clients of GLSurfaceView.
	 */
	@Override
	public void onSurfaceTextureAvailable(final SurfaceTexture surface, final int width, final int height) {
		mGLThread.surfaceCreated();
		mGLThread.onWindowResize(width, height);

		if (mSurfaceTextureListener != null) {
			mSurfaceTextureListener.onSurfaceTextureAvailable(surface, width, height);
		}
	}

	/**
	 * This method is part of the SurfaceHolder.Callback interface, and is
	 * not normally called or subclassed by clients of GLSurfaceView.
	 */
	@Override
	public boolean onSurfaceTextureDestroyed(final SurfaceTexture surface) {
		try {
			if (mSurfaceTextureListener != null) {
				mSurfaceTextureListener.onSurfaceTextureDestroyed(surface);
			}
		} finally {
			// Surface will be destroyed when we return
			mGLThread.surfaceDestroyed();
//			mGLThread.finish();
//			try {
//				mGLThread.join();
//				mGLThread = null;
//			} catch (final InterruptedException e) {
//				Log.e(GLThread.TAG, "Could not wait for render thread");
//			}
		}
		return true;
	}

	/**
	 * This method is part of the SurfaceHolder.Callback interface, and is
	 * not normally called or subclassed by clients of GLSurfaceView.
	 */
	@Override
	public void onSurfaceTextureSizeChanged(final SurfaceTexture surface, final int width, final int height) {
		mGLThread.onWindowResize(width, height);

		if (mSurfaceTextureListener != null) {
			mSurfaceTextureListener.onSurfaceTextureSizeChanged(surface, width, height);
		}
	}

	@Override
	public void onSurfaceTextureUpdated(final SurfaceTexture surface) {
		if (mSurfaceTextureListener != null) {
			mSurfaceTextureListener.onSurfaceTextureUpdated(surface);
		}
	}

	/**
	 * Inform the view that the activity is paused. The owner of this view must
	 * call this method when the activity is paused. Calling this method will
	 * pause the rendering thread.
	 * Must not be called before a renderer has been set.
	 */
	public void onPause() {
		mGLThread.onPause();
	}

	/**
	 * Inform the view that the activity is resumed. The owner of this view must
	 * call this method when the activity is resumed. Calling this method will
	 * recreate the OpenGL display and resume the rendering
	 * thread.
	 * Must not be called before a renderer has been set.
	 */
	public void onResume() {
		mGLThread.onResume();
	}

	/**
	 * Queue a runnable to be run on the GL rendering thread. This can be used
	 * to communicate with the Renderer on the rendering thread.
	 * Must not be called before a renderer has been set.
	 * 
	 * @param r the runnable to be run on the GL rendering thread.
	 */
	public void queueEvent(final Runnable r) {
		mGLThread.queueEvent(r);
	}

	/**
	 * This method is used as part of the View class and is not normally
	 * called or subclassed by clients of GLSurfaceView.
	 */
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (LOG_ATTACH_DETACH) {
			Log.d(TAG, "onAttachedToWindow reattach =" + mDetached);
		}
		if (mDetached && (mRenderer != null)) {
			int renderMode = RENDERMODE_CONTINUOUSLY;
			if (mGLThread != null) {
				renderMode = mGLThread.getRenderMode();
			}
			mGLThread = new GLThread(mThisWeakRef);
			if (renderMode != RENDERMODE_CONTINUOUSLY) {
				mGLThread.setRenderMode(renderMode);
			}
			mGLThread.start();
		}
		mDetached = false;
	}

	/**
	 * This method is used as part of the View class and is not normally
	 * called or subclassed by clients of GLSurfaceView.
	 * Must not be called before a renderer has been set.
	 */
	@Override
	protected void onDetachedFromWindow() {
		if (LOG_ATTACH_DETACH) {
			Log.d(TAG, "onDetachedFromWindow");
		}
		if (mGLThread != null) {
			mGLThread.requestExitAndWait();
		}
		mDetached = true;
		super.onDetachedFromWindow();
	}

	/**
	 * An EGL helper class.
	 */
	private static class EglHelper {
		public EglHelper(final WeakReference<GLTextureView> glSurfaceViewWeakRef) {
			mGLSurfaceViewWeakRef = glSurfaceViewWeakRef;
		}

		/**
		 * Initialize EGL for a given configuration spec.
		 */
		public void start() {
			if (LOG_EGL) {
				Log.w("EglHelper", "start() tid=" + Thread.currentThread().getId());
			}
			/*
			 * Get an EGL instance
			 */
			mEgl = (EGL10) EGLContext.getEGL();

			/*
			 * Get to the default display.
			 */
			mEglDisplay = mEgl.eglGetDisplay(EGL_DEFAULT_DISPLAY);

			if (mEglDisplay == EGL_NO_DISPLAY) {
				throw new RuntimeException("eglGetDisplay failed");
			}

			/*
			 * We can now initialize EGL for that display
			 */
			int[] version = new int[2];
			if(!mEgl.eglInitialize(mEglDisplay, version)) {
				throw new RuntimeException("eglInitialize failed");
			}
			GLTextureView view = mGLSurfaceViewWeakRef.get();
			if (view == null) {
				mEglConfig = null;
				mEglContext = null;
			} else {
				mEglConfig = view.mEGLConfigChooser.chooseConfig(mEgl, mEglDisplay);

				/*
				 * Create an EGL context. We want to do this as rarely as we can, because an
				 * EGL context is a somewhat heavy object.
				 */
				mEglContext = view.mEGLContextFactory.createContext(mEgl, mEglDisplay, mEglConfig);
			}
			if (mEglContext == null || mEglContext == EGL_NO_CONTEXT) {
				mEglContext = null;
				throwEglException("createContext");
			}
			if (LOG_EGL) {
				Log.w("EglHelper", "createContext " + mEglContext + " tid=" + Thread.currentThread().getId());
			}

			mEglSurface = null;
		}

		/**
		 * Create an egl surface for the current SurfaceHolder surface. If a surface
		 * already exists, destroy it before creating the new surface.
		 *
		 * @return true if the surface was created successfully.
		 */
		public boolean createSurface() {
			if (LOG_EGL) {
				Log.w("EglHelper", "createSurface()  tid=" + Thread.currentThread().getId());
			}
			/*
			 * Check preconditions.
			 */
			if (mEgl == null) {
				throw new RuntimeException("egl not initialized");
			}
			if (mEglDisplay == null) {
				throw new RuntimeException("eglDisplay not initialized");
			}
			if (mEglConfig == null) {
				throw new RuntimeException("mEglConfig not initialized");
			}

			/*
			 *  The window size has changed, so we need to create a new
			 *  surface.
			 */
			destroySurfaceImp();

			/*
			 * Create an EGL surface we can render into.
			 */
			GLTextureView view = mGLSurfaceViewWeakRef.get();
			if (view != null) {
				mEglSurface = view.mEGLWindowSurfaceFactory.createWindowSurface(mEgl, mEglDisplay, mEglConfig, view.getSurfaceTexture());
			} else {
				mEglSurface = null;
			}

			if (mEglSurface == null || mEglSurface == EGL_NO_SURFACE) {
				final int error = mEgl.eglGetError();
				if (error == EGL_BAD_NATIVE_WINDOW) {
					Log.e("EglHelper", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
				}
				return false;
			}

			/*
			 * Before we can issue GL commands, we need to make sure
			 * the context is current and bound to a surface.
			 */
			if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
				/*
				 * Could not make the context current, probably because the underlying
				 * SurfaceView surface has been destroyed.
				 */
				logEglErrorAsWarning("EGLHelper", "eglMakeCurrent", mEgl.eglGetError());
				return false;
			}

			return true;
		}

		/**
		 * Create a GL object for the current EGL context.
		 * @return
		 */
		GL createGL() {

			GL gl = mEglContext.getGL();
			final GLTextureView view = mGLSurfaceViewWeakRef.get();
			if (view != null) {
				if (view.mGLWrapper != null) {
					gl = view.mGLWrapper.wrap(gl);
				}

				if ((view.mDebugFlags & (DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS)) != 0) {
					int configFlags = 0;
					Writer log = null;
					if ((view.mDebugFlags & DEBUG_CHECK_GL_ERROR) != 0) {
						configFlags |= GLDebugHelper.CONFIG_CHECK_GL_ERROR;
					}
					if ((view.mDebugFlags & DEBUG_LOG_GL_CALLS) != 0) {
						log = new LogWriter(GLTextureView.TAG);
					}
					gl = GLDebugHelper.wrap(gl, configFlags, log);
				}
			}
			return gl;
		}

		/**
		 * Display the current render surface.
		 * @return the EGL error code from eglSwapBuffers.
		 */
		public int swap() {
			if (! mEgl.eglSwapBuffers(mEglDisplay, mEglSurface)) {
				return mEgl.eglGetError();
			}
			return EGL_SUCCESS;
		}

		public void destroySurface() {
			if (LOG_EGL) {
				Log.w("EglHelper", "destroySurface()  tid=" + Thread.currentThread().getId());
			}
			destroySurfaceImp();
		}

		private void destroySurfaceImp() {
			if (mEglSurface != null && mEglSurface != EGL_NO_SURFACE) {
				mEgl.eglMakeCurrent(mEglDisplay, EGL_NO_SURFACE,
						EGL_NO_SURFACE,
						EGL_NO_CONTEXT);
				final GLTextureView view = mGLSurfaceViewWeakRef.get();
				if (view != null) {
					view.mEGLWindowSurfaceFactory.destroySurface(mEgl, mEglDisplay, mEglSurface);
				}
				mEglSurface = null;
			}
		}

		public void finish() {
			if (LOG_EGL) {
				Log.w("EglHelper", "finish() tid=" + Thread.currentThread().getId());
			}
			if (mEglContext != null) {
				final GLTextureView view = mGLSurfaceViewWeakRef.get();
				if (view != null) {
					view.mEGLContextFactory.destroyContext(mEgl, mEglDisplay, mEglContext);
				}
				mEglContext = null;
			}
			if (mEglDisplay != null) {
				mEgl.eglTerminate(mEglDisplay);
				mEglDisplay = null;
			}
		}

		private void throwEglException(final String function) {
			throwEglException(function, mEgl.eglGetError());
		}

		public static void throwEglException(final String function, final int error) {
			final String message = formatEglError(function, error);
			if (LOG_THREADS) {
				Log.e("EglHelper", "throwEglException tid=" + Thread.currentThread().getId() + " " + message);
			}
			throw new RuntimeException(message);
		}

		public static void logEglErrorAsWarning(final String tag, final String function, final int error) {
			Log.w(tag, formatEglError(function, error));
		}

		public static String formatEglError(final String function, final int error) {
			return function + " failed: " + EGLLogWrapper.getErrorString(error);
		}

		private WeakReference<GLTextureView> mGLSurfaceViewWeakRef;
		EGL10 mEgl;
		EGLDisplay mEglDisplay;
		EGLSurface mEglSurface;
		EGLConfig mEglConfig;
		EGLContext mEglContext;

	}

	/**
	 * A generic GL Thread. Takes care of initializing EGL and GL. Delegates
	 * to a Renderer instance to do the actual drawing. Can be configured to
	 * render continuously or on request.
	 *
	 * All potentially blocking synchronization is done through the
	 * sGLThreadManager object. This avoids multiple-lock ordering issues.
	 *
	 */
	static class GLThread extends Thread {
		GLThread(final WeakReference<GLTextureView> glSurfaceViewWeakRef) {
			super();
			mWidth = 0;
			mHeight = 0;
			mRequestRender = true;
			mRenderMode = RENDERMODE_CONTINUOUSLY;
			mGLSurfaceViewWeakRef = glSurfaceViewWeakRef;
		}

		@Override
		public void run() {
			setName("GLThread " + getId());
			if (LOG_THREADS) {
				Log.i("GLThread", "starting tid=" + getId());
			}

			try {
				guardedRun();
			} catch (final InterruptedException e) {
				// fall thru and exit normally
			} finally {
				sGLThreadManager.threadExiting(this);
			}
		}

		/*
		 * This private method should only be called inside a
		 * synchronized(sGLThreadManager) block.
		 */
		private void stopEglSurfaceLocked() {
			if (mHaveEglSurface) {
				mHaveEglSurface = false;
				mEglHelper.destroySurface();
			}
		}

		/*
		 * This private method should only be called inside a
		 * synchronized(sGLThreadManager) block.
		 */
		private void stopEglContextLocked() {
			if (mHaveEglContext) {
				mEglHelper.finish();
				mHaveEglContext = false;
				sGLThreadManager.releaseEglContextLocked(this);
			}
		}

		private void guardedRun() throws InterruptedException {
			mEglHelper = new EglHelper(mGLSurfaceViewWeakRef);
			mHaveEglContext = false;
			mHaveEglSurface = false;
			try {
				GL10 gl = null;
				boolean createEglContext = false;
				boolean createEglSurface = false;
				boolean createGlInterface = false;
				boolean lostEglContext = false;
				boolean sizeChanged = false;
				boolean wantRenderNotification = false;
				boolean doRenderNotification = false;
				boolean askedToReleaseEglContext = false;
				int w = 0;
				int h = 0;
				Runnable event = null;

				while (true) {
					synchronized (sGLThreadManager) {
						while (true) {
							if (mShouldExit) {
								return;
							}

							if (! mEventQueue.isEmpty()) {
								event = mEventQueue.remove(0);
								break;
							}

							// Update the pause state.
							boolean pausing = false;
							if (mPaused != mRequestPaused) {
								pausing = mRequestPaused;
								mPaused = mRequestPaused;
								sGLThreadManager.notifyAll();
								if (LOG_PAUSE_RESUME) {
									Log.i("GLThread", "mPaused is now " + mPaused + " tid=" + getId());
								}
							}

							// Do we need to give up the EGL context?
							if (mShouldReleaseEglContext) {
								if (LOG_SURFACE) {
									Log.i("GLThread", "releasing EGL context because asked to tid=" + getId());
								}
								stopEglSurfaceLocked();
								stopEglContextLocked();
								mShouldReleaseEglContext = false;
								askedToReleaseEglContext = true;
							}

							// Have we lost the EGL context?
							if (lostEglContext) {
								stopEglSurfaceLocked();
								stopEglContextLocked();
								lostEglContext = false;
							}

							// When pausing, release the EGL surface:
							if (pausing && mHaveEglSurface) {
								if (LOG_SURFACE) {
									Log.i("GLThread", "releasing EGL surface because paused tid=" + getId());
								}
								stopEglSurfaceLocked();
							}

							// When pausing, optionally release the EGL Context:
							if (pausing && mHaveEglContext) {
								final GLTextureView view = mGLSurfaceViewWeakRef.get();
								final boolean preserveEglContextOnPause = view == null ? false : view.mPreserveEGLContextOnPause;
								if (!preserveEglContextOnPause || sGLThreadManager.shouldReleaseEGLContextWhenPausing()) {
									stopEglContextLocked();
									if (LOG_SURFACE) {
										Log.i("GLThread", "releasing EGL context because paused tid=" + getId());
									}
								}
							}

							// When pausing, optionally terminate EGL:
							if (pausing) {
								if (sGLThreadManager.shouldTerminateEGLWhenPausing()) {
									mEglHelper.finish();
									if (LOG_SURFACE) {
										Log.i("GLThread", "terminating EGL because paused tid=" + getId());
									}
								}
							}

							// Have we lost the SurfaceView surface?
							if ((! mHasSurface) && (! mWaitingForSurface)) {
								if (LOG_SURFACE) {
									Log.i("GLThread", "noticed surfaceView surface lost tid=" + getId());
								}
								if (mHaveEglSurface) {
									stopEglSurfaceLocked();
								}
								mWaitingForSurface = true;
								mSurfaceIsBad = false;
								sGLThreadManager.notifyAll();
							}

							// Have we acquired the surface view surface?
							if (mHasSurface && mWaitingForSurface) {
								if (LOG_SURFACE) {
									Log.i("GLThread", "noticed surfaceView surface acquired tid=" + getId());
								}
								mWaitingForSurface = false;
								sGLThreadManager.notifyAll();
							}

							if (doRenderNotification) {
								if (LOG_SURFACE) {
									Log.i("GLThread", "sending render notification tid=" + getId());
								}
								wantRenderNotification = false;
								doRenderNotification = false;
								mRenderComplete = true;
								sGLThreadManager.notifyAll();
							}

							// Ready to draw?
							if (readyToDraw()) {

								// If we don't have an EGL context, try to acquire one.
								if (! mHaveEglContext) {
									if (askedToReleaseEglContext) {
										askedToReleaseEglContext = false;
									} else if (sGLThreadManager.tryAcquireEglContextLocked(this)) {
										try {
											mEglHelper.start();
										} catch (final RuntimeException t) {
											sGLThreadManager.releaseEglContextLocked(this);
											throw t;
										}
										mHaveEglContext = true;
										createEglContext = true;

										sGLThreadManager.notifyAll();
									}
								}

								if (mHaveEglContext && !mHaveEglSurface) {
									mHaveEglSurface = true;
									createEglSurface = true;
									createGlInterface = true;
									sizeChanged = true;
								}

								if (mHaveEglSurface) {
									if (mSizeChanged) {
										sizeChanged = true;
										w = mWidth;
										h = mHeight;
										wantRenderNotification = true;
										if (LOG_SURFACE) {
											Log.i("GLThread", "noticing that we want render notification tid=" + getId());
										}

										// Destroy and recreate the EGL surface.
										createEglSurface = true;

										mSizeChanged = false;
									}
									mRequestRender = false;
									sGLThreadManager.notifyAll();
									break;
								}
							}

							// By design, this is the only place in a GLThread thread where we wait().
							if (LOG_THREADS) {
								Log.i("GLThread", "waiting tid=" + getId()
										+ " mHaveEglContext: " + mHaveEglContext
										+ " mHaveEglSurface: " + mHaveEglSurface
										+ " mPaused: " + mPaused
										+ " mHasSurface: " + mHasSurface
										+ " mSurfaceIsBad: " + mSurfaceIsBad
										+ " mWaitingForSurface: " + mWaitingForSurface
										+ " mWidth: " + mWidth
										+ " mHeight: " + mHeight
										+ " mRequestRender: " + mRequestRender
										+ " mRenderMode: " + mRenderMode);
							}
							sGLThreadManager.wait();
						}
					} // end of synchronized(sGLThreadManager)

					if (event != null) {
						event.run();
						event = null;
						continue;
					}

					if (createEglSurface) {
						if (LOG_SURFACE) {
							Log.w("GLThread", "egl createSurface");
						}
						if (!mEglHelper.createSurface()) {
							synchronized (sGLThreadManager) {
								mSurfaceIsBad = true;
								sGLThreadManager.notifyAll();
							}
							continue;
						}
						createEglSurface = false;
					}

					if (createGlInterface) {
						gl = (GL10) mEglHelper.createGL();

						sGLThreadManager.checkGLDriver(gl);
						createGlInterface = false;
					}

					if (createEglContext) {
						if (LOG_RENDERER) {
							Log.w("GLThread", "onSurfaceCreated");
						}
						final GLTextureView view = mGLSurfaceViewWeakRef.get();
						if (view != null) {
							view.mRenderer.onSurfaceCreated(gl, mEglHelper.mEglConfig);
						}
						createEglContext = false;
					}

					if (sizeChanged) {
						if (LOG_RENDERER) {
							Log.w("GLThread", "onSurfaceChanged(" + w + ", " + h + ")");
						}
						final GLTextureView view = mGLSurfaceViewWeakRef.get();
						if (view != null) {
							view.mRenderer.onSurfaceChanged(gl, w, h);
						}
						sizeChanged = false;
					}

					if (LOG_RENDERER_DRAW_FRAME) {
						Log.w("GLThread", "onDrawFrame tid=" + getId());
					}
					{
						final GLTextureView view = mGLSurfaceViewWeakRef.get();
						if (view != null) {
							view.mRenderer.onDrawFrame(gl);
						}
					}
					final int swapError = mEglHelper.swap();
					switch (swapError) {
						case EGL_SUCCESS:
							break;
						case EGL_CONTEXT_LOST:
							if (LOG_SURFACE) {
								Log.i("GLThread", "egl context lost tid=" + getId());
							}
							lostEglContext = true;
							break;
						default:
							// Other errors typically mean that the current surface is bad,
							// probably because the SurfaceView surface has been destroyed,
							// but we haven't been notified yet.
							// Log the error to help developers understand why rendering stopped.
							EglHelper.logEglErrorAsWarning("GLThread", "eglSwapBuffers", swapError);

							synchronized (sGLThreadManager) {
								mSurfaceIsBad = true;
								sGLThreadManager.notifyAll();
							}
							break;
					}

					if (wantRenderNotification) {
						doRenderNotification = true;
					}
				}

			} finally {
				/*
				 * clean-up everything...
				 */
				synchronized (sGLThreadManager) {
					stopEglSurfaceLocked();
					stopEglContextLocked();
				}
			}
		}

		public boolean ableToDraw() {
			return mHaveEglContext && mHaveEglSurface && readyToDraw();
		}

		private boolean readyToDraw() {
			return (!mPaused) && mHasSurface && (!mSurfaceIsBad)
					&& (mWidth > 0) && (mHeight > 0)
					&& (mRequestRender || (mRenderMode == RENDERMODE_CONTINUOUSLY));
		}

		public void setRenderMode(final int renderMode) {
			if ( !((RENDERMODE_WHEN_DIRTY <= renderMode) && (renderMode <= RENDERMODE_CONTINUOUSLY)) ) {
				throw new IllegalArgumentException("renderMode");
			}
			synchronized(sGLThreadManager) {
				mRenderMode = renderMode;
				sGLThreadManager.notifyAll();
			}
		}

		public int getRenderMode() {
			synchronized(sGLThreadManager) {
				return mRenderMode;
			}
		}

		public void requestRender() {
			synchronized(sGLThreadManager) {
				mRequestRender = true;
				sGLThreadManager.notifyAll();
			}
		}

		public void surfaceCreated() {
			synchronized(sGLThreadManager) {
				if (LOG_THREADS) {
					Log.i("GLThread", "surfaceCreated tid=" + getId());
				}
				mHasSurface = true;
				sGLThreadManager.notifyAll();
				while((mWaitingForSurface) && (!mExited)) {
					try {
						sGLThreadManager.wait();
					} catch (final InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}

		public void surfaceDestroyed() {
			synchronized(sGLThreadManager) {
				if (LOG_THREADS) {
					Log.i("GLThread", "surfaceDestroyed tid=" + getId());
				}
				mHasSurface = false;
				sGLThreadManager.notifyAll();
				while((!mWaitingForSurface) && (!mExited)) {
					try {
						sGLThreadManager.wait();
					} catch (final InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}

		public void onPause() {
			synchronized (sGLThreadManager) {
				if (LOG_PAUSE_RESUME) {
					Log.i("GLThread", "onPause tid=" + getId());
				}
				mRequestPaused = true;
				sGLThreadManager.notifyAll();
				while ((! mExited) && (! mPaused)) {
					if (LOG_PAUSE_RESUME) {
						Log.i("Main thread", "onPause waiting for mPaused.");
					}
					try {
						sGLThreadManager.wait();
					} catch (final InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}

		public void onResume() {
			synchronized (sGLThreadManager) {
				if (LOG_PAUSE_RESUME) {
					Log.i("GLThread", "onResume tid=" + getId());
				}
				mRequestPaused = false;
				mRequestRender = true;
				mRenderComplete = false;
				sGLThreadManager.notifyAll();
				while ((! mExited) && mPaused && (!mRenderComplete)) {
					if (LOG_PAUSE_RESUME) {
						Log.i("Main thread", "onResume waiting for !mPaused.");
					}
					try {
						sGLThreadManager.wait();
					} catch (final InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}

		public void onWindowResize(final int width, final int height) {
			synchronized (sGLThreadManager) {
				mWidth = width;
				mHeight = height;
				mSizeChanged = true;
				mRequestRender = true;
				mRenderComplete = false;
				sGLThreadManager.notifyAll();

				// Wait for thread to react to resize and render a frame
				while (! mExited && !mPaused && !mRenderComplete && ableToDraw()) {
					if (LOG_SURFACE) {
						Log.i("Main thread", "onWindowResize waiting for render complete from tid=" + getId());
					}
					try {
						sGLThreadManager.wait();
					} catch (final InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}

		public void requestExitAndWait() {
			// don't call this from GLThread thread or it is a guaranteed
			// deadlock!
			synchronized(sGLThreadManager) {
				mShouldExit = true;
				sGLThreadManager.notifyAll();
				while (! mExited) {
					try {
						sGLThreadManager.wait();
					} catch (final InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}

		public void requestReleaseEglContextLocked() {
			mShouldReleaseEglContext = true;
			sGLThreadManager.notifyAll();
		}

		/**
		 * Queue an "event" to be run on the GL rendering thread.
		 * @param r the runnable to be run on the GL rendering thread.
		 */
		public void queueEvent(final Runnable r) {
			if (r == null) {
				throw new IllegalArgumentException("r must not be null");
			}
			synchronized(sGLThreadManager) {
				mEventQueue.add(r);
				sGLThreadManager.notifyAll();
			}
		}

		// Once the thread is started, all accesses to the following member
		// variables are protected by the sGLThreadManager monitor
		private boolean mShouldExit;
		private boolean mExited;
		private boolean mRequestPaused;
		private boolean mPaused;
		private boolean mHasSurface;
		private boolean mSurfaceIsBad;
		private boolean mWaitingForSurface;
		private boolean mHaveEglContext;
		private boolean mHaveEglSurface;
		private boolean mShouldReleaseEglContext;
		private int mWidth;
		private int mHeight;
		private int mRenderMode;
		private boolean mRequestRender;
		private boolean mRenderComplete;
		private ArrayList<Runnable> mEventQueue = new ArrayList<Runnable>();
		private boolean mSizeChanged = true;

		// End of member variables protected by the sGLThreadManager monitor.

		private EglHelper mEglHelper;

		/**
		 * Set once at thread construction time, nulled out when the parent view is garbage
		 * called. This weak reference allows the GLSurfaceView to be garbage collected while
		 * the GLThread is still alive.
		 */
		private WeakReference<GLTextureView> mGLSurfaceViewWeakRef;

	}

	private void checkRenderThreadState() {
		if (mGLThread != null) {
			throw new IllegalStateException("setRenderer has already been called for this instance.");
		}
	}

	private static class GLThreadManager {
		private static String TAG = "GLThreadManager";

		public synchronized void threadExiting(final GLThread thread) {
			if (LOG_THREADS) {
				Log.i("GLThread", "exiting tid=" + thread.getId());
			}
			thread.mExited = true;
			if (mEglOwner == thread) {
				mEglOwner = null;
			}
			notifyAll();
		}

		/*
		 * Tries once to acquire the right to use an EGL context. Does not
		 * block. Requires that we are already in the sGLThreadManager monitor
		 * when this is called.
		 * @return true if the right to use an EGL context was acquired.
		 */
		public boolean tryAcquireEglContextLocked(final GLThread thread) {
			if (mEglOwner == thread || mEglOwner == null) {
				mEglOwner = thread;
				notifyAll();
				return true;
			}
			checkGLESVersion();
			if (mMultipleGLESContextsAllowed) {
				return true;
			}
			// Notify the owning thread that it should release the context.
			// TODO: implement a fairness policy. Currently
			// if the owning thread is drawing continuously it will just
			// reacquire the EGL context.
			if (mEglOwner != null) {
				mEglOwner.requestReleaseEglContextLocked();
			}
			return false;
		}

		/*
		 * Releases the EGL context. Requires that we are already in the
		 * sGLThreadManager monitor when this is called.
		 */
		public void releaseEglContextLocked(final GLThread thread) {
			if (mEglOwner == thread) {
				mEglOwner = null;
			}
			notifyAll();
		}

		public synchronized boolean shouldReleaseEGLContextWhenPausing() {
			// Release the EGL context when pausing even if
			// the hardware supports multiple EGL contexts.
			// Otherwise the device could run out of EGL contexts.
			return mLimitedGLESContexts;
		}

		public synchronized boolean shouldTerminateEGLWhenPausing() {
			checkGLESVersion();
			return !mMultipleGLESContextsAllowed;
		}

		public synchronized void checkGLDriver(final GL10 gl) {
			if (!mGLESDriverCheckComplete) {
				checkGLESVersion();
				final String renderer = gl.glGetString(GL10.GL_RENDERER);
				if (sGLESVersion < kGLES_20) {
					mMultipleGLESContextsAllowed = !renderer.startsWith(kMSM7K_RENDERER_PREFIX);
					notifyAll();
				}
				
				
				mLimitedGLESContexts = !mMultipleGLESContextsAllowed || (Integer.parseInt(Build.VERSION.SDK) < JELLY_BEAN && renderer.startsWith(kADRENO));
				if (LOG_SURFACE) {
					Log.w(TAG, "checkGLDriver renderer = \"" + renderer + "\" multipleContextsAllowed = " + mMultipleGLESContextsAllowed + " mLimitedGLESContexts = " + mLimitedGLESContexts);
				}
				mGLESDriverCheckComplete = true;
			}
		}

		private void checkGLESVersion() {
			if (!mGLESVersionCheckComplete) {
				if (sGLESVersion >= kGLES_20) {
					mMultipleGLESContextsAllowed = true;
				}
				if (LOG_SURFACE) {
					Log.w(TAG, "checkGLESVersion mGLESVersion =" + " " + sGLESVersion + " mMultipleGLESContextsAllowed = " + mMultipleGLESContextsAllowed);
				}
				mGLESVersionCheckComplete = true;
			}
		}

		/**
		 * This check was required for some pre-Android-3.0 hardware. Android 3.0 provides
		 * support for hardware-accelerated views, therefore multiple EGL contexts are
		 * supported on all Android 3.0+ EGL drivers.
		 */
		private boolean mGLESVersionCheckComplete;
		private boolean mGLESDriverCheckComplete;
		private boolean mMultipleGLESContextsAllowed;
		private boolean mLimitedGLESContexts;
		private static final int kGLES_20 = 0x20000;
		private static final String kMSM7K_RENDERER_PREFIX = "Q3Dimension MSM7500 ";
		private static final String kADRENO = "Adreno";
		private static final int JELLY_BEAN = 16;
		private GLThread mEglOwner;
	}

	private static final GLThreadManager sGLThreadManager = new GLThreadManager();

	private final WeakReference<GLTextureView> mThisWeakRef = new WeakReference<GLTextureView>(this);
	private GLThread mGLThread;
	private Renderer mRenderer;
	private boolean mDetached;
	private EGLConfigChooser mEGLConfigChooser;
	private EGLContextFactory mEGLContextFactory;
	private EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
	private GLWrapper mGLWrapper;
	private int mDebugFlags;
	private int mEGLContextClientVersion;
	private boolean mPreserveEGLContextOnPause;
}
