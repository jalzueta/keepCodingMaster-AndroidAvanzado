

package com.fillingapps.twittnearby.utils.twitter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.fillingapps.twittnearby.R;

public class ConnectTwitterTask extends AsyncTask<Void, Void, Void> {
	private Activity context;
	private Uri uri;
    private OnConnectTwitterListener listener;
	
	public ConnectTwitterTask(Activity context) {
        this.context = context;
        if (context != null) {
            Intent i = context.getIntent();
            if (i != null) {
                this.uri = i.getData();
            }
        }
    }

    public ConnectTwitterTask(Activity context, Uri uri) {
        this.context = context;
        this.uri = uri;
    }
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... mode) {
        if (checkConnected()) {
            return null;
        }

        if (uri != null) {
            // MODE_RECONNECT
            Globals.getSharedTwitterHelper(context).handleOauthCallback(uri);
        } else {
            // MODE_CONNECT_FIRST_TIME
            this.publishProgress((Void[])null);
            Globals.getSharedTwitterHelper(context).askOAuth(context);
        }

		return null;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		Toast.makeText(context, context.getString(R.string.authorize_this_app), Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

        if (listener != null && checkConnected()) {
            listener.twitterConnectionFinished();
        }
	}
	
	private boolean checkConnected() {
		if (Globals.getSharedTwitterHelper(this.context).isConnected()) {
            return true;
		}

        return false;
	}

    public OnConnectTwitterListener getListener() {
        return listener;
    }

    public void setListener(OnConnectTwitterListener listener) {
        this.listener = listener;
    }

    public interface OnConnectTwitterListener {
        public void twitterConnectionFinished();
    }
}
