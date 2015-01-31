package com.flask.templateapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.model.GlideUrl;
import com.google.gson.Gson;

import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uk.co.senab.bitmapcache.BitmapLruCache;

public class TemplateApplication extends Application {
	private static final int glideDiskCacheSizeInBytes = 128 * 1024 * 1024; // MB
	private static int width, height;
	private static float density;
	private static TemplateApplication instance;
	private static WindowManager windowManager;
	private static Gson gson = new Gson();

	private BitmapLruCache bitmapDiskCache;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		initGlide();
		initPersistBitmapCache();
		measureRealSize();
	}

	private void initGlide() {
		try {
			Glide.setup(new GlideBuilder(this)
							.setDiskCache(DiskLruCacheWrapper.get(Glide.getPhotoCacheDir(this), glideDiskCacheSizeInBytes))
							.setDecodeFormat(DecodeFormat.PREFER_RGB_565)
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initPersistBitmapCache() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			File cacheLocation = new File(Environment.getExternalStorageDirectory(), "bitmap_cache");

			BitmapLruCache.Builder builder = new BitmapLruCache.Builder(this);
			builder.setMemoryCacheEnabled(true).setMemoryCacheMaxSize(1);
			builder.setDiskCacheEnabled(true).setDiskCacheLocation(cacheLocation);
			bitmapDiskCache = builder.build();
		}
	}

	public static void measureRealSize() {
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		// since SDK_INT = 1;
		width = metrics.widthPixels;
		height = metrics.heightPixels;
		// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
			try {
				width = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
				height = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
			} catch (Exception e) {
				e.printStackTrace();
			}
		// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 17)
			try {
				Point realSize = new Point();
				Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
				width = realSize.x;
				height = realSize.y;
			} catch (Exception e) {
				e.printStackTrace();
			}
		density = metrics.density;
	}

	public static void hideKeyboard(Activity activity) {
		View view = activity.getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) instance.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public static String getFormattedString(int resId, Object... args) {
		return MessageFormat.format(instance.getString(resId), args);
	}

	public static WindowManager getWindowManager() {
		if (windowManager == null)
			windowManager = (WindowManager) getInstance().getSystemService(Context.WINDOW_SERVICE);
		return windowManager;
	}

	public static LayoutInflater getLayoutInflater() {
		return (LayoutInflater) instance.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public static TemplateApplication getInstance() {
		return instance;
	}

	public ExecutorService getNewExecutorService() {
		return Executors.newCachedThreadPool();
	}

	public static <T> T fromJson(String json, Class<? extends T> klass) {
		return gson.fromJson(json, klass);
	}

	public static String toJson(Object o) {
		return gson.toJson(o);
	}

	public static int getWidth() {
		return width;
	}

	public static int getHeight() {
		return height;
	}

	public static int dp2px(float dp) {
		return (int) (density * dp + .5f);
	}

	public static int getDimensionAsPx(int rid) {
		return (int) (getInstance().getResources().getDimension(rid) + .5f);
	}

	public static int getColor(int rid) {
		return getInstance().getResources().getColor(rid);
	}
}