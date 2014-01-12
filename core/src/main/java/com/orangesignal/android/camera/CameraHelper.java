/*
 * Copyright (c) 2012 OrangeSignal.com All Rights Reserved.
 */

package com.orangesignal.android.camera;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.SurfaceHolder;

/**
 * {@link android.hardware.Camera} の基本操作について、Android のバージョン依存を気にせずに操作可能とするヘルパーのインタフェースを提供します。
 * 
 * @author 杉澤 浩二
 */
public interface CameraHelper {

	/**
	 * 既定のカメラ ID です。
	 */
	static final int DEFAULT_CAMERA_ID = 0;

	/**
	 * このデバイス上で使用可能なカメラの数を返します。
	 * 
	 * @return 使用可能なカメラの数。
	 * @see {@link android.hardware.Camera#getNumberOfCameras()}
	 */
	int getNumberOfCameras();

	/**
	 * 現在のカメラ ID を返します。
	 * 
	 * @return 現在のカメラ ID
	 * @see {@link #DEFAULT_CAMERA_ID}
	 */
	int getCameraId();

	/**
	 * カメラに関する情報を提供します。
	 */
	public static class CameraInfoCompat {

		/**
		 * カメラの面が画面の反対の面にある場合の定数です。
		 */
		public static final int CAMERA_FACING_BACK = 0;

		/**
		 * カメラの面が画面と同じ面にある場合の定数です。
		 */
		public static final int CAMERA_FACING_FRONT = 1;

		/**
		 * カメラの面の方向です。{@link #CAMERA_FACING_BACK} または {@link #CAMERA_FACING_FRONT} のいずれかでなければなりません。
		 */
		public int facing;

		/**
		 * カメラ画像の傾きです。
		 * 値は、それが自然なオリエンテーションでディスプレイに正しく見えるように、カメラ画像を時計回りに回転する必要がある角度です。
		 * 値は、{@code 0}、{@code 90}、{@code 180}、{@code 270} のいずれかでなければなりません。<p>
		 * 
		 * @see {@link android.hardware.Camera#setDisplayOrientation(int)}
		 * @see {@link android.hardware.Camera.Parameters#setRotation(int)}
		 * @see {@link android.hardware.Camera.Parameters#setPreviewSize(int, int)}
		 * @see {@link android.hardware.Camera.Parameters#setPictureSize(int, int)}
		 * @see {@link android.hardware.Camera.Parameters#setJpegThumbnailSize(int, int)}
		 */
		public int orientation;

	}

	/**
	 * 現在のカメラのカメラ情報を返します。<p>
	 * 実装は {@code null} を返してはなりません。
	 * 
	 * @return カメラ情報。
	 */
	CameraInfoCompat getCameraInfo();

	/**
	 * 現在のカメラがフェイスカメラであるかどうかを返します。
	 * 
	 * @return 現在のカメラがフェイスカメラであるかどうか
	 */
	boolean isFaceCamera();

	//////////////////////////////////////////////////////////////////////////
	// カメラの接続・切断

	/**
	 * カメラへ接続しているかどうかを返します。
	 * 
	 * @return カメラへ接続しているかどうか
	 * @see {@link #openCamera(int)}
	 */
	boolean isOpened();

	/**
	 * 指定されたカメラを現在のカメラとして接続します。
	 * カメラへ接続中の場合は接続中のカメラから切断後に、指定されたカメラへ接続します。
	 * 実装はカメラへ接続後、{@link #initializeFocusMode()} を呼び出してフォーカスモードを初期化します。
	 * 
	 * @param cameraId カメラ ID
	 * @throws {@link RuntimeException} カメラへの接続に失敗した場合
	 * @see {@link #initializeFocusMode()}
	 */
	void openCamera(int cameraId);

	/**
	 * 現在のカメラの次のカメラを開きます。
	 */
	void nextCamera();

	/**
	 * フォーカスモードを初期化します。
	 * 
	 * @see {@link #openCamera(int)}
	 */
	void initializeFocusMode();

	/**
	 * 現在のカメラから切断します。
	 * 実装はカメラへ接続していない場合は何も行いません。
	 */
	void releaseCamera();

	//////////////////////////////////////////////////////////////////////////
	// エラーハンドリング

	/**
	 * @see {@link Camera#setErrorCallback(android.hardware.Camera.ErrorCallback)}
	 */
	void setErrorCallback(Camera.ErrorCallback cb);

	//////////////////////////////////////////////////////////////////////////
	// プレビューの開始・停止

