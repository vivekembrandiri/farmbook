package com.mypackage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class TakePhoto extends FarmbookActivity {
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	//private String image_type="default";
	public static final int MEDIA_TYPE_IMAGE = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE)); 
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); 
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	private File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File( Environment.getExternalStorageDirectory(), "Farmbook" );

		if (! mediaStorageDir.exists()) {
			if (! mediaStorageDir.mkdirs()) {
				Log.e("TakePhoto", "Failed to create directory");
				return null;
			}
		}

		String timestamp=new SimpleDateFormat("dd_MM_yyyy__HH_mm_ss").format(new Date());

		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator+"IMG_"+timestamp+".jpg");
		} else {
			return null;
		}
		return mediaFile;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Log.i("TakePhoto","Photo captured and saved");

				Intent resultIntent = new Intent();
				resultIntent.putExtra("filePath", fileUri);
				setResult(Activity.RESULT_OK, resultIntent);

			} else if (resultCode == RESULT_CANCELED) {
				Log.i("TakePhoto","Cancelled");
			} else {
				Log.e("TakePhoto","Error");
			}
			finish();
		}
	}
}
