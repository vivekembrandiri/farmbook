package com.mypackage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SelectProfilePic extends FarmbookActivity {
	
	private static final int SELECT_CAMERA = 1,SELECT_IMAGE = 2;
	private ImageButton camera,image;
	private String filePath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectprofilepic);
		
		addBarFunctions(SelectProfilePic.this);
		
		camera = (ImageButton)findViewById(R.id.selectprofilepic_camera);
		image = (ImageButton)findViewById(R.id.selectprofilepic_image);
		
		camera.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SelectProfilePic.this, TakePhoto.class);
				startActivityForResult(intent, SELECT_CAMERA);
			}
		});
		image.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SelectProfilePic.this, UploadPhoto.class);
				startActivityForResult(intent,SELECT_IMAGE);
			}
		});
	}
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
		super.onActivityResult(requestCode, resultCode, data); 
		if (resultCode == Activity.RESULT_OK) {
			
			if(data != null) {
				filePath = data.getStringExtra("filePath");
				
				Intent resultIntent = new Intent();
				resultIntent.putExtra("filePath", filePath);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		} 
	}
}