	/**
	 * 可能であれば指定されたサイズとカメラのプレビューサイズおよびピクチャーサイズの縦横比 (アスペクト比) が合うように調整します。
	 * 
	 * @param measureWidth 幅
	 * @param measureHeight 高さ
	 * @param maxSize 制限サイズ (オプショナル)
	 * @param requiredSameSize
	 */
	void setupOptimalPreviewSizeAndPictureSize(int measureWidth, int measureHeight, int maxSize);

	/**
	 * 現在のカメラの傾きを返します。
	 * 
	 * @return 現在のカメラの傾き
	 */
	int getOptimalOrientation();
	int getOrientation();

	/**
	 * Android 2.2 未満の端末では、縦置きのプレビューをサポートしているかは実機依存です。
	 * そして 2.2 以降でもプレビューだけでなくピクチャの方向もサポートしているかは機種依存です。
	 * 機種依存なので Android 2.2 未満の端末もサポートする場合は、Android 2.2 未満の環境では実質的に横置きレイアウトとするのが前提となります。
	 * 
	 * @see {@link Camera#setDisplayOrientation(int)}
	 */
	void setDisplayOrientation(int degrees);

	/**
	 * 指定されたプレビューコールバックを適切な方法で設定します。
	 * 
	 * @param cb プレビューコールバック
	 */
	void setPreviewCallback(Camera.PreviewCallback cb);

	/**
	 * @see {@link Camera#setPreviewDisplay(SurfaceHolder)}
	 */
	void setPreviewDisplay(SurfaceHolder holder) throws IOException;

	/**
	 * 実装は、{@link Camera#setPreviewTexture(android.graphics.SurfaceTexture)} を呼び出します。
	 * 
	 * @param surfaceTexture the {@link android.graphics.SurfaceTexture} to which the preview images are to be sent or null to remove the current preview surface texture
	 * @see {@link Camera#setPreviewTexture(android.graphics.SurfaceTexture)}
	 */
	void setPreviewTexture(Object surfaceTexture) throws IOException;

	/**
	 * プレビューを開始します。
	 */
	void startPreview();

	/**
	 * {@link Camera.PreviewCallback#onPreviewFrame(byte[], Camera)} 呼出し後に再度、{@link Camera.PreviewCallback#onPreviewFrame(byte[], Camera)} が呼ばれるようにするための追加の処理を実行します。
	 * 
	 * @param cb {@link Camera.PreviewCallback}
	 */
	void onPreviewFrame(Camera.PreviewCallback cb);

	/**
	 * プレビューを停止します。
	 */
	void stopPreview();

	//////////////////////////////////////////////////////////////////////////
	// キャプチャ

	/**
	 * キャプチャを開始します。
	 * 実装は可能であればオートフォーカスを処理するとして単に {@link #takePicture(android.hardware.Camera.PictureCallback, boolean)} を呼び出すだけです。
	 * 
	 * @param callback コールバック
	 * @see {@link android.hardware.Camera#takePicture(android.hardware.Camera.ShutterCallback, android.hardware.Camera.PictureCallback, android.hardware.Camera.PictureCallback)}
	 * @see {@link android.hardware.Camera#takePicture(android.hardware.Camera.ShutterCallback, android.hardware.Camera.PictureCallback, android.hardware.Camera.PictureCallback, android.hardware.Camera.PictureCallback)}
	 * @see {@link android.hardware.Camera#autoFocus(android.hardware.Camera.AutoFocusCallback)}
	 */
	void takePicture(Camera.PictureCallback callback);

	/**
	 * キャプチャを開始します。
	 * 
	 * @param callback コールバック
	 * @param autoFocus 可能であればオートフォーカスを処理するかどうか
	 * @see {@link android.hardware.Camera#takePicture(android.hardware.Camera.ShutterCallback, android.hardware.Camera.PictureCallback, android.hardware.Camera.PictureCallback)}
	 * @see {@link android.hardware.Camera#takePicture(android.hardware.Camera.ShutterCallback, android.hardware.Camera.PictureCallback, android.hardware.Camera.PictureCallback, android.hardware.Camera.PictureCallback)}
	 * @see {@link android.hardware.Camera#autoFocus(android.hardware.Camera.AutoFocusCallback)}
	 */
	void takePicture(Camera.PictureCallback callback, boolean autoFocus);

	/**
	 * オートフォーカス処理中であれば中止します。 
	 * 
	 * @see {@link #takePicture(android.hardware.Camera.PictureCallback)}
	 * @see {@link android.hardware.Camera#cancelAutoFocus()}
	 */
	void cancelAutoFocus();

