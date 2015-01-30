package com.flask.templateapp;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.readystatesoftware.systembartint.SystemBarTintManager;

public class EnhancedActionBarActivity extends ActionBarActivity {
	protected SystemBarTintManager tintManager;
	private Toolbar toolbar;
	private ColorDrawable colorDrawable;
	private Integer defaultPrimaryColor = null;
	private Integer defaultPrimaryDarkColor = null;

	protected void afterViews(Toolbar toolbar) {
		setToolbar(toolbar);
		setSupportActionBar(toolbar);

		tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setTintColor(getPrimaryDarkColor());

		colorDrawable = new ColorDrawable(getPrimaryColor());
		getSupportActionBar().setBackgroundDrawable(colorDrawable);
	}

	protected void setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
	}

	public int getPrimaryColor() {
		if (defaultPrimaryColor == null)
			return getColor(R.color.material_primary);
		return defaultPrimaryColor;
	}

	public int getPrimaryDarkColor() {
		if (defaultPrimaryDarkColor == null)
			return getColor(R.color.material_primary_dark);
		return defaultPrimaryDarkColor;
	}

	public void changeColor(int primary, int primaryDark) {
		defaultPrimaryColor = primary;
		defaultPrimaryDarkColor = primaryDark;

		if (tintManager != null)
			tintManager.setTintColor(primaryDark);
		if (toolbar != null) {
			toolbar.setBackgroundColor(primary);
			toolbar.invalidate();
		}
		if (colorDrawable != null) {
			colorDrawable.setColor(primary);
			colorDrawable.invalidateSelf();
		}
	}

	public void changeColorWithResId(int primaryResId, int primaryDarkResId) {
		changeColor(getColor(primaryResId), getColor(primaryDarkResId));
	}

	public void changeTitleColors(int titleTextColor, int subtitleTextColor) {
		toolbar.setTitleTextColor(titleTextColor);
		toolbar.setSubtitleTextColor(subtitleTextColor);
		toolbar.invalidate();
	}

	public void changeTitleColorsWithResId(int titleTextColorResId, int subtitleTextColorResId) {
		changeTitleColors(getColor(titleTextColorResId), getColor(subtitleTextColorResId));
	}

	public void setDefaultToolbarNavigationIcon() {
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void setDefaultToolbarNavigationIconDark() {
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public int getColor(int resId) {
		return getResources().getColor(resId);
	}

	public void setSubtitle(String subtitle) {
		toolbar.setSubtitle(subtitle);
	}

	@Override
	protected void onDestroy() {
		toolbar = null;
		tintManager = null;
		colorDrawable = null;
		super.onDestroy();
	}
}