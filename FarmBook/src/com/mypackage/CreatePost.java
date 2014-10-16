package com.mypackage;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.utilitiespackage.Base64;
import com.utilitiespackage.CustomHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class CreatePost extends FarmbookActivity {
	private static final int PICK_IMAGE = 1, PICK_AUDIO = 2;

	private ImageButton image,audio,pause;
	private ImageButton send;
	private ImageView image_preview;
	private EditText post_edittext;
	private Bitmap bitmap;
	private MediaPlayer mp_main=null;

	private String image_path="",audio_path="",post_text="";
	private String postid,posttype,postphoneno;

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.create);
		
		addBarFunctions(CreatePost.this);
		
		mp_main = MediaPlayer.create(CreatePost.this, R.raw.upload);
		pause = (ImageButton)findViewById(R.id.bar_pause);
		playMedia(mp_main, pause, R.drawable.play_button, R.drawable.pause_button, true);
		
		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
			posttype=extras.getString("type");
			postid=extras.getString("postid");
			postphoneno=extras.getString("postphoneno");
		}

		image = (ImageButton)findViewById(R.id.create_image);
		audio = (ImageButton)findViewById(R.id.create_audio);
		send = (ImageButton)findViewById(R.id.create_send);
		image_preview = (ImageView)findViewById(R.id.create_image_preview);
		post_edittext = (EditText)findViewById(R.id.create_text);
		
		image.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(mp_main != null)
					mp_main.release();
				Intent intent = new Intent(CreatePost.this, UploadPhoto.class);
				startActivityForResult(intent,PICK_IMAGE);
			}
		});
		audio.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(mp_main != null)
					mp_main.release();
				Intent intent = new Intent(CreatePost.this, UploadAudio.class);
				startActivityForResult(intent,PICK_AUDIO);
			}
		});
		send.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				post_text=post_edittext.getText().toString();
				
				if(!post_text.equals("") || !image_path.equals("") || !audio_path.equals(""))
					sendToServer();
			}
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if(mp_main != null)
			mp_main.release();
	}

	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
		super.onActivityResult(requestCode, resultCode, data); 
		switch(requestCode) { 
			case (PICK_IMAGE):
				if (resultCode == Activity.RESULT_OK) { 
					image_path = data.getStringExtra("filePath");
					bitmap = BitmapFactory.decodeFile(image_path);
					
					image_preview.setImageBitmap(bitmap);
				} 
				break; 
			case (PICK_AUDIO):
				if (resultCode == Activity.RESULT_OK) { 
					audio_path = data.getStringExtra("filePath");
				} 
				break; 
		}
		changeMainAudio();
	}

	private void changeMainAudio() {
		
		if(mp_main != null)
			mp_main.release();
		mp_main = MediaPlayer.create(CreatePost.this, R.raw.upload2);
		playMedia(mp_main, pause, R.drawable.play_button, R.drawable.pause_button, true);
	}

	private void sendToServer() {
		boolean result = true;
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("phoneno",getStuffApplication().getPhonenumber()));
		postParameters.add(new BasicNameValuePair("image",getImageStr()));
		postParameters.add(new BasicNameValuePair("imagepath",image_path.substring(image_path.lastIndexOf('/')+1)));
		postParameters.add(new BasicNameValuePair("audiopath",audio_path.substring(audio_path.lastIndexOf('/')+1)));
		postParameters.add(new BasicNameValuePair("text",post_text));
		postParameters.add(new BasicNameValuePair("type",posttype));
		postParameters.add(new BasicNameValuePair("postid",postid));
		postParameters.add(new BasicNameValuePair("postphoneno",postphoneno));
		
		progressDialog = ProgressDialog.show(CreatePost.this, "", "Uploading...");
		
		if(!audio_path.equals("")) {
			Log.i("CreatePost","Audio present");
			try {
				if(!sendAudio())
					result = false;
			} catch(Exception e) {
				Toast.makeText(getApplicationContext(), "Audio upload failed!", Toast.LENGTH_LONG).show();
				finish();
			}
		} else
			Log.i("CreatePost","No Audio present");
		
		try {
			String response = CustomHttpClient.executeHttpPost("createpost.php", postParameters);
			Log.i("CreatePost","uploadimage.php response = "+response);
			
			if(!response.equals("1"))
				result = false;
		} catch (Exception e) {
			Log.e("CreatePost",e.toString());
			result = false;
		}
		
		progressDialog.dismiss();
		Toast.makeText(getApplicationContext(), (result ? "Post submitted successfully" : "Post submission failed"), Toast.LENGTH_LONG).show();
		finish();
	}

	private String getImageStr() {
		
		if(!image_path.equals("")) {
			Log.i("CreatePost","Image present");
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream); //compress to which format you want.
			byte[] byte_arr = stream.toByteArray();
			return Base64.encodeBytes(byte_arr);
		}
		Log.i("CreatePost","No Image present");
		return "";
	}

	private boolean sendAudio() {

		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		DataInputStream inStream = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary =  "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1*1024*1024;
		try {
			FileInputStream fileInputStream = new FileInputStream(new File(audio_path) );

			URL url = new URL("http://"+CustomHttpClient.thisIp+"/uploadaudio.php?phoneno="+getStuffApplication().getPhonenumber());

			conn = (HttpURLConnection)url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

			dos = new DataOutputStream( conn.getOutputStream() );
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + audio_path + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			Log.i("CreatePost","File "+audio_path+" written");
			fileInputStream.close();
			dos.flush();
			dos.close();
		}
		catch (MalformedURLException e) {
			Log.e("CreatePost", e.getMessage());
		}
		catch (IOException e) {
			Log.e("CreatePost", e.getMessage());
		}

		try {
			inStream = new DataInputStream ( conn.getInputStream() );
			String response = inStream.readLine();
			inStream.close();

			if(response.equals("1"))
				return true;
		}
		catch (IOException e) {
			Log.e("CreatePost", e.getMessage());
		}
		return false;
	}
}