	/**
	 * 可能であればシャッター音の有効/無効を設定します。
	 * 
	 * @param enabled {@link takePicture} が呼ばれるときにカメラのシャッター音をさせるかどうか
	 * @return 設定できた場合は {@code true} を、それ以外の場合は {@code false}
	 * @see {@link android.hardware.Camera#enableShutterSound(boolean)}
	 */
	boolean enableShutterSound(boolean enabled);

	//////////////////////////////////////////////////////////////////////////
	// パラメータ操作

	/**
	 * サポートしているプレビューサイズをキーとしたアスペクト比が一致するサポートしているピクチャーサイズのマップを返します。<p>
	 * サポートしているプレビューサイズのキーはサポートしているピクチャーサイズとアスペクト比が一致しない物を含みません。
	 * 
	 * @return サポートしているプレビューサイズをキーとしたアスペクト比が一致するサポートしているピクチャーサイズのマップ
	 */
	LinkedHashMap<Camera.Size, Camera.Size> getSupportedPreviewSizeAndSupportedPictureSizeMap();

	/**
	 * サポートしているプレビューサイズのリストを返します。
	 * サポートしているプレビューサイズが不明な場合は <code>null</code> を返します。
	 * 
	 * @return サポートしているプレビューサイズのリスト。または <code>null</code>
	 * @see {@link android.hardware.Camera.Parameters#getSupportedPreviewSizes()}
	 */
	List<Camera.Size> getSupportedPreviewSizes();

	/**
	 * サポートしているピクチャーサイズのリストを返します。
	 * サポートしているピクチャーサイズが不明な場合は <code>null</code> を返します。
	 * 
	 * @return サポートしているピクチャーサイズのリスト。または <code>null</code>
	 * @see {@link android.hardware.Camera.Parameters#getPictureSize()}
	 */
	List<Camera.Size> getSupportedPictureSizes();

	/**
	 * 現在のカメラのプレビューサイズを返します。
	 * {@link Camera.Size} のサイズは横置きを前提とした幅と高さを返すので注意が必要です。
	 * 
	 * @return 現在のカメラのプレビューサイズ
	 */
	Camera.Size getPreviewSize();

	/**
	 * 現在のカメラのピクチャーサイズを返します。
	 * {@link Camera.Size} のサイズは横置きを前提とした幅と高さを返すので注意が必要です。
	 * 
	 * @return 現在のカメラのピクチャーサイズ
	 */
	Camera.Size getPictureSize();

	void setPictureFormat(int format);

	/**
	 * 現在設定されている干渉縞防止を返します。
	 * 干渉縞防止をサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @return 干渉縞防止。または <code>null</code>
	 * @see {@link android.hardware.Camera.Parameters#getAntibanding()}
	 */
	String getAntibanding();

	/**
	 * 現在設定されているカラーエフェクトを返します。
	 * カラーエフェクトをサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @return カラーエフェクト。または <code>null</code>
	 * @see {@link android.hardware.Camera.Parameters#getColorEffect()}
	 */
	String getColorEffect();

	/**
	 * 現在設定されているフラッシュモードを返します。
	 * フラッシュモードをサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @return フラッシュモード。または <code>null</code>
	 * @see {@link android.hardware.Camera.Parameters#getFlashMode()}
	 */
	String getFlashMode();

	/**
	 * 現在設定されているフォーカスモードを返します。
	 * フォーカスモードをサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @return フォーカスモード。または <code>null</code>
	 * @see {@link android.hardware.Camera.Parameters#getFocusMode()}
	 */
	String getFocusMode();

	/**
	 * 現在設定されているシーンを返します。
	 * シーンをサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @return シーン。または <code>null</code>
	 * @see {@link android.hardware.Camera.Parameters#getWhiteBalance()}
	 */
	String getSceneMode();

	/**
	 * 現在設定されているホワイトバランスを返します。
	 * ホワイトバランスをサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @return ホワイトバランス。または <code>null</code>
	 * @see {@link android.hardware.Camera.Parameters#getWhiteBalance()}
	 */
	String getWhiteBalance();

	/**
	 * サポートしている干渉縞防止のリストを返します。
	 * 干渉縞防止がをサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @return サポートしている干渉縞防止のリスト。または <code>null</code>
	 * @see {@link android.hardware.Camera.Parameters#getSupportedAntibanding()}
	 */
	List<String> getSupportedAntibanding();

	/**
	 * サポートしているカラーエフェクトのリストを返します。
	 * カラーエフェクトをサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @return サポートしているカラーエフェクトのリスト。または <code>null</code>
	 * @see {@link android.hardware.Camera.Parameters#getSupportedColorEffects()}
	 */
	List<String> getSupportedColorEffects();

