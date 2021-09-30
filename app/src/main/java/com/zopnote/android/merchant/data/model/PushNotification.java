package com.zopnote.android.merchant.data.model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.gson.JsonArray;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.agreement.AgreementActivity;
import com.zopnote.android.merchant.analytics.Analytics;
import com.zopnote.android.merchant.analytics.Event;
import com.zopnote.android.merchant.analytics.Param;
import com.zopnote.android.merchant.notifications.Command;
import com.zopnote.android.merchant.notifications.CommandFactory;
import com.zopnote.android.merchant.notifications.NotificationContentHiddenActivity;
import com.zopnote.android.merchant.notifications.NotificationClearedActivity;
import com.zopnote.android.merchant.notifications.inbox.InboxActivity;
import com.zopnote.android.merchant.util.Extras;
import com.zopnote.android.merchant.util.Prefs;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PushNotification {

	private static final boolean DEBUG = false;
	private static final String LOG_TAG = "PushNotification";

	public static final String ATTR_TYPE = "type";
	public static final String ATTR_DATA = "data";
	private static final String ATTR_CAMPAIGN = "campaign";
	private static final String ATTR_TITLE = "title";
	private static final String ATTR_TEXT = "text";
	private static final String ATTR_BIGTEXT = "bigText";
	private static final String ATTR_BIGPICTURE = "bigPicture";
	private static final String ATTR_BODY = "content";
	private static final String ATTR_LARGE_ICON = "largeIcon";
	private static final String ATTR_ACTION = "action";
	private static final String ATTR_ACTION_LABEL = "actionLabel";
	private static final String ATTR_NOTI_DATA = "merchantNotiData"; //new notification field

	private static final String ATTR_CONTAINS_APPS = "containsApps"; // targeting condition
	private static final String ATTR_NOT_CONTAINS_APPS = "notContainsApps"; // targeting condition

	public static final String TYPE_ANNOUNCEMENT = "Announcement";
	private static final String TYPE_APP_UPDATE_AVAILABLE = "AppUpdateAvailable";

	private static final String COMMA_SEPARATOR_REGEX = " *, *";

	private final String mType;
	private final String mCampaign;
	private final String mTitle;
	private final String mText;
	private final String mBody;
	private final String mBigtext;
	private final String mBigpicture;
	private final String mPraBody;
	private final String mLargeIcon;
	private final String mAction;
	private final String mActionLabel;
	private final String mContainsApps;
	private final String mNotContainsApps;
	private final String mNotData;

	private Context mContext;
	private Boolean mConditionMatch;
	private CountDownLatch mDoneSignal;
	private Bitmap mLargeIconBitmap;
	private Bitmap mBigPictureBitmap;

	public PushNotification(Context context, Bundle data) {
		mContext = context;

		System.out.println("notification data" + data.toString());
		mType = data.getString(ATTR_TYPE);
		mCampaign = data.getString(ATTR_CAMPAIGN);
		mTitle = data.getString(ATTR_TITLE);
		mPraBody = data.getString("body");
		mNotData = data.getString(ATTR_NOTI_DATA);
		mText = data.getString(ATTR_TEXT);
		mBigtext = data.getString(ATTR_BIGTEXT);
		mBigpicture = data.getString(ATTR_BIGPICTURE);
		mBody = data.getString(ATTR_BODY);
		mLargeIcon = data.getString(ATTR_LARGE_ICON);
		mAction = data.getString(ATTR_ACTION);
		mActionLabel = data.getString(ATTR_ACTION_LABEL);
		// TODO notification action ?
		mContainsApps = data.getString(ATTR_CONTAINS_APPS);
		mNotContainsApps = data.getString(ATTR_NOT_CONTAINS_APPS);
	}

	public void save() {

		if (TYPE_APP_UPDATE_AVAILABLE.equals(mType)) {

			// minimum requirement
			if (mBody == null) {
				return;
			}

			// verify json
			try {
				new AppUpdateInfo(mBody, mContext);
			} catch (Exception e) {
				// failed
				return;
			}

			// save for re-use on app launch
			Prefs.putString(AppConstants.PREFS_APP_UPDATE_CONTENT, mBody);
			Prefs.putString(AppConstants.PREFS_APP_UPDATE_CAMPAIGN, mCampaign);

			if (DEBUG) Log.d(LOG_TAG, "Saved app update notification");
		}
	}

	public void process() {

		if (TYPE_ANNOUNCEMENT.equals(mType) || TYPE_APP_UPDATE_AVAILABLE.equals(mType)) {

			// minimum requirement
			if (mTitle == null) {
				return;
			}

			// check targeting condition
			if (TYPE_ANNOUNCEMENT.equals(mType)  && ! isConditionMatch()) {
				return;
			}

			int latchCount = 0;
			if (mLargeIcon != null) {
				latchCount++;
			}
			if (mBigpicture != null) {
				latchCount++;
			}
			if (latchCount > 0) {
				// start download
				mDoneSignal = new CountDownLatch(latchCount);

				final ImageLoader imageLoader = VolleyManager.getInstance(mContext).getImageLoader();
				imageLoader.setBatchedResponseDelay(0);

				if (mLargeIcon != null) {
					// download large icon
					new Handler(mContext.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							imageLoader.get(mLargeIcon, new ImageLoader.ImageListener() {
								@Override
								public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
									mLargeIconBitmap = response.getBitmap();
									if (mLargeIconBitmap != null) {
										mDoneSignal.countDown();
										if (DEBUG) Log.d(LOG_TAG, "Large icon downloaded");
									} else {
										if (DEBUG) Log.d(LOG_TAG, "Large icon null");
									}
								}

								@Override
								public void onErrorResponse(VolleyError error) {
									mDoneSignal.countDown();
									if (DEBUG) Log.d(LOG_TAG, "Large icon download error: " + error.toString());
								}
							});
						}
					});
				}
				if (mBigpicture != null) {
					// download bigpicture
					new Handler(mContext.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							imageLoader.get(mBigpicture, new ImageLoader.ImageListener() {
								@Override
								public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
									mBigPictureBitmap = response.getBitmap();
									if (mBigPictureBitmap != null) {
										mDoneSignal.countDown();
										if (DEBUG) Log.d(LOG_TAG, "Bigpicture downloaded");
									} else {
										if (DEBUG) Log.d(LOG_TAG, "Bigpicture null");
									}
								}

								@Override
								public void onErrorResponse(VolleyError error) {
									mDoneSignal.countDown();
									if (DEBUG) Log.d(LOG_TAG, "Bigpicture download error: " + error.toString());
								}
							});
						}
					});
				}
			}

			if (mDoneSignal != null) {
				// wait for download
				try {
					if (DEBUG) Log.d(LOG_TAG, "Waiting for latch");
					boolean completed = mDoneSignal.await(30, TimeUnit.SECONDS);
					if (DEBUG) {
						if (completed) {
							Log.d(LOG_TAG, "Latch countdown complete");
						} else {
							Log.d(LOG_TAG, "Latch countdown timed out");
						}
					}
					// proceed
					postNotification();
				} catch (InterruptedException ignore) {
				}
			} else {
				// proceed
				postNotification();
			}

		} else {


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
			String NOTIFICATION_CHANNEL_ID = "my chanel";

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

				CharSequence name = "my channel";  //R.string.channel_name
				String description = "my channel description";//getString(R.string.channel_description);
				int importance = NotificationManager.IMPORTANCE_DEFAULT;
				NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
				channel.setDescription(description);
				// Register the channel with the system; you can't change the importance
				// or other notification behaviors after this
				notificationManager.createNotificationChannel(channel);
			}


			NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);

			//   Intent notificationIntent = new Intent(this.getApplicationContext(), SplashActivity.class);
			//   notificationIntent.putExtra("NOTIFICATION_TYPE", remoteMessage.getData().get("type"));
			//   PendingIntent contentIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			/*String tempAction = null;
			try {
				JSONObject message_data = new JSONObject(mNotData);
				JSONArray jsonArray = message_data.getJSONArray("merchants");
				JSONObject data = jsonArray.getJSONObject(0);
				tempAction = data.getString("action");
			} catch (JSONException e) {
				e.printStackTrace();
			}*/

			System.out.println("Notification........action" + mAction);
			Intent intent ;
			if (mAction.equalsIgnoreCase("")) {
				intent = new Intent(mContext, InboxActivity.class);
				intent.putExtra(Extras.NOTIFICATION_TYPE, mType);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

			}else if (mAction.equalsIgnoreCase("agreement")){
				 intent = new Intent(mContext, AgreementActivity.class);
				 intent.putExtra(Extras.TITLE, "Agreement");
				 intent.putExtra(Extras.URL, AppConstants.AGREEMENT_URL);
			}else {
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mAction));
			}
			PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

			notificationBuilder.setAutoCancel(true)
					.setContentIntent(pendingIntent)
					.setPriority(NotificationCompat.PRIORITY_DEFAULT)
					//    .setDefaults(Notification.DEFAULT_ALL)
					     .setWhen(System.currentTimeMillis())
					.setSmallIcon(R.drawable.ic_notification_bell)
					//      .setTicker(title)
					.setAutoCancel(true)
			      .setContentTitle(mTitle)
			     //  .setSound(NotificationCompat.DEFAULT_SOUND)
			      .setContentText(mPraBody);

			notificationManager.notify(UUID.randomUUID().toString(),1, notificationBuilder.build());


			int pendingNotificationsCount = Prefs.getInt(AppConstants.PREFS_NOTIFICATION_INIT_COUNT,0) + 1;
			Prefs.putInt(AppConstants.PREFS_NOTIFICATION_INIT_COUNT,pendingNotificationsCount);
			notificationBuilder.setNumber(pendingNotificationsCount) ;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


			// check if data push

			Bundle bundle = new Bundle();
			bundle.putString(ATTR_TYPE, mType);
			bundle.putString(ATTR_DATA, mBody);

			Command command = CommandFactory.create(mContext, bundle);
			if (DEBUG) Log.d(LOG_TAG, "command: " + command.getClass().getSimpleName());

			if (command != null) {
				command.process();
			}
		}
	}

	private void postNotification() {
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext);

		notificationBuilder.setSmallIcon(R.drawable.ic_stat_zopnote);

		notificationBuilder.setLargeIcon(mLargeIconBitmap);

		notificationBuilder.setContentTitle(mTitle);

		notificationBuilder.setContentText(mText);

		notificationBuilder.setTicker(mText);

		notificationBuilder.setAutoCancel(true);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			notificationBuilder.setColor(mContext.getResources().getColor(R.color.primary));
		}

		if (mBigtext != null) {
			final NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
			bigTextStyle.setBigContentTitle(mTitle);
			bigTextStyle.bigText(mBigtext);
			notificationBuilder.setStyle(bigTextStyle);
		}

		if (mBigPictureBitmap != null) {
			NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
			bigPictureStyle.setBigContentTitle(mTitle);
			bigPictureStyle.setSummaryText(mText);
			bigPictureStyle.bigPicture(mBigPictureBitmap);
			notificationBuilder.setStyle(bigPictureStyle);
		}

		// content intent
		Intent contentIntent = new Intent(mContext, NotificationContentHiddenActivity.class);
		contentIntent.putExtra(Extras.TITLE, mTitle);
		if (TYPE_ANNOUNCEMENT.equals(mType)) {
			// content is applicable only for announcement
			contentIntent.putExtra(Extras.CONTENT, mBody);
		}
		contentIntent.putExtra(Extras.CAMPAIGN, mCampaign);
		contentIntent.putExtra(Extras.CTA, mActionLabel);
		contentIntent.putExtra(Extras.CTA_LINK, mAction);
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notificationBuilder.setContentIntent(pendingIntent);

		// delete intent
		Intent deleteIntent = new Intent(mContext, NotificationClearedActivity.class);
		deleteIntent.putExtra(Extras.CAMPAIGN, mCampaign);
		notificationBuilder.setDeleteIntent(PendingIntent.getActivity(mContext, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT));

		// notify
		NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(UUID.randomUUID().toString(), 0, notificationBuilder.build()); // random tag to prevent overwrite of previous notification

		if (DEBUG) Log.d(LOG_TAG, "Posted notification");
		/*new Analytics.Builder()
				.setEventName(Event.NOTIFICATION_RECEIVED_X)
				.addParam(Param.CAMPAIGN, mCampaign)
				.logEvent();*/
	}

	private boolean isConditionMatch() {
		// one time init
		if (mConditionMatch == null) {
			// check contains condition
			if (mContainsApps != null) {
				String[] apps = mContainsApps.split(COMMA_SEPARATOR_REGEX);
				for (String app : apps) {
					try {
						mContext.getPackageManager().getPackageInfo(app, 0);
						// found
					} catch (PackageManager.NameNotFoundException e) {
						// not found
						if (DEBUG) Log.d(LOG_TAG, "Failed contains check: " + app);
						mConditionMatch = false;
						break;
					}
				}
			}
			// still not set?
			if (mConditionMatch == null) {
				// check NOT contains condition
				if (mNotContainsApps != null) {
					String[] apps = mNotContainsApps.split(COMMA_SEPARATOR_REGEX);
					for (String app : apps) {
						try {
							mContext.getPackageManager().getPackageInfo(app, 0);
							// found
							if (DEBUG) Log.d(LOG_TAG, "Failed not contains check: " + app);
							mConditionMatch = false;
							break;
						} catch (PackageManager.NameNotFoundException e) {
							// not found
							// OK
						}
					}
				}
			}
			// still not set?
			if (mConditionMatch == null) {
				// conditions, if any, met
				mConditionMatch = true;
			}
		}

		return mConditionMatch;
	}
}
