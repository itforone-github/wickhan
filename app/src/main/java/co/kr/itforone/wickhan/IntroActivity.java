package co.kr.itforone.wickhan;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class IntroActivity extends BaseActivity {
	final int SEC = 3000;//다음 화면에 넘어가기 전에 머물 수 있는 시간(초)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
			TedPermission.with(this)
					.setPermissions(
							android.Manifest.permission.READ_MEDIA_IMAGES,
							android.Manifest.permission.READ_MEDIA_VIDEO,
							android.Manifest.permission.READ_MEDIA_AUDIO,
							android.Manifest.permission.CAMERA)
					.setRationaleMessage("이 앱은 권한설정을 하셔야 사용하실 수 있습니다.")
					.setDeniedMessage("권한설정에 거부하시면 앱설정에서 직접하셔야 합니다.")
					.setPermissionListener(permissionListener)
					.check();
		}else{
			TedPermission.with(this)
					.setPermissions(
							android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
							android.Manifest.permission.READ_EXTERNAL_STORAGE,
							Manifest.permission.CAMERA)
					.setRationaleMessage("이 앱은 권한설정을 하셔야 사용하실 수 있습니다.")
					.setDeniedMessage("권한설정에 거부하시면 앱설정에서 직접하셔야 합니다.")
					.setPermissionListener(permissionListener)
					.check();

		}

		/*Intent miIntent = new Intent(this, MainActivity.class);
		startActivity(miIntent);
		finish();*/
	}
	PermissionListener permissionListener = new PermissionListener() {
		//퍼미션 설정을 하면
		@Override
		public void onPermissionGranted() {
			try{
				goHandler();

                /*LocationPosition.act= mActivity;
                LocationPosition.setPosition(mActivity);
                if(LocationPosition.lng==0.0){
                    LocationPosition.setPosition(mActivity);
                }*/
			}catch(Exception e){

			}

		}
		//퍼미션 설정을 하지 않으면
		@Override
		public void onPermissionDenied(List<String> deniedPermissions) {
			Toast.makeText(IntroActivity.this, "뭐지", Toast.LENGTH_SHORT).show();
		}
	};
	//핸들러로 이용해서 1초간 머물고 이동이 됨
	public void goHandler() {
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
			}
		}, SEC);
	}
}