	/**
	 * サポートしているフラッシュモードのリストを返します。
	 * フラッシュモードをサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @return サポートしているフラッシュモードのリスト。または <code>null</code>
	 * @see {@link android.hardware.Camera.Parameters#getSupportedFlashModes()}
	 */
	List<String> getSupportedFlashModes();

	/**
	 * サポートしているフォーカスモードのリストを返します。
	 * フォーカスモードをサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @return サポートしているフォーカスモードのリスト。または <code>null</code>
	 * @see {@link android.hardware.Camera.Parameters#getSupportedFocusModes()}
	 */
	List<String> getSupportedFocusModes();

	/**
	 * サポートしているシーンのリストを返します。
	 * シーンをサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @return サポートしているシーンのリスト。または <code>null</code>
	 * @see {@link android.hardware.Camera.Parameters#getSupportedSceneModes()}
	 */
	List<String> getSupportedSceneModes();

	/**
	 * サポートしているホワイトバランスのリストを返します。
	 * ホワイトバランスをサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @return サポートしているホワイトバランスのリスト。または <code>null</code>
	 * @see {@link android.hardware.Camera.Parameters#getSupportedWhiteBalance()}
	 */
	List<String> getSupportedWhiteBalance();

	/**
	 * 指定した干渉縞防止の列挙からサポートしている干渉縞防止をリストとして返します。
	 * 干渉縞防止をサポートしていない場合や指定した干渉縞防止を一つもサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @param values 干渉縞防止の列挙
	 * @return サポートしている干渉縞防止のリスト。または <code>null</code>
	 */
	List<String> getSupportedAntibanding(String... values);

	/**
	 * 指定したカラーエフェクトの列挙からサポートしているカラーエフェクトをリストとして返します。
	 * カラーエフェクトをサポートしていない場合や指定したカラーエフェクトを一つもサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @param values カラーエフェクトの列挙
	 * @return サポートしているカラーエフェクトのリスト。または <code>null</code>
	 */
	List<String> getSupportedColorEffects(String... values);

	/**
	 * 指定したフラッシュモードの列挙からサポートしているフラッシュモードをリストとして返します。
	 * フラッシュモードをサポートしていない場合や指定したフラッシュモードを一つもサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @param values フラッシュモードの列挙
	 * @return サポートしているフラッシュモードのリスト。または <code>null</code>
	 */
	List<String> getSupportedFlashModes(String... values);

	/**
	 * 指定したフォーカスモードの列挙からサポートしているフォーカスモードをリストとして返します。
	 * フォーカスモードをサポートしていない場合や指定したフォーカスモードを一つもサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @param values フォーカスモードの列挙
	 * @return サポートしているフォーカスモードのリスト。または <code>null</code>
	 */
	List<String> getSupportedFocusModes(String... values);

	/**
	 * 指定したシーンの列挙からサポートしているシーンをリストとして返します。
	 * シーンをサポートしていない場合や指定したシーンを一つもサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @param values シーンの列挙
	 * @return サポートしているシーンのリスト。または <code>null</code>
	 */
	List<String> getSupportedSceneModes(String... values);

	/**
	 * 指定したホワイトバランスの列挙からサポートしているホワイトバランスをリストとして返します。
	 * ホワイトバランスをサポートしていない場合や指定したホワイトバランスを一つもサポートしていない場合は <code>null</code> を返します。
	 * 
	 * @param values ホワイトバランスの列挙
	 * @return サポートしているホワイトバランスのリスト。または <code>null</code>
	 */
	List<String> getSupportedWhiteBalance(String... values);

	/**
	 * 可能であれば干渉縞防止を設定します。
	 * 
	 * @param antibanding 干渉縞防止
	 * @see {@link android.hardware.Camera.Parameters#setAntibanding(String)}
	 */
	void setAntibanding(String antibanding);

	/**
	 * 可能であればカラーエフェクトを設定します。
	 * 
	 * @param value カラーエフェクト
	 * @see {@link android.hardware.Camera.Parameters#setColorEffect(String)}
	 */
	void setColorEffect(String value);

	/**
	 * 可能であればフラッシュモードを設定します。
	 * 
	 * @param value フラッシュモード
	 * @see {@link android.hardware.Camera.Parameters#setFlashMode(String)}
	 */
	void setFlashMode(String value);

	/**
	 * 可能であればフォーカスモードを設定します。
	 * 
	 * @param value フォーカスモード
	 * @see {@link android.hardware.Camera.Parameters#setFocusMode(String)}
	 */
	void setFocusMode(String value);

