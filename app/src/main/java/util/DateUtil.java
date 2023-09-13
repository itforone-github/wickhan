package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	public static boolean Compare(String strFormat, String date1, String date2){
		
		SimpleDateFormat format = null;
		if(strFormat != null && strFormat.length() > 0){
			format = new SimpleDateFormat( strFormat, java.util.Locale.getDefault());
		}
		
        Date day1 = null;
        Date day2 = null;
        try {
            day1 = format.parse(date1);
            day2 = format.parse(date2);
		} catch (Exception e) {
		}
        
        int compare = day1.compareTo( day2 );
        if ( compare >= 0 )
        {
            return true;
        }
        else
        {
        	return false;
        }
	}
	
	public static boolean Compare(String date1, String date2){
		return Compare("yyyy-MM-dd", date1, date2);
	}
}
