package com.smarttaxi.driver.helpers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public class LoaderHelper {

	public static ProgressDialog progressDialog;
	public static ProgressDialog mProgressDialog;

	public static void showLoader(Activity activity, String message,
			String title) {

		progressDialog = new ProgressDialog(activity);
		progressDialog.setMessage(message);

		if (!title.equals(""))
			progressDialog.setTitle(title);

		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	public static void showProgressLoaderForFragments(Context activity, String message,
			String title) {

		mProgressDialog = new ProgressDialog(activity);
		mProgressDialog.setMessage(message);

		if (!title.equals(""))
			mProgressDialog.setTitle(title);

		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}

	public static void hideLoader() {
		if (progressDialog != null)
			if (progressDialog.isShowing())
				progressDialog.dismiss();
	}
	
	public static void closeProgressLoader() {
		if (mProgressDialog != null)
			if (mProgressDialog.isShowing())
				mProgressDialog.dismiss();
	}

	public static boolean isLoaderShowing() {
		return progressDialog.isShowing();
	}
	
	public static boolean isFragmentLoaderShowing() {
		return mProgressDialog.isShowing();
	}
}