	/**
	 * 可能であればシーンを設定します。
	 * 
	 * @param value シーン
	 * @see {@link android.hardware.Camera.Parameters#setSceneMode(String)}
	 */
	void setSceneMode(String value);

	/**
	 * 可能であればホワイトバランスを設定します。
	 * 
	 * @param value ホワイトバランス
	 * @see {@link android.hardware.Camera.Parameters#setWhiteBalance(String)}
	 */
	void setWhiteBalance(String value);

	/**
	 * 可能であれば次の干渉縞防止設定へ切り替えます。
	 * 
	 * @return 設定された干渉縞防止設定
	 */
	String switchAntibanding();

	/**
	 * 可能であれば次のカラーエフェクト設定へ切り替えます。
	 * 
	 * @return 設定されたカラーエフェクト設定
	 */
	String switchColorEffect();

	/**
	 * 可能であれば次のフラッシュモード設定へ切り替えます。
	 * 
	 * @return 設定されたフラッシュモード設定
	 */
	String switchFlashMode();

	/**
	 * 可能であれば次のフォーカスモード設定へ切り替えます。
	 * 
	 * @return 設定されたフォーカスモード設定
	 */
	String switchFocusMode();

	/**
	 * 可能であれば次のシーン設定へ切り替えます。
	 * 
	 * @return 設定されたシーン設定
	 */
	String switchSceneMode();

	/**
	 * 可能であれば次のホワイトバランス設定へ切り替えます。
	 * 
	 * @return 設定されたホワイトバランス設定
	 */
	String switchWhiteBalance();

	/**
	 * 指定した干渉縞防止設定の列挙から可能であれば次の干渉縞防止設定設定へ切り替えます。
	 * 
	 * @param values 干渉縞防止設定の列挙
	 * @return 設定された干渉縞防止設定設定
	 */
	String switchAntibanding(String... values);

	/**
	 * 指定したカラーエフェクトの列挙から可能であれば次のカラーエフェクト設定へ切り替えます。
	 * 
	 * @param values カラーエフェクトの列挙
	 * @return 設定されたカラーエフェクト設定
	 */
	String switchColorEffect(String... values);

	/**
	 * 指定したフラッシュモードの列挙から可能であれば次のフラッシュモード設定へ切り替えます。
	 * 
	 * @param values フラッシュモードの列挙
	 * @return 設定されたフラッシュモード設定
	 */
	String switchFlashMode(String... values);

	/**
	 * 指定したフォーカスモードの列挙から可能であれば次のフォーカスモード設定へ切り替えます。
	 * 
	 * @param values フォーカスモードの列挙
	 * @return 設定されたフォーカスモード設定
	 */
	String switchFocusMode(String... values);

	/**
	 * 指定したシーンの列挙から可能であれば次のシーン設定へ切り替えます。
	 * 
	 * @param values シーンの列挙
	 * @return 設定されたシーン設定
	 */
	String switchSceneMode(String... values);

	/**
	 * 指定したホワイトバランスの列挙から可能であれば次のホワイトバランス設定へ切り替えます。
	 * 
	 * @param values ホワイトバランスの列挙
	 * @return 設定されたホワイトバランス設定
	 */
	String switchWhiteBalance(String... values);

	//////////////////////////////////////////////////////////////////////////
	// 露出補正

	/**
	 * {@link Camera} 経由での露出補正がサポートされているかどうかを返します。<p>
	 * このメソッドは利便性のために提供しています。
	 * 実装は単に {@link #getMinExposureCompensation()} と {@link #getMaxExposureCompensation()} を呼び出して戻り値が両方とも <code>0</code> でない場合に <code>true</code> を返します。
	 * 
	 * @return 露出補正がサポートされている場合は <code>true</code> それ以外の場合は <code>false</code>
	 */
	public boolean isExposureCompensationSupported();

	/**
	 * 露出補正の EV 値の上限を返します。
	 * 
	 * @return 露出補正の EV 値の上限
	 * @see {@link android.hardware.Camera.Parameters#getMaxExposureCompensation()}
	 */
	public int getMaxExposureCompensation();

	/**
	 * 露出補正の EV 値の下限を返します。
	 * 
	 * @return 露出補正の EV 値の下限
	 * @see {@link android.hardware.Camera.Parameters#getMinExposureCompensation()}
	 */
	public int getMinExposureCompensation();

	/**
	 * 露出補正の EV 値の刻み値を返します。
	 * 
	 * @return 露出補正の EV 値の刻み値
	 * @see {@link android.hardware.Camera.Parameters#getExposureCompensationStep()}
	 */
	public float getExposureCompensationStep();

