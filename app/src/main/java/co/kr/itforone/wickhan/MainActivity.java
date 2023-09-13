package co.kr.itforone.wickhan;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import util.ForYouConfig;

public class MainActivity extends BaseActivity implements OnClickListener {
	private boolean end = false;
	private WebView mWebview = null;
	private ArrayList<BottomData> mData = new ArrayList<BottomData>();

	private ProgressDialog m_progressdialog = null;
	private ProgressBar mProgressBar = null;

	private ForYouConfig mConfig = null;
	public String appName = "";


	private String bo_table = "";
	private static final String TYPE_IMAGE = "image/*";
	private static final int INPUT_FILE_REQUEST_CODE = 1;

	final int FILECHOOSER_NORMAL_REQ_CODE = 1200,FILECHOOSER_LOLLIPOP_REQ_CODE=1300;
	ValueCallback<Uri> filePathCallbackNormal;
	ValueCallback<Uri[]> filePathCallbackLollipop;
	Uri mCapturedImageURI;



	public boolean isLoading = false;
	private final Handler handler = new Handler();
	private String IntentURL = "";
	int file_count = 0;

	final int REQ_CODE_SELECT_IMAGE = 100;
	final int MULTI_UPLOAD_IMAGE = 101;

	/****************************************************************************************************************/

	final public static String SEVER_URL = "http://www.wickhan.com";
	final public static String MAIN_URL = SEVER_URL + "";
	final public static String COME_URL = MAIN_URL + "s1.htm";

	final private static String TEL = "010-8945-5430";

	private String[] bottomText = new String[] { "홈으로","공유하기", "종료" };
	private int[] bottomImg = new int[] { R.drawable.home, R.drawable.btn_share,R.drawable.finish };
	private String KakaoText = "[W.I CKHAN] - 정보공유, 구구인구직, 부동산, 여행정보를 한곳에~";
	private boolean SettingGcm = false;

	/****************************************************************************************************************/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		WebView();
		Bottom();
		gcm();

		Intro();

		String url = getIntent().getStringExtra("url");
		if(url != null && url.length() > 0){
			IntentURL = url;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			CookieSyncManager.getInstance().startSync();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			CookieSyncManager.getInstance().stopSync();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		String url = intent.getStringExtra("url");
		if (url != null && url.length() > 0) {
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.RelativeLayout1);
			for (int i = layout.getChildCount() - 1; i > 0; i--) {
				View view = layout.getChildAt(i);
				if (view.getTag() != null && String.valueOf(view.getTag()).equals("webview")) {
					WebView webview = (WebView) view;
					webview.loadUrl("javascript:self.close()");
				}
			}
			loadUrl(url);
		}
		super.onNewIntent(intent);
	}



	private void init() {
		m_progressdialog = new ProgressDialog(this);
		m_progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		m_progressdialog.setTitle("");
		m_progressdialog.setCancelable(true);
		m_progressdialog.setMessage("잠시만 기다려주세요");

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar_webview);
		mProgressBar.setVisibility(View.GONE);

		mConfig = new ForYouConfig(this);
		appName = getPackageName();

		Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
		intent.putExtra("badge_count", 0);
		intent.putExtra("badge_count_package_name", "kr.itforyou.khj");
		intent.putExtra("badge_count_class_name", "kr.itforyou.khj.MainActivity");
		sendBroadcast(intent);
		mConfig.pref_save("badgeCount", "0");
	}

	private void loadUrl(String url) {

		mWebview.loadUrl(url);
	}

	@SuppressLint("JavascriptInterface")
	private void WebView() {
		mWebview = (WebView) findViewById(R.id.webView);
		mWebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebview.setScrollbarFadingEnabled(true);
		mWebview.setHorizontalScrollBarEnabled(false);
		mWebview.setVerticalScrollBarEnabled(false);
		mWebview.setWebViewClient(new WebViewClientClass());
		mWebview.setWebChromeClient(new WebChromeClientClass());
		mWebview.addJavascriptInterface(new WebChromeClientClass(), "androidfile");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			mWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		} else {
			mWebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		try {
			if (Build.VERSION.SDK_INT < 11) {
				ZoomButtonsController zoom_controll = null;
				zoom_controll = (ZoomButtonsController) mWebview.getClass().getMethod("getZoomButtonsController").invoke(mWebview);
				zoom_controll.getContainer().setVisibility(View.GONE);
			} else {
				mWebview.getSettings().getClass().getMethod("setDisplayZoomControls", Boolean.TYPE).invoke(mWebview.getSettings(), false);
			}
		} catch (Exception e) {
		}

		WebSettings webSettings = mWebview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSaveFormData(false);
		webSettings.setPluginState(PluginState.ON);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setUseWideViewPort(true);//
		webSettings.setSupportZoom(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setSupportMultipleWindows(false);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDatabaseEnabled(true);

		webSettings.setDomStorageEnabled(true);
		webSettings.setUserAgentString( webSettings.getUserAgentString() + " (XY ClientApp)" );
		webSettings.setAllowFileAccess(true);

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH){
			webSettings.setTextZoom(100);
		}
		webSettings.setCacheMode (WebSettings.LOAD_NO_CACHE);

		loadUrl(MAIN_URL);
	}

	private class WebViewClientClass extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d("url",url);
			if (url.startsWith("tel:")) {
				Intent call_phone = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(call_phone);
				return true;
			} else if (url.startsWith("sms:")) {
				Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
				startActivity(i);
				return true;
			} else if (url.startsWith("mailto:")) {

				String maile[] = url.split(":");
				String body = "";

				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[] { maile[1]});
				email.putExtra(Intent.EXTRA_SUBJECT, "");
				email.putExtra(Intent.EXTRA_TEXT, body);
				// need this to prompts email client only
				email.setType("message/rfc822");
				startActivity(Intent.createChooser(email, "메일을 보낼 앱을 선택해주세요."));

				return true;
			} else if(url.startsWith("intent://")){
				mWebview.loadUrl("http://wickhan.com/admin/login.html");
				return true;
			} else if(url.equals("http://wickhan.com/okname/auth.html")||url.equals("http://www.wickhan.com/okname/auth.html")){
				Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
				startActivity(intent);
				return true;
			}else {
				if (url.indexOf("logout.php") > -1 || url.indexOf("login.php") > -1) {
					try {



						if(mConfig == null) mConfig = new ForYouConfig(MainActivity.this);
						mConfig.pref_save("id", "");
						mConfig.pref_save("pw", "");

						CookieManager.getInstance().removeAllCookie();

					} catch (Exception e) {
						// TODO: handle exception
					}

				}
				loadUrl(url);
			}
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			isLoading = true;
			mHandler.sendEmptyMessageDelayed(2, 750);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {

			isLoading =false;
			mHandler.removeMessages(2);
			mHandler.removeMessages(3);
			mProgressBar.setVisibility(View.GONE);

			if(IntentURL.length() > 0){
				loadUrl(IntentURL);
				IntentURL = "";
			}

			try {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
					CookieSyncManager.getInstance().sync();
				} else {
					CookieManager.getInstance().flush();
				}

				String id = mConfig.pref_get("id", "");
				if (id.equals("")) {
					String Allcookie = CookieManager.getInstance().getCookie(url);
					String cookieArr[] = Allcookie.split(";");
					for (String str : cookieArr) {
						String cookie[] = str.split("=");
						if (cookie[0].trim().equals("mb_id")) {
							String cookieVlaue = cookie[1].trim();
							try {
								mConfig.pref_save("id", cookieVlaue);
								gcm();
								break;
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			super.onPageFinished(view, url);
		}
	}

	private class WebChromeClientClass extends WebChromeClient {

		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			mProgressBar.setProgress(newProgress);
		}


		// For Android < 3.0
		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
			openFileChooser(uploadMsg, "");
			Log.d("iii","111");
		}

		// For Android 3.0+
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
			filePathCallbackNormal = uploadMsg;
			Log.d("iii","111");
			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
			i.setType("image/*");
			startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_NORMAL_REQ_CODE);
		}

		// For Android 4.1+
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
			Log.d("iii","222");
			openFileChooser(uploadMsg, acceptType);
		}


		// For Android 5.0+
		public boolean onShowFileChooser(
				WebView webView, ValueCallback<Uri[]> filePathCallback,
				FileChooserParams fileChooserParams) {

			Log.d("iii","333");
			if (filePathCallbackLollipop != null) {
//                    filePathCallbackLollipop.onReceiveValue(null);
				filePathCallbackLollipop = null;
			}
			filePathCallbackLollipop = filePathCallback;


			// Create AndroidExampleFolder at sdcard
			File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AndroidExampleFolder");
			if (!imageStorageDir.exists()) {
				// Create AndroidExampleFolder at sdcard
				imageStorageDir.mkdirs();
			}

			// Create camera captured image file path and name
			File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
			mCapturedImageURI = Uri.fromFile(file);

			Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
			i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
			i.setType("image/*");

			// Create file chooser intent
			Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
			// Set camera intent to file chooser
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

			// On select image call onActivityResult method of activity
			startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE);
			return true;

		}

		@Override
		public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
			WebView.HitTestResult result = view.getHitTestResult();
			String url = result.getExtra();

			/*if(result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE){
				try{
					Message hrefMsg = new Message();
					hrefMsg.setTarget(new Handler());
					view.requestFocusNodeHref(hrefMsg);
					url = (String)hrefMsg.getData().get("url");
				}
				catch(Exception e){

				}
			}

			if(url != null && url.length() > 0){
				if(!url.startsWith(SEVER_URL)){
					Uri uri = Uri.parse(url);
					Intent it  = new Intent(Intent.ACTION_VIEW,uri);
					startActivity(it);
					return false;
				}

			}

			view.removeAllViews();
			WebView newView = new WebView(MainActivity.this);
			WebSettings webSettings = newView.getSettings();

			webSettings.setSaveFormData(false);
			webSettings.setPluginState(PluginState.ON);
			webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
			webSettings.setBuiltInZoomControls(true);
			webSettings.setUseWideViewPort(true);//
			webSettings.setSupportZoom(true);
			webSettings.setLoadWithOverviewMode(true);
			webSettings.setSupportMultipleWindows(true);
			webSettings.setLoadsImagesAutomatically(true);
			webSettings.setJavaScriptEnabled(true);
			webSettings.setDatabaseEnabled(true);
			webSettings.setDatabasePath("data/data/kr.itforyou.khj/databases");
			webSettings.setDomStorageEnabled(true);
			webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
			webSettings.setUserAgentString( webSettings.getUserAgentString() + " (XY ClientApp)" );
			webSettings.setAllowFileAccess(true);
			webSettings.setSavePassword(false);
			webSettings.setAppCacheEnabled(true);
			webSettings.setAppCachePath("");
			webSettings.setAppCacheMaxSize(5*1024*1024);


			webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
			newView.getSettings().setCacheMode (WebSettings.LOAD_NO_CACHE);
			newView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
			newView.setScrollbarFadingEnabled(true);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				newView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
			} else {
				newView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			}


			try {
				if (android.os.Build.VERSION.SDK_INT < 11) {

					ZoomButtonsController zoom_controll = null;

					zoom_controll = (ZoomButtonsController) mWebview.getClass()
							.getMethod("getZoomButtonsController").invoke(mWebview);

					zoom_controll.getContainer().setVisibility(View.GONE);

				} else {

					newView.getSettings().getClass()
							.getMethod("setDisplayZoomControls", Boolean.TYPE)
							.invoke(mWebview.getSettings(), false);

				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			newView.setHorizontalScrollBarEnabled(false);
			newView.setVerticalScrollBarEnabled(false);
			newView.setTag("webview");
			newView.getSettings().setCacheMode (WebSettings.LOAD_NO_CACHE);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			newView.setLayoutParams(params);
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.RelativeLayout1);
			layout.addView(newView);



			newView.setWebChromeClient(new WebChromeClientClass() {
				@Override
				public void onCloseWindow(android.webkit.WebView window) {
					super.onCloseWindow(window);
					RelativeLayout layout = (RelativeLayout) findViewById(R.id.RelativeLayout1);
					layout.removeView(window);
				}
				@Override
				public boolean onCreateWindow(android.webkit.WebView view,boolean isDialog, boolean isUserGesture,Message resultMsg) {
					return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
				}

			});
			newView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}

				@Override
				public void onPageStarted (WebView view, String url, Bitmap favicon){
				}

				@Override
				public void onPageFinished(android.webkit.WebView view,String url) {
					super.onPageFinished(view, url);
				}
			});

			WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
			transport.setWebView(newView);
			resultMsg.sendToTarget();
			mWebview.post(new Runnable() {
				@Override
				public void run() {

				}
			});*/

			return false;
		}

	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File imageFile = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);
		return imageFile;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == FILECHOOSER_NORMAL_REQ_CODE) {
				if (filePathCallbackNormal == null) return;
				Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
				filePathCallbackNormal.onReceiveValue(result);


			} else if (requestCode == FILECHOOSER_LOLLIPOP_REQ_CODE) {


				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

					//멀티 업로드
					if(data != null && data.getClipData() != null){
						int count = data.getClipData().getItemCount();
						Uri[] uriArr = new Uri[count];
						for(int i=0; i<count;i++){
							uriArr[i] = data.getClipData().getItemAt(i).getUri();
						}
						filePathCallbackLollipop.onReceiveValue(uriArr);
					}else if( data!=null && data.getClipData() == null) {
						if (data != null) {
							Uri[] result = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
							filePathCallbackLollipop.onReceiveValue(result);
						}
					}
					/*if (resultCode == RESULT_OK) {
						result = (data == null) ? new Uri[]{mCapturedImageURI} : WebChromeClient.FileChooserParams.parseResult(resultCode, data);
						filePathCallbackLollipop.onReceiveValue(result);
					}*/


				}
			}
		} else {
			try {
				if (filePathCallbackLollipop != null) {
					filePathCallbackLollipop.onReceiveValue(null);
					filePathCallbackLollipop = null;

				}
			} catch (Exception e) {

			}
		}

	}



	private void Intro() {
		Intent mIntent = new Intent(MainActivity.this, IntroActivity.class);
		startActivity(mIntent);
	}

	private void Bottom() {
		LinearLayout layout1 = (LinearLayout) findViewById(R.id.layout_bottom_icon_1);
		LinearLayout layout2 = (LinearLayout) findViewById(R.id.layout_bottom_icon_2);
		LinearLayout layout3 = (LinearLayout) findViewById(R.id.layout_bottom_icon_3);
		LinearLayout layout4 = (LinearLayout) findViewById(R.id.layout_bottom_icon_4);
		LinearLayout layout5 = (LinearLayout) findViewById(R.id.layout_bottom_icon_5);

		TextView textview1 = (TextView) findViewById(R.id.tv_bottom_icon_1);
		TextView textview2 = (TextView) findViewById(R.id.tv_bottom_icon_2);
		TextView textview3 = (TextView) findViewById(R.id.tv_bottom_icon_3);
		TextView textview4 = (TextView) findViewById(R.id.tv_bottom_icon_4);
		TextView textview5 = (TextView) findViewById(R.id.tv_bottom_icon_5);

		ImageView imgview1 = (ImageView) findViewById(R.id.iv_bottom_icon_1);
		ImageView imgview2 = (ImageView) findViewById(R.id.iv_bottom_icon_2);
		ImageView imgview3 = (ImageView) findViewById(R.id.iv_bottom_icon_3);
		ImageView imgview4 = (ImageView) findViewById(R.id.iv_bottom_icon_4);
		ImageView imgview5 = (ImageView) findViewById(R.id.iv_bottom_icon_5);

		AddData(layout1, textview1, imgview1);
		AddData(layout2, textview2, imgview2);
		AddData(layout3, textview3, imgview3);
		AddData(layout4, textview4, imgview4);
		AddData(layout5, textview5, imgview5);

		for (BottomData data : mData) {
			data.layout.setVisibility(View.GONE);
		}

		int i = 0;
		for (String str : bottomText) {
			BottomData data = mData.get(i);
			data.layout.setVisibility(View.VISIBLE);
			data.layout.setOnClickListener(this);
			data.textview.setText(str);
			data.imageview.setImageResource(bottomImg[i]);
			i++;
		}
	}

	@Override
	public void onBackPressed() {

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.RelativeLayout1);
		for (int i = layout.getChildCount() - 1; i > 0; i--) {
			View view = layout.getChildAt(i);
			if (view.getTag() != null && String.valueOf(view.getTag()).equals("webview")) {
				WebView webview = (WebView) view;
				WebBackForwardList list2 = webview.copyBackForwardList();
				if (list2.getCurrentIndex() <= 0 && !webview.canGoBack()) {
					webview.loadUrl("javascript:self.close()");
					layout.removeView(webview);
				} else {
					webview.goBack();
				}
				return;
			}
		}

		WebBackForwardList list = mWebview.copyBackForwardList();
		if (list.getCurrentIndex() <= 0 && !mWebview.canGoBack()) {
			finishApp();
		} else {

			String url = mWebview.getUrl();

			if(url.equals(MAIN_URL)){
				finishApp();
			} else if(url.indexOf("login.php") > -1){
				loadUrl(MAIN_URL);
			} else if (url.indexOf("board.php?bo_table=") > -1 && url.indexOf("wr_id") > -1) {
				url = url.replaceAll("\\&wr_id=\\d+", "");
				loadUrl(url);
			} else if (url.indexOf("board.php?bo_table=") > -1) {
				loadUrl(MAIN_URL);
			} else {
				mWebview.goBack();
			}
		}
	}

	private void AddData(LinearLayout layout, TextView textview, ImageView imageview) {
		BottomData data = new BottomData();
		data.layout = layout;
		data.imageview = imageview;
		data.textview = textview;
		mData.add(data);
	}

	@Override
	public void onClick(View v) {

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.RelativeLayout1);
		for (int i = layout.getChildCount() - 1; i > 0; i--) {
			View view = layout.getChildAt(i);
			if (view.getTag() != null && String.valueOf(view.getTag()).equals("webview")) {
				WebView webview = (WebView) view;
				webview.loadUrl("javascript:self.close()");
				layout.removeView(webview);
			}
		}

		switch (v.getId()) {
			case R.id.layout_bottom_icon_1: {
				loadUrl(MAIN_URL);
			}
			break;
			case R.id.layout_bottom_icon_2: {
			/*try {
				try {
					PackageManager manager = getPackageManager();
				    Intent i = manager.getLaunchIntentForPackage("com.nhn.android.band");
				} catch (Exception e) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nhn.android.band"));
				    startActivity(intent);
				    return;
				}
				String serviceDomain = "http://www.wickhan.com/"; //  연동 서비스 도메인
				String encodedText = "[W.I CKHAN] - 정보공유, 구구인구직, 부동산, 여행정보를 한곳에~\nhttps://play.google.com/store/apps/details?id="+ getPackageName(); // 글 본문 (utf-8 urlencoded)
				try {
					encodedText = URLEncoder.encode(encodedText, "utf-8");
				} catch (Exception e) {
					// TODO: handle exception
				}
				Uri uri = Uri.parse("bandapp://create/post?text=" + encodedText + "&route=" + serviceDomain);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			} catch (Exception e) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nhn.android.band"));
			    startActivity(intent);
			    return;
			}*/
				try {
					Intent intent = new Intent(Intent.ACTION_SEND);

					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_SUBJECT, "[W.I CKHAN]");

					intent.putExtra(Intent.EXTRA_TEXT, "정보공유, 구구인구직, 부동산, 여행정보를 한곳에~\nhttps://play.google.com/store/apps/details?id="+ getPackageName());
					startActivity(Intent.createChooser(intent,"Choose"));
				}catch (Exception e){
					Log.d("screen-error",e.toString());
				}

			}
			break;
			case R.id.layout_bottom_icon_3: {
				try {
					finishApp();
				} catch (Exception e) {
					// TODO: handle exception
				}



			}
			break;
			case R.id.layout_bottom_icon_4: {
				finishApp();
			}
			break;
			case R.id.layout_bottom_icon_5: {

			}
			break;
		}
	}

	private void finishApp() {
		if (end == true) {
			end = false;
			finish();
		} else {
			end = true;
			Toast.makeText(this, "한번 더 클릭하면 종료됩니다.", Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessageDelayed(1, 1500);
		}
	}



	final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if(msg.what == 0){
			} else if(msg.what == 1){
				end = false;
			} else if(msg.what == 2){
				if(isLoading == true){
					mProgressBar.setProgress(0);
					mProgressBar.setVisibility(View.VISIBLE);
					mHandler.sendEmptyMessageDelayed(3, 500);
				}

			} else if(msg.what == 3){
				if(isLoading == true){
					mProgressBar.setVisibility(View.GONE);
				}
			}
		}
	};

	public void gcm() {
		/*
		m_Gcmutil = new GCMUTIL(this);
		m_Gcmutil.setServerID(GCMUTIL.R_KEY);
		m_Gcmutil.register();
		*/

	}

	class BottomData {
		LinearLayout layout = null;
		TextView textview = null;
		ImageView imageview = null;
	}
}
