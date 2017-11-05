package cn.okpay.android.sdkhelper;

import android.util.Log;

public class TAGS {
	
	public static final int TYPE_D = 0;
	public static final int TYPE_I = 1;
	public static final int TYPE_V = 2;
	public static final int TYPE_E = 3;
	public static final int TYPE_W = 4;
	public static final int TYPE_WTF = 5;
	
	
	public static boolean isDebug = true;
	
	private static int type = TYPE_I;
	
	public static final String PROCESSING = "yunbee_processing";
	
	private static String tag = PROCESSING;
	
	/**
	 * <code>tag:</code> <strong><font color="red">yunbee_processing</font></strong>
	 */
	public static void log(String msg) {
		if(isDebug) {
			switch(type) {
			default:
			case TYPE_I:
				 Log.i(tag, msg);
				break;
				
			case TYPE_D:
				Log.d(tag, msg);
				break;
			case TYPE_V:
				 Log.v(tag, msg);
				break;
				
			case TYPE_E:
				Log.e(tag, msg);
				break;
				
			case TYPE_W:
				Log.w(tag, msg);
				break;
				
			case TYPE_WTF:
				Log.wtf(tag, msg);
				break;
			}
			
		}
	}
	
	public static void setTag(String tag) {
		TAGS.tag = tag;
	}
	
	public static void setType(int type) {
		TAGS.type = type;
	}
	
	
}
