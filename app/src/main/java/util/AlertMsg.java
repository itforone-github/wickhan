package util;

import android.app.AlertDialog;
import android.content.Context;

public class AlertMsg {
	private Context context;
	public AlertMsg(Context context){
		this.context = context;
	}
	
	public void show(String Title, String Message, String btnName){
		AlertDialog.Builder aleart = new AlertDialog.Builder(context);
		aleart.setTitle(Title);
		aleart.setMessage(Message);
		aleart.setPositiveButton(btnName, null);
		
		AlertDialog dialog = aleart.create();
		dialog.show();
	}
}
