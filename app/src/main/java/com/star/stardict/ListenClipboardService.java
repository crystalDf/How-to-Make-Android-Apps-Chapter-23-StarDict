package com.star.stardict;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

public final class ListenClipboardService extends Service {

    private ClipboardManager mClipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    CharSequence content = mClipboardManager.getPrimaryClip()
                            .getItemAt(0).getText();

                    if (TextUtils.isEmpty(content)) {
                        return;
                    }

                    Intent intent = new Intent(ListenClipboardService.this, StarDictActivity.class);
                    intent.putExtra(getString(R.string.content), content);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                }
            };

    public static void start(Context context) {
        Intent serviceIntent = new Intent(context, ListenClipboardService.class);
        context.startService(serviceIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mClipboardManager = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);

        mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mClipboardManager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}