package com.mypackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.utilitiespackage.CustomHttpClient;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

public class FarmbookActivity extends Activity {
	public static final String NONE = "none";
	public static final String POST = "post";
	public static final String COMMENT = "comment";
	
	public static final String ADMIN = "a";
	public static final String FARMER = "f";
	
	public static Animation animation;
	
	public FarmbookActivity() {
		super();
		
		animation = new AlphaAnimation(1, 0);
		animation.setDuration(500); 
	    animation.setInterpolator(new LinearInterpolator()); 
	    animation.setRepeatCount(Animation.INFINITE); 
	    animation.setRepeatMode(Animation.REVERSE); 
	}

	protected FarmbookApplication getStuffApplication() {
		return (FarmbookApplication)getApplication();
	}

	protected void addBarFunctions(final Context context) {
		
		ImageButton home = (ImageButton)findViewById(R.id.bar_home);
		ImageButton camera = (ImageButton)findViewById(R.id.bar_camera);
		ImageButton soundrecorder = (ImageButton)findViewById(R.id.bar_record);
		ImageButton close = (ImageButton)findViewById(R.id.bar_close);
		
		home.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, Home.class);
				startActivity(intent);
			}
		});
		soundrecorder.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
				startActivity(intent);
			}
		});
		camera.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, TakePhoto.class);
				startActivity(intent);
			}
		});
		close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				moveTaskToBack(true);
			}
		});
	}
	
	protected MediaPlayer playAudio(String mediaUrl) {
		try {
			URLConnection cn = new URL("http://"+CustomHttpClient.thisIp+"/audio/"+mediaUrl).openConnection();
			InputStream is = cn.getInputStream();

			// create file to store audio
			File mediaFile = new File(this.getCacheDir(),"mediafile");
			FileOutputStream fos = new FileOutputStream(mediaFile);   
			byte buf[] = new byte[16 * 1024];
			Log.i("playAudio", mediaUrl+" downloaded");

			// write to file until complete
			do {
				int numread = is.read(buf);   
				if (numread <= 0)  
					break;
				fos.write(buf, 0, numread);
			} while (true);
			fos.flush();
			fos.close();
			Log.i("playAudio", mediaUrl+" saved");

			MediaPlayer mp = new MediaPlayer();
			FileInputStream fis = new FileInputStream(mediaFile);

			mp.setDataSource(fis.getFD());
			return mp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected void playMedia(final MediaPlayer mp, final ImageButton pause, final int playimage, final int pauseimage, boolean playing) {
		
		if(playing) {
			pause.setImageResource(pauseimage);
			mp.start();
		} else {
			pause.setImageResource(playimage);
		}
		mp.setOnCompletionListener(new OnCompletionListener(){
			public void onCompletion(MediaPlayer arg0) {
				pause.setImageResource(playimage);
			}
		});
		pause.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mp.isPlaying()) {
					mp.pause();
					pause.setImageResource(playimage);
				}
				else {
					mp.start();
					pause.setImageResource(pauseimage);
				}
			}	
		});
	}
	
	protected Dialog getOkDialog(String title, String text, Context context) {
		
		Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.okdialog);
		
		dialog.setTitle(title);
		TextView dialog_tv = (TextView)dialog.findViewById(R.id.okdialog_tv);
		dialog_tv.setText(text);
		
		return dialog;
	}
	
	protected Dialog getOkCancelDialog(String title, String text, Context context) {
		
		Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.okcanceldialog);
		
		dialog.setTitle(title);
		TextView dialog_tv = (TextView)dialog.findViewById(R.id.okcanceldialog_tv);
		dialog_tv.setText(text);
		
		return dialog;
	}
}