	/**
	 * 露出補正の EV 値を返します。
	 * 
	 * @return 露出補正の EV 値
	 * @see {@link android.hardware.Camera.Parameters#getExposureCompensation()}
	 */
	public int getExposureCompensation();

	/**
	 * 露出補正の EV 値を設定します。<p>
	 * 設定可能な値の範囲については、{@link #getMinExposureCompensation()}、{@link #getMaxExposureCompensation()} を参照してください。
	 * 設定可能な値については、{@link #getExposureCompensation()}、{@link #getExposureCompensationStep()} を参照してください。<p>
	 * カメラに露出補正機能が搭載されている場合でも、{@link Camera} API 経由での露出補正制御をサポートしていない端末があるので、
	 * {@link #getMinExposureCompensation()}、{@link #getMaxExposureCompensation()} を使用して設定可能な値の範囲を確認して下さい。
	 * 
	 * @see {@link android.hardware.Camera.Parameters#setExposureCompensation(int)}
	 */
	public void setExposureCompensation(int value);

	//////////////////////////////////////////////////////////////////////////
	// ズーム

	/**
	 * Callback interface for zoom changes during a smooth zoom operation.
	 * 
	 * @see {@link Camera.OnZoomChangeListener}
	 */
	public interface OnZoomChangeListener {
		/**
		 * Called when the zoom value has changed during a smooth zoom.
		 * 
		 * @param zoomValue the current zoom value. In smooth zoom mode, camera calls this for every new zoom value.
		 * @param stopped whether smooth zoom is stopped. If the value is true, this is the last zoom update for the application.
		 * @param camera the {@link CameraHelper} object
		 */
		void onZoomChange(int zoomValue, boolean stopped, CameraHelper camera);
	}

	/**
	 * ズーム機能をサポートしているかどうかを返します。
	 * 
	 * @return ズーム機能をサポートしているかどうか
	 * @see {@link Camera.Parameters#isZoomSupported()}
	 */
	boolean isZoomSupported();

	/**
	 * ズームレベルの上限値を返します。
	 * 
	 * @return ズームレベルの上限値
	 * @see {@link Camera.Parameters#getMaxZoom()}
	 */
	int getMaxZoom();

	/**
	 * 各ズームレベルに対する倍率をリストで返します。
	 * 
	 * @return 各ズームレベルに対する倍率のリスト
	 * @see {@link Camera.Parameters#getZoomRatios()}
	 */
	List<Integer>getZoomRatios();

	/**
	 * 現在のズームレベルを返します。
	 * 
	 * @return 現在のズームレベル
	 * @see {@link Camera.Parameters#getZoom()}
	 */
	int getZoom();

	/**
	 * 指定されたズームレベルを設定します。
	 * 
	 * @param value ズームレベル
	 * @see {@link Camera.Parameters#setZoom(int)}
	 */
	void setZoom(int value);

	/**
	 * 指定されたパラメータを {@link Camera.OnZoomChangeListener} として設定します。
	 * 
	 * @param listener {@link OnZoomChangeListener}
	 * @see {@link Camera#setZoomChangeListener(android.hardware.Camera.OnZoomChangeListener)}
	 */
	void setZoomChangeListener(OnZoomChangeListener listener);

	/**
	 * 要求されたズームレベルへのスムーズズームを開始します。
	 * 
	 * @param value ズームレベル (0 から {@link #getMaxZoom()} の範囲の値)
	 * @see {@link Camera#startSmoothZoom(int)}
	 */
	void startSmoothZoom(int value);

	/**
	 * スムーズズームを中止します。
	 * 
	 * @see {@link Camera#stopSmoothZoom()}
	 */
	void stopSmoothZoom();

	//////////////////////////////////////////////////////////////////////////
	// Face detection (顔検出)

	/**
	 * Callback interface for face detected in the preview frame.
	 * 
	 * @see {@link Camera.FaceDetectionListener}
	 */
	public interface FaceDetectionListener {
		/**
		 * Notify the listener of the detected faces in the preview frame.
		 * 
		 * @param faces  The detected faces in a list
		 * @param camera The {@link CameraHelper} object
		 */
		void onFaceDetection (FaceCompat[] faces, CameraHelper camera);
	}

	/**
	 * Information about a face identified through camera face detection.
	 * 
	 * <p>When face detection is used with a camera, the {@link FaceDetectionListener} returns a
	 * list of face objects for use in focusing and metering.</p>
	 * 
	 * @see {@link FaceDetectionListener}
	 */
	public static class FaceCompat {
		/**
		 * Create an empty face.
		 */
		public FaceCompat() {}

		/**
		 * Bounds of the face. (-1000, -1000) represents the top-left of the
		 * camera field of view, and (1000, 1000) represents the bottom-right of
		 * the field of view. For example, suppose the size of the viewfinder UI
		 * is 800x480. The rect passed from the driver is (-1000, -1000, 0, 0).
		 * The corresponding viewfinder rect should be (0, 0, 400, 240). The
		 * width and height of the rect will not be 0 or negative. The
		 * coordinates can be smaller than -1000 or bigger than 1000. But at
		 * least one vertex will be within (-1000, -1000) and (1000, 1000).
		 * 
		 * <p>The direction is relative to the sensor orientation, that is, what
		 * the sensor sees. The direction is not affected by the rotation or
		 * mirroring of {@link #setDisplayOrientation(int)}.</p>
		 * 
		 * @see #startFaceDetection()
		 */
		public Rect rect;

		/**
		 * The confidence level for the detection of the face. The range is 1 to 100. 100 is the
		 * highest confidence.
		 * 
		 * @see #startFaceDetection()
		 */
		public int score;

		/**
		 * An unique id per face while the face is visible to the tracker. If
		 * the face leaves the field-of-view and comes back, it will get a new
		 * id. This is an optional field, may not be supported on all devices.
		 * If not supported, id will always be set to -1. The optional fields
		 * are supported as a set. Either they are all valid, or none of them
		 * are.
		 */
		public int id = -1;

		/**
		 * The coordinates of the center of the left eye. The coordinates are in
		 * the same space as the ones for {@link #rect}. This is an optional
		 * field, may not be supported on all devices. If not supported, the
		 * value will always be set to null. The optional fields are supported
		 * as a set. Either they are all valid, or none of them are.
		 */
		public Point leftEye = null;

		/**
		 * The coordinates of the center of the right eye. The coordinates are
		 * in the same space as the ones for {@link #rect}.This is an optional
		 * field, may not be supported on all devices. If not supported, the
		 * value will always be set to null. The optional fields are supported
		 * as a set. Either they are all valid, or none of them are.
		 */
		public Point rightEye = null;

		/**
		 * The coordinates of the center of the mouth.  The coordinates are in
		 * the same space as the ones for {@link #rect}. This is an optional
		 * field, may not be supported on all devices. If not supported, the
		 * value will always be set to null. The optional fields are supported
		 * as a set. Either they are all valid, or none of them are.
		 */
		public Point mouth = null;
	}

	//////////////////////////////////////////////////////////////////////////
	// エリア

	/**
	 * The Area class is used for choosing specific metering and focus areas for the camera to use when calculating auto-exposure, auto-white balance, and auto-focus.<p>
	 * 
	 * To find out how many simultaneous areas a given camera supports, use {@link 	android.hardware.Camera.Parameters#getMaxNumMeteringAreas()} and {@link android.hardware.Camera.Parameters#getMaxNumFocusAreas()}.
	 * If metering or focusing area selection is unsupported, these methods will return <code>0</code>.<p>
	 * 
	 * Each Area consists of a rectangle specifying its bounds, and a weight that determines its importance.
	 * The bounds are relative to the camera's current field of view. The coordinates are mapped so that <code>(-1000, -1000)</code> is always the top-left corner of the current field of view, and <code>(1000, 1000)</code> is always the bottom-right corner of the current field of view.
	 * Setting Areas with bounds outside that range is not allowed. Areas with zero or negative width or height are not allowed.<p>
	 * 
	 * The weight must range from <code>1</code> to <code>1000</code>, and represents a weight for every pixel in the area.
	 * This means that a large metering area with the same weight as a smaller area will have more effect in the metering result.
	 * Metering areas can overlap and the driver will add the weights in the overlap region.
	 * 
	 * @see {@link 	android.hardware.Camera.Parameters#setFocusAreas(java.util.List)}
	 * @see {@link 	android.hardware.Camera.Parameters#getFocusAreas()}
	 * @see {@link 	android.hardware.Camera.Parameters#getMaxNumFocusAreas()}
	 * @see {@link 	android.hardware.Camera.Parameters#setMeteringAreas(java.util.List)}
	 * @see {@link 	android.hardware.Camera.Parameters#getMeteringAreas()}
	 * @see {@link 	android.hardware.Camera.Parameters#getMaxNumMeteringAreas()}
	 */
	public static class AreaCompat {

