package util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import co.kr.itforone.wickhan.MainActivity;
import co.kr.itforone.wickhan.R;


public class ProgressNotification extends Notification {

	public static final int WIFI = 10;
	public static final int MOBILE = 20;
	public static final int MOBILE4G = 30;
	public static final int STOP = 90;
	public static final int NO_CONNECT = 0;

	private Context _context;
	private NotificationManager _notificationManager;
	private boolean _start;
	private int _icon = R.drawable.ic_launcher;
	private String m_strTitle = "";
	private String m_strSubTitle = "";
	private long m_nPercent = 0;

	public static final int Noti_Downloading_Ref = 1000;
	public static final int Noti_DownloadComplement_Ref = Noti_Downloading_Ref + 1;
	public static final int Noti_DownloadError_Ref = Noti_DownloadComplement_Ref + 1;
	public static final int Noti_LoginError_Ref = Noti_DownloadError_Ref + 1;
	
	@SuppressWarnings("deprecation")
	public ProgressNotification(Context context, int icon) {
		super(icon, "업로드를 시작합니다.", System.currentTimeMillis());
		_icon = icon;
		_context = context;
		
		create();
	}

	public void create() {
		check();
		
		this.flags = Notification.FLAG_ONGOING_EVENT;
		_notificationManager = (NotificationManager) _context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		

		m_nPercent = 0;
		m_strSubTitle ="0KB/0KB(0%)";
	}
	
	public void check(){
		//if(this.contentView == null)
		{
			RemoteViews contentView = new RemoteViews(_context.getPackageName(),R.layout.filelot_downloading_notification);
			this.contentView = contentView;
			this.contentView.setImageViewResource(R.id.imgNoti,_icon);
			this.contentView.setTextViewText(R.id.txtNotiName, m_strTitle);
			this.contentView.setTextViewText(R.id.txtPerCentNoti, m_strSubTitle);
			this.contentView.setProgressBar(R.id.proBarNoti, 100, (int) m_nPercent, false);
		}
		//if(this.contentIntent == null)
		{
			Intent intent = new Intent(_context, MainActivity.class);
			final PendingIntent contentIntent = PendingIntent.getActivity(_context, 0,intent, 0);
			this.contentIntent = contentIntent;
		}
	}
	
	public void setTicker(String strTicker){
		try {
			this.tickerText = strTicker;
		} catch (Exception e) {
		}
	}

	public void setState(int state) {
		try {
			check();
			
			/*switch (state) {
			case WIFI:
				_icon = this.icon = R.drawable.logo_wifi;
				this.contentView.setImageViewResource(R.id.imgNoti,
						_icon);
				break;

			case MOBILE:
				_icon = this.icon = R.drawable.logo_3g;
				this.contentView.setImageViewResource(R.id.imgNoti,
						_icon);
				break;

			case MOBILE4G:
				_icon = this.icon = R.drawable.logo_4g;
				this.contentView.setImageViewResource(R.id.imgNoti,
						_icon);
				break;

			case STOP:
				_icon = this.icon = R.drawable.logo_stop;
				this.contentView.setImageViewResource(R.id.imgNoti,
						_icon);
				_notificationManager.notify(WebhardService.Noti_Downloading_Ref, this);
				break;

			case NO_CONNECT:
				_icon = this.icon = R.drawable.logo_no;
				this.contentView.setImageViewResource(R.id.imgNoti,
						_icon);
				break;
			}*/

		} catch (Exception e) {
		}
	}

	public void start() {
		try {
			check();
			if(_start != true){
				this.cancel();
			}
			_start = true;
			this.icon = _icon;
			this.contentView.setImageViewResource(R.id.imgNoti, _icon);
			_notificationManager.notify(Noti_Downloading_Ref, this);
		} catch (Exception e) {
		}
	}

