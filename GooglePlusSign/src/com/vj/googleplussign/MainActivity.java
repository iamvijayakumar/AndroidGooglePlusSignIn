package com.vj.googleplussign;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener {

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 0;

	/* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;

	/*
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	
	
	private boolean mIntentInProgress;
    TextView textView;
    ImageView profileImage ;
    com.google.android.gms.common.SignInButton signInBtn;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy); 
		
		setContentView(R.layout.activity_main);
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API, null)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
		
		textView = (TextView)findViewById(R.id.username);
		profileImage = (ImageView)findViewById(R.id.profileImage);
		signInBtn = (com.google.android.gms.common.SignInButton)findViewById(R.id.sign_in_button);
		signInBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mGoogleApiClient.connect();
				
			}
		});
	}

	protected void onStart() {
		super.onStart();
		
	}

	protected void onStop() {
		super.onStop();

		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {

		if (!mIntentInProgress && arg0.hasResolution()) {
			try {
				mIntentInProgress = true;
				arg0.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				// The intent was canceled before it was sent. Return to the
				// default
				// state and attempt to connect to get an updated
				// ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}

		// Log.e("error", "error code" + arg0.getResolution());
		Toast.makeText(this, "User is onConnectionFailed!", Toast.LENGTH_LONG)
				.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {

		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
		signInBtn.setVisibility(View.GONE);
		if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
			Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
		
			textView.setText("Welcome : "+person.getDisplayName() );
			try {
				JSONObject jsonObject = new JSONObject(person.getImage().toString());
				String imageUrl  = jsonObject.getString("url");
				
				try {
					URL url = new URL(imageUrl);
					Bitmap bmp;
					bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
					profileImage.setImageBitmap(bmp);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

	}

	@Override
	public void onConnectionSuspended(int arg0) {

		Toast.makeText(this, "User is onConnectionSuspended!",
				Toast.LENGTH_LONG).show();
	}

}