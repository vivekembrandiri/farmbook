package com.mypackage;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

public class UploadPhoto extends FarmbookActivity {
	private MediaPlayer mp_main=null;
	private String filePath=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mp_main = MediaPlayer.create(UploadPhoto.this, R.raw.select_image);
		mp_main.start();

		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if(mp_main != null)
			mp_main.release();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			Uri selectedImageUri = data.getData();

			// OI FILE Manager
			String filemanagerstring = selectedImageUri.getPath();

			// MEDIA GALLERY
			String selectedImagePath = getPath(selectedImageUri);

			if (selectedImagePath != null) {
				filePath = selectedImagePath;
				Log.i("UploadPhoto","File Path : "+filePath);
			} else if (filemanagerstring != null) {
				filePath = filemanagerstring;
				Log.i("UploadPhoto","File Path : "+filePath);
			} else {
				Log.e("UploadPhoto", "Unknown path");
			}

			if (filePath != null) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra("filePath", filePath);
				setResult(Activity.RESULT_OK, resultIntent);
				if(mp_main != null)
					mp_main.release();
				finish();				
			} else {
				Log.e("UploadPhoto","File Path error!");
			}
		}
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