		/**
		 * Bounds of the area.
		 * (-1000, -1000) represents the top-left of the camera field of view, and (1000, 1000) represents the bottom-right of the field of view.
		 * Setting bounds outside that range is not allowed.
		 * Bounds with zero or negative width or height are not allowed.
		 * 
		 * @see {@link 	android.hardware.Camera.Parameters#getFocusAreas()}
		 * @see {@link 	android.hardware.Camera.Parameters#getMeteringAreas()}
		 */
		public Rect rect;

		/**
		 * Weight of the area.
		 * The weight must range from 1 to 1000, and represents a weight for every pixel in the area.
		 * This means that a large metering area with the same weight as a smaller area will have more effect in the metering result.
		 * Metering areas can overlap and the driver will add the weights in the overlap region.
		 * 
		 * @see {@link 	android.hardware.Camera.Parameters#getFocusAreas()}
		 * @see {@link 	android.hardware.Camera.Parameters#getMeteringAreas()}
		 */
		public int weight;

		/**
		 * Create an area with specified rectangle and weight.
		 * 
		 * @param rect the bounds of the area.
		 * @param weight the weight of the area.
		 */
		public AreaCompat(final Rect rect, final int weight) {
			this.rect = rect;
			this.weight = weight;
		}

		/**
		 * Compares {@code obj} to this area.
		 * 
		 * @param obj the object to compare this area with.
		 * @return {@code true} if the rectangle and weight of {@code obj} is the same as those of this area. {@code false} otherwise.
		 */
		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof AreaCompat)) {
				return false;
			}
			AreaCompat a = (AreaCompat) obj;
			if (rect == null) {
				if (a.rect != null) return false;
			} else {
				if (!rect.equals(a.rect)) return false;
			}
			return weight == a.weight;
		}

	}

	/**
	 * サポートしているフォーカスエリアの数を返します。
	 * 
	 * @return サポートしているフォーカスエリアの数
	 * @see {@link Camera.Parameters#getMaxNumFocusAreas()}
	 */
	int getMaxNumFocusAreas();

	/**
	 * フォーカスエリアのリストを返します。
	 * フォーカスエリアをサポートしていない場合は {@code null} を返します。
	 * 
	 * @return フォーカスエリアのリスト
	 * @see {@link Camera.Parameters#getFocusAreas()}
	 */
	List<AreaCompat>getFocusAreas();

	/**
	 * フォーカスエリアを設定します。
	 * 
	 * @param focusAreas フォーカスエリアの列挙
	 * @see {@link Camera.Parameters#setFocusAreas(List)}
	 */
	void setFocusAreas(AreaCompat... focusAreas);

	/**
	 * フォーカスエリアを設定します。
	 * 
	 * @param focusAreas フォーカスエリアのリスト
	 * @see {@link Camera.Parameters#setFocusAreas(List)}
	 */
	void setFocusAreas(List<AreaCompat> focusAreas);

	/**
	 * サポートしているホワイトバランス計算エリアの数を返します。
	 * 
	 * @return サポートしているホワイトバランス計算エリアの数
	 * @see {@link Camera.Parameters#getMaxNumMeteringAreas()}
	 */
	int getMaxNumMeteringAreas();

	/**
	 * ホワイトバランス計算エリアのリストを返します。
	 * ホワイトバランス計算エリアをサポートしていない場合は {@code null} を返します。
	 * 
	 * @return ホワイトバランス計算エリアのリスト
	 * @see {@link Camera.Parameters#getMeteringAreas()}
	 */
	List<AreaCompat>getMeteringAreas();

	/**
	 * ホワイトバランス計算エリアを設定します。
	 * 
	 * @param meteringAreas ホワイトバランス計算エリアの列挙
	 * @see {@link Camera.Parameters#setMeteringAreas(List)}
	 */
	void setMeteringAreas(AreaCompat... meteringAreas);

	/**
	 * ホワイトバランス計算エリアを設定します。
	 * 
	 * @param meteringAreas ホワイトバランス計算エリアのリスト
	 * @see {@link Camera.Parameters#setMeteringAreas(List)}
	 */
	void setMeteringAreas(List<AreaCompat> meteringAreas);

	//////////////////////////////////////////////////////////////////////////

	boolean isAutoWhiteBalanceLockSupported();
	void setAutoWhiteBalanceLock(boolean toggle);
	boolean getAutoWhiteBalanceLock();

	boolean isAutoExposureLockSupported();
	void setAutoExposureLock(boolean toggle);
	boolean getAutoExposureLock();

	boolean isVideoSnapshotSupported();

	//////////////////////////////////////////////////////////////////////////
	// Video Stabilization

	boolean isVideoStabilizationSupported();
	void setVideoStabilization(boolean toggle);
	boolean getVideoStabilization();

}