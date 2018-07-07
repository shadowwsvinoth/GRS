package com.app.grs.helper;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.grs.R;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(mailTo = "shadowwsvinothkumar@gmail.com")
public class GRS extends Application {

    public static IntentFilter filter;
    public static BroadcastReceiver networkStateReceiver;
    public static Dialog dialog;

    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ACRA.init(this);
    }

    public static void registerReceiver(final Context ctx){

        if (networkStateReceiver == null) {
            Log.v("network dialog", "network dialog");

            dialog = new Dialog(ctx);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.network_dialog);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            networkStateReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info = cm.getActiveNetworkInfo();
                    if (info != null
                            && (info.getState() == NetworkInfo.State.CONNECTED || info.getState() == NetworkInfo.State.CONNECTING)) {
                        Log.v("we are connected", "we are connected");
                    } else {
                        Log.v("Disconnected", "Disconnected");
                        try {
                            networkError(dialog, context);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            };

            ctx.registerReceiver(networkStateReceiver, filter);
        }
    }

    private static void networkError(final Dialog dia, final Context ctx){
        try {

            TextView ok = dia.findViewById(R.id.alert_button);
            TextView cancel = dia.findViewById(R.id.alert_cancel);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null){
                        dialog.cancel();
                    }
                    dialog = null;
                    Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                    ctx.startActivity(intent);
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null){
                        dialog.cancel();
                    }
                    dialog = null;
                }
            });
            Log.v("show", "show=" + dia.isShowing());
            dia.show();
        } catch (NullPointerException e){
            e.printStackTrace();
        } catch (WindowManager.BadTokenException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void unregisterReceiver(Context ctx){
        if (networkStateReceiver != null) {
            if (dialog != null){
                dialog.cancel();
            }
            dialog = null;
            ctx.unregisterReceiver(networkStateReceiver);
            networkStateReceiver = null;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    Log.i("Class", info[i].getState().toString());
                    if (info[i].getState() == NetworkInfo.State.CONNECTED || info[i].getState() == NetworkInfo.State.CONNECTING) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