	public void cancel() {
		try {
			_notificationManager.cancel(Noti_Downloading_Ref);
			_start = false;
			create();
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("deprecation")
	public void complete() {
		try {
			check();
			
			_start = false;

			this.flags = Notification.FLAG_AUTO_CANCEL;
			_notificationManager.notify(Noti_Downloading_Ref, this);
			_notificationManager.cancel(Noti_Downloading_Ref);
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("deprecation")
	public void stop(int nTotal, int nCount) {
		try {
			
			check();
			_start = false;

			this.flags = Notification.FLAG_AUTO_CANCEL;
			_notificationManager.notify(Noti_Downloading_Ref, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTitle(String title) {
		try {
			m_strTitle = title;
			check();
			
			if (_start == false)
				return;
			this.contentView.setTextViewText(R.id.txtNotiName, m_strTitle);
			_notificationManager.notify(Noti_Downloading_Ref, this);
		} catch (Exception e) {
		}

	}

	public void setProgress(long nDownSize, long nFileSize) {
		try {
			if (nDownSize > 0) {
				m_nPercent= nDownSize * 100 / nFileSize;
			} else
				m_nPercent = 0;
			
			m_strSubTitle = nDownSize + "/"
					+ nFileSize + "("
					+ m_nPercent + "%)";
			
			check();
			
			if (_start == false)
				return;
			
			this.contentView.setTextViewText(R.id.txtPerCentNoti, m_strSubTitle);
			this.contentView.setProgressBar(R.id.proBarNoti, 100, (int) m_nPercent, false);
			this.flags = Notification.FLAG_ONGOING_EVENT;
			
			_notificationManager.notify(Noti_Downloading_Ref, this);
		} catch (Exception e) {
		}
	}
	
	public static final int FormatAuto = 0;
	public static final int FormatByte = 1;
	public static final int FormatKB = 2;
	public static final int FormatMB = 3;
	public static final int FormatGB = 4;
	public static final int FormatTB = 5;
	
	public String bytesToSize(long nFileSize, int nRoundUp, int SizeFormat){
		String ret ="0B";
		double FileSize = nFileSize * 1.0f;
		double kilobyte = 1024.f;
		double megabyte = kilobyte * 1024.f;
		double gigabyte = megabyte * 1024.f;
		double terabyte = gigabyte * 1024.f;
		if(SizeFormat == FormatAuto){
			if ((FileSize >= 0) && (FileSize < kilobyte)) {
		        ret = FileSize + "B";
		    } else if ((FileSize >= kilobyte) && (FileSize < megabyte)) {
		    	ret = String.format("%." + nRoundUp + "f", (FileSize / kilobyte)) + "KB";
		    } else if ((FileSize >= megabyte) && (FileSize < gigabyte)) {
		    	ret = String.format("%." + nRoundUp + "f", (FileSize / megabyte)) + "MB";
		    } else if ((FileSize >= gigabyte) && (FileSize < terabyte)) {
		    	ret = String.format("%." + nRoundUp + "f", (FileSize / gigabyte)) + "GB";
		    } else if (FileSize >= terabyte) {
		    	ret = String.format("%." + nRoundUp + "f", (FileSize / terabyte)) + "TB";
		    } else {
		    	ret = FileSize + "B";
		    }
		} else if(SizeFormat == FormatByte){
			ret = FileSize + "B";
		} else if(SizeFormat == FormatKB){
	    	ret = String.format("%." + nRoundUp + "f", (FileSize / kilobyte)) + "KB";
		} else if(SizeFormat == FormatMB){
	    	ret = String.format("%." + nRoundUp + "f", (FileSize / megabyte)) + "MB";
		} else if(SizeFormat == FormatGB){
	    	ret = String.format("%." + nRoundUp + "f", (FileSize / gigabyte)) + "GB";
		} else if(SizeFormat == FormatTB){
	    	ret = String.format("%." + nRoundUp + "f", (FileSize / terabyte)) + "TB";
		}
		return ret;
	}
	
	public String bytesToSize(long nFileSize, int nRoundUp){
		return bytesToSize(nFileSize, nRoundUp, FormatAuto);
	}


}
