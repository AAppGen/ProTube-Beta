package io.awesome.gagtube.util;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import io.awesome.gagtube.R;
import io.awesome.gagtube.util.dialog.DialogUtils;

public class SharedUtils {
	
	public static void shareUrl(Context context) {

		String sharedText = "\uD83C\uDF1F Install now \uD83C\uDF1F\n\n" + "https://play.google.com/store/apps/details?id=" + context.getPackageName();

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
		intent.putExtra(Intent.EXTRA_TEXT, sharedText);
		context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_dialog_title)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}
	
	public static void rateApp(Context context) {
		DialogUtils.showEnjoyAppDialog(context,
									   // positive
									   (dialog, which) -> {
										   // dismiss dialog
										   dialog.dismiss();
										   // show dialog ask to rate
										   DialogUtils.showAskRatingAppDialog(context,
																			  // positive
																			  (dialog1, which1) -> {
																				  // open play store
																				  NavigationHelper.openGooglePlayStore(context, context.getPackageName());
																				  // dismiss dialog
																				  dialog1.dismiss();
																			  },
																			  // negative
																			  (dialog1, which1) -> {
																				  // dismiss dialog
																				  dialog1.dismiss();
																			  });
									   },
									   // negative
									   (dialog, which) -> {
										   // dismiss dialog
										   dialog.dismiss();
										   // show dialog feedback
										   DialogUtils.showFeedBackDialog(context,
																		  // positive
																		  (dialog2, which2) -> {
																			  // open email app
																			  NavigationHelper.composeEmail(context, context.getString(R.string.app_name) + " Android Feedback");
																			  // dismiss dialog
																			  dialog2.dismiss();
																		  },
																		  // negative
																		  (dialog2, which2) -> {
																			  // dismiss dialog
																			  dialog2.dismiss();
																		  });
									   });
	}

	public static void copyToClipboard(@NonNull final Context context, final String text) {
		final ClipboardManager clipboardManager = ContextCompat.getSystemService(context, ClipboardManager.class);

		if (clipboardManager == null) {
			Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_LONG).show();
			return;
		}

		try {
			clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));
			if (Build.VERSION.SDK_INT < 33) {
				// Android 13 has its own "copied to clipboard" dialog
				Toast.makeText(context, R.string.msg_copied, Toast.LENGTH_SHORT).show();
			}
		} catch (final Exception e) {
			Toast.makeText(context, R.string.msg_failed_to_copy, Toast.LENGTH_SHORT).show();
		}
	}

	public static void openUrlInApp(@NonNull final Context context, final String url) {
		openIntentInApp(context, new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}

	public static void openIntentInApp(@NonNull final Context context,
									   @NonNull final Intent intent) {
		if (!tryOpenIntentInApp(context, intent)) {
			Toast.makeText(context, R.string.no_app_to_open_intent, Toast.LENGTH_LONG).show();
		}
	}

	public static boolean tryOpenIntentInApp(@NonNull final Context context,
											 @NonNull final Intent intent) {
		try {
			context.startActivity(intent);
		} catch (final ActivityNotFoundException e) {
			return false;
		}
		return true;
	}
}