package com.mypackage;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

public class UploadAudio extends FarmbookActivity {
	private String filePath = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = new Intent();
		intent.setType("audio/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent,"Select Audio"), 1);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {	
		if (resultCode == RESULT_OK) {
			Uri selectedAudioUri = data.getData();
			filePath = getPath(selectedAudioUri);
			Log.i("UploadAudio","Audio Path : " + filePath);

			Intent resultIntent = new Intent();
			resultIntent.putExtra("filePath", filePath);
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		}
		finish();
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}
}
