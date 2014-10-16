package com.mypackage;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.utilitiespackage.Base64;
import com.utilitiespackage.CustomHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Register extends FarmbookActivity {

	private EditText firstname,lastname,location;
	private Button select,ok;
	private AlertDialog messageDialog;
	private RadioGroup sexgroup;
	private String sex="F",filePath;
	private ImageView image_preview;
	private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		firstname=(EditText)findViewById(R.id.register_firstname);
		lastname=(EditText)findViewById(R.id.register_lastname);
		location=(EditText)findViewById(R.id.register_location);
		sexgroup=(RadioGroup)findViewById(R.id.register_sex);
		image_preview = (ImageView)findViewById(R.id.register_image_preview);

		select=(Button)findViewById(R.id.register_select);
		select.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Register.this, SelectProfilePic.class);
				startActivityForResult(intent,1);
			}
		});

		ok=(Button)findViewById(R.id.register_register);
		ok.setEnabled(false);
		ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(!firstname.getText().toString().equals("") && !lastname.getText().toString().equals("") && !location.getText().toString().equals(""))
					validate();
			}
		});
	}

	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
		super.onActivityResult(requestCode, resultCode, data); 
		if (resultCode == Activity.RESULT_OK) { 
			filePath = data.getStringExtra("filePath");
			Log.i("Register","File path = "+filePath);

			bitmap = BitmapFactory.decodeFile(filePath);
			image_preview.setImageBitmap(bitmap);
			ok.setEnabled(true);
		} 
	}

	public void validate() {

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

		postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));
		postParameters.add(new BasicNameValuePair("deviceid", getStuffApplication().getDeviceid()));
		postParameters.add(new BasicNameValuePair("firstname", firstname.getText().toString()));
		postParameters.add(new BasicNameValuePair("lastname", lastname.getText().toString()));
		postParameters.add(new BasicNameValuePair("location", location.getText().toString()));

		switch(sexgroup.getCheckedRadioButtonId()) {
		case R.id.register_sex_female:
			sex="F";
			break;
		case R.id.register_sex_male:
			sex="M";
			break;
		}
		postParameters.add(new BasicNameValuePair("sex", sex));
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
		//bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
		byte[] byte_arr = stream.toByteArray();
		String image_str = Base64.encodeBytes(byte_arr);
		
		postParameters.add(new BasicNameValuePair("image",image_str));
		
		try {
			String response = CustomHttpClient.executeHttpPost("register.php", postParameters);
			Log.i("Register","register.php response = "+response);

			if(response.equals("1")) {
				getStuffApplication().setFirstname(firstname.getText().toString());
				getStuffApplication().setLastname(lastname.getText().toString());
				getStuffApplication().setLocation(location.getText().toString());
				getStuffApplication().setSex(sex);
				
				messageDialog = new AlertDialog.Builder(this)
				.setTitle("Registration Status")
				.setMessage("User successfully registered!")
				.setPositiveButton("OK", new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent resultIntent = new Intent();
						setResult(Activity.RESULT_OK, resultIntent);
						finish();
					}
				})
				.create();
				messageDialog.show();

			} else {
				messageDialog = new AlertDialog.Builder(this)
				.setTitle("Registration Status")
				.setMessage("Registration failed!")
				.setPositiveButton("OK", new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						messageDialog.cancel();
					}
				})
				.create();
				messageDialog.show();
			}
		} catch (Exception e) {
			Log.e("Register",e.toString());
			Toast.makeText(getApplicationContext(), "Error connecting to server!", Toast.LENGTH_LONG).show();
			finish();
		}

	}
}
