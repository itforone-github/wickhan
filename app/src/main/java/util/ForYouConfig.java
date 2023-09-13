package util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ForYouConfig {
	public String prefname = "";
	private Context context = null;
	public static String m_save_folder = "";
	private static int Mode = Context.MODE_PRIVATE;

	public ForYouConfig(Context context) {
		this.context = context;
		prefname = context.getPackageName().toString().substring(8)
				+ "_Config_preferences";
		m_save_folder = this.context.getCacheDir().getAbsolutePath() + "/log/";
		File file = new File(m_save_folder);
		file.mkdirs();
		
		if(android.os.Build.VERSION.SDK_INT >= 11)
			Mode = Activity.MODE_MULTI_PROCESS;
	}

	public void pref_save(String pref_name, String pref_value) {
		synchronized (ForYouConfig.class) {
			SharedPreferences prefs = context.getSharedPreferences(prefname,Mode);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(pref_name, pref_value);
			editor.commit();
		}
	}

	public String pref_get(String pref_name) {
		synchronized (ForYouConfig.class) {
			SharedPreferences prefs = context.getSharedPreferences(prefname,Mode);
			String text = prefs.getString(pref_name, "");
			return text;
		}
	}

	public String pref_get(String pref_name,String defult_value) {
		synchronized (ForYouConfig.class) {
			SharedPreferences prefs = context.getSharedPreferences(prefname,Mode);
			if (pref_isset(pref_name) == false)
				return defult_value;
			String text = prefs.getString(pref_name, "");
			return text;
		}
	}
	
	public boolean pref_get_b(String pref_name,boolean bool) {
		synchronized (ForYouConfig.class) {
			SharedPreferences prefs = context.getSharedPreferences(prefname,Mode);
			bool = prefs.getBoolean(pref_name, bool);
			return bool;
		}
	}
	
	public void pref_save_b(String pref_name,boolean bool) {
		synchronized (ForYouConfig.class) {
			SharedPreferences prefs = context.getSharedPreferences(prefname,Mode);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean(pref_name, bool);
			editor.commit();
		}
	}

	public void pref_delete(String pref_name) {
		synchronized (ForYouConfig.class) {
			SharedPreferences prefs = context.getSharedPreferences(prefname,
					Mode);
			SharedPreferences.Editor editor = prefs.edit();
			editor.remove(pref_name);
			editor.commit();
		}
	}
	
	public boolean pref_isset(String pref_name){
		synchronized (ForYouConfig.class) {
			SharedPreferences prefs = context.getSharedPreferences(prefname,
					Mode);
			String text = prefs.getString(pref_name, "!NOT_STRING!");

			if (text != null && text.equals("!NOT_STRING!") == false)
				return true;
			return false;
		}
	}
	
	public void pref_all_delete() {
		/*SharedPreferences prefs = context.getSharedPreferences(prefname,
				Mode);
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.commit();*/
	}

	public boolean append_text(String file_name, String text) {
		boolean r = false;
		String gen_str = get_text(file_name);
		gen_str = "\r\n" + gen_str;
		File file = new File(m_save_folder, file_name);
		try {
			FileOutputStream fileOutput = new FileOutputStream(file);
			fileOutput.write(text.getBytes("UTF-8"));
			fileOutput.write(gen_str.getBytes("UTF-8"));
			fileOutput.flush();
			fileOutput.close();
			r = true;
		} catch (Exception e) {
		}
		return r;
	}

	public boolean new_text(String file_name, String text) {
		boolean r = false;
		del_text(file_name);
		File file = new File(m_save_folder, file_name);
		try {
			FileOutputStream fileOutput = new FileOutputStream(file);
			fileOutput.write(text.getBytes("UTF-8"));
			fileOutput.flush();
			fileOutput.close();
			r = true;
		} catch (Exception e) {
		}
		return r;
	}

	public String get_text(String file_name) {
		String r = "";
		try {
			File file = new File(m_save_folder, file_name);

			if (file.isFile()) {
				StringBuilder r_s = new StringBuilder();
				FileInputStream fileInputStream = new FileInputStream(file);

				byte buff[];
				int length = 0;
				while (true) {
					buff = new byte[1024];
					length = fileInputStream.read(buff);
					if (length < 1)
						break;
					r_s.append(new String(buff, 0, length, "UTF-8"));
				}
				r = r_s.toString().trim();
				fileInputStream.close();
			}
		} catch (Exception e) {
		}

		return r;
	}

	public void del_text(String file_name) {
		File file = new File(m_save_folder, file_name);
		try {
			if (file.isFile())
				file.delete();
		} catch (Exception e) {
		}
	}
	
	public boolean Check_Date_IsUpdate(String strDate){
		return Check_Date_IsUpdate(strDate, 1);
	}
	
	@SuppressLint("SimpleDateFormat")
	public boolean Check_Date_IsUpdate(String strDate, int day){
		DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd HH:mm:SS");
		Date tempDate1;
		Date nowDate = new Date();
		Date isDate =  new Date(nowDate.getTime() - (1000 * 60 * 60 * 24 * day));
		
		try {
			tempDate1 = sdFormat.parse(strDate);
		} catch (ParseException e) {
			return true;
		}
		
		long milis = isDate.getTime() - tempDate1.getTime();
		
		if(milis > 0)
			return true;
		
		return false;
	}

	public boolean Network_Check() {
		boolean b = false;
		ConnectivityManager connect = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connect != null){
			if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null){
				if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED){
					b = true;
				}
			}
			if(connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null){
				if(connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
					b = true;
				}
			}
			if(connect.getNetworkInfo(ConnectivityManager.TYPE_WIMAX) != null){
				if(connect.getNetworkInfo(ConnectivityManager.TYPE_WIMAX).getState() == NetworkInfo.State.CONNECTED){
					b = true;
				}
			}
			
			NetworkInfo netInfo = connect.getActiveNetworkInfo();
			if(netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED){
				b = true;
			}
		}
		return b;
	}

	public boolean isWifiCheck() {
		boolean b = false;
		ConnectivityManager connect = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connect != null){
			if(connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null){

				NetworkInfo netInfo = connect.getActiveNetworkInfo();
				
				if(connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
					b = true;
				}
				else if(!(is3GCheck() || is4GCheck()) && (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)){
					b = true;
				}
			}
		}
		return b;
	}
	
	public boolean is4GCheck(){
			boolean b = false;
			ConnectivityManager connect = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			TelephonyManager service = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			if(connect != null && service != null){
				if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null){
					if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED && service.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE){
						b = true;
					}
				}
			}

			return b;
	}

	public boolean is3GCheck() {
		boolean b = false;
		ConnectivityManager connect = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connect != null){
			if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null){
				if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED){
					b = true;
				}
			}
		}
		return b;
	}
	
	public boolean isMoblie(){
		ConnectivityManager connect = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connect != null){
			if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null){
				return true;
			}
		}
		return false;
	}
	
	public boolean is3GDataEnabled() {
		TelephonyManager telephonyManager = (TelephonyManager) context
	            .getSystemService(Context.TELEPHONY_SERVICE);

	    if(telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED){
	        return true;
	    }else{
	    	return false;  
	    }   
	}

	public interface OKFunction {
		void call();
	}
}
