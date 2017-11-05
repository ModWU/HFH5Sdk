package cn.okpay.android.sdkpay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import cn.okpay.android.sdkhelper.DYH5Activity;
import cn.okpay.android.sdkhelper.HttpUtils;
import cn.okpay.android.sdkhelper.ParamNames;
import cn.okpay.android.sdkhelper.PayLoadingActivity;
import cn.okpay.android.sdkhelper.TAGS;
import cn.okpay.android.sdkhelper.Util;

public abstract class Pay {
	
	protected IPayListener listener;
	
	protected String localIp = "";
	
	protected HashMap<String, String> mParamMap = new HashMap<String, String>();
	
	
	/*
	 * 支付类型
	 */
	public static final int PAY_TYPE_NULL = -1;
	public static final int PAY_TYPE_WEIXIN = 0;
	public static final int PAY_TYPE_ALIPAY = 1;
	public static final int PAY_TYPE_WAP_CASHIER = 2;
	public static final int PAY_TYPE_PROTO_CASHIER = 3;
	
	/*
	 * 消息类别
	 */
	protected static final int MESSAGE_START_PAY = 0;
	protected static final int MESSAGE_PAY_FINISHED = 1;
	public static final int MESSAGE_PAY_FAIL_ORDERID_EXIST = 2;
	
	
	/*
	 * 消息传送的数据标记
	 */
	protected static final String TAG_IS_SUCCESS = "tag_is_success";
	protected static final String TAG_INFO = "tag_info";
	
	
	private static boolean isPaying = false;
	
	private Activity mActivity;
	
	private String host = "http://pay.yunbee.cn";
	
	protected String getHost() {
		return host;
	}
	
	public static class Builder {
		
		private IPayListener listener;
		private String host;
		private String mch_id, key, app_id, trade_type, nonce_str, app_name, bundle, detail, out_trade_no, total_fee, notify_url;
		
		public Builder buildListener(IPayListener listener) {
			this.listener = listener;
			return this;
		}
		
		public Builder buildHost(String host) {
			this.host = host;
			return this;
		}
		
		
		public Builder buildParam(String mch_id, String key, String app_id, String trade_type, String nonce_str, String app_name, String bundle, String detail, String out_trade_no, String total_fee, String notify_url) {
			this.mch_id = mch_id;
			this.key = key;
			this.app_id = app_id;
			this.trade_type = trade_type;
			this.nonce_str = nonce_str;
			this.app_name = app_name;
			this.bundle = bundle;
			this.detail = detail;
			this.out_trade_no = out_trade_no;
			this.total_fee = total_fee;
			this.notify_url = notify_url;
			return this;
		}
		
		public Pay build(int payType) {
			Pay pay = Pay.build(payType, listener).buildParam(mch_id, key, app_id, trade_type, nonce_str, app_name, bundle, detail, out_trade_no, total_fee, notify_url);
			pay.host = host;
			return pay;
		}
	}
	
	
	Pay(int payType) {
		this(payType, null);
	}
	
	Pay(int payType, IPayListener listener) {
		assertUiThread();
		localIp = Util.getGprsIpAddress();
		this.listener = listener;
	}
	
	protected void adjustParam() {
		String key = mParamMap.remove(ParamNames.KEY);
		List<String> keyList = new ArrayList<String>(mParamMap.keySet());
		Collections.sort(keyList, new Comparator<String>() {

			@Override
			public int compare(String lhs, String rhs) {
				return lhs.compareTo(rhs);
			}
		});
		
		StringBuffer paramsBuffer = new StringBuffer();
		for(String k : keyList) {
			String v = mParamMap.get(k);
			paramsBuffer.append(k + "=" + v);
			paramsBuffer.append("&");
		}
		paramsBuffer.append("key=" + key);
		
		Log.i("INFO", "paramsBuffer: " + paramsBuffer.toString());
		
		String sign = Util.md5(paramsBuffer.toString()).toUpperCase();
		mParamMap.put("sign", sign);
	}
	
	protected void addParams(Map<String, String> params) {
		mParamMap.putAll(params);
	}
	protected void addParam(String k, String v) {
		mParamMap.put(k, v);
	}
	
	public static void setDebug(boolean isDebug) {
		TAGS.isDebug = isDebug;
	}
	
	 Pay buildParam(String mch_id, String key, String app_id, String trade_type, String nonce_str, String app_name, String bundle, String detail, String out_trade_no, String total_fee, String notify_url) {
		HashMap<String, String> tmpParams = new HashMap<String, String>();
		tmpParams.put(ParamNames.MCH_ID, mch_id);
		tmpParams.put(ParamNames.KEY, key);
		tmpParams.put(ParamNames.APP_ID, app_id);
		tmpParams.put(ParamNames.PAY_CHANNEL, getPayChannel());
		tmpParams.put(ParamNames.TRADE_TYPE, trade_type);
		tmpParams.put(ParamNames.NONCE_STR, nonce_str);
		tmpParams.put(ParamNames.APP_NAME, app_name);
		tmpParams.put(ParamNames.BUNDLE, bundle);
		tmpParams.put(ParamNames.DETAIL, detail);
		tmpParams.put(ParamNames.OUT_TRADE_NO, out_trade_no);
		tmpParams.put(ParamNames.TOTAL_FEE, total_fee);
		tmpParams.put(ParamNames.NOTIFY_URL, notify_url);
		addParams(tmpParams);
		return this;
	}
	
	static Pay build(int payType, IPayListener listener) {
		if(payType == PAY_TYPE_ALIPAY) {
			return new AlipayPay(payType, listener);
		} else if(payType == PAY_TYPE_WAP_CASHIER) {
			return new WapCashierPay(payType, listener);
		} else if(payType == PAY_TYPE_WEIXIN) {
			return new WechatPay(payType, listener);
		} else if(payType == PAY_TYPE_PROTO_CASHIER) {
			return new ProtoCashierPay(payType, listener);
		} else {
			return new NullPay(payType, listener);
		}
	}
	
	protected String getIp(Result result) {
		String ipUrl = getHost() + "/get/clientip";
		String ipData = HttpUtils.sendGet(ipUrl);
		String ip = "";
		
		try {
			JSONObject jsonObj = new JSONObject(ipData);
			ip = jsonObj.getString("ip");
			if(Util.isEmpty(ip)) {
				result.code = Result.RESULT_REQUEST_NULL;
				result.result = "cannot get ip from \"" + ipUrl + "\"";
			}
			
		} catch (Exception e) {
			ip = localIp;
		}
		return ip;
	}
	
	protected void assertUiThread() {
		if(!isOnMainThread()) {
			throw new IllegalArgumentException("You cannot call this method on a background thread");
		}
		
	}
	
	private boolean isOnMainThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}

	protected Handler mListenerHandler = new Handler(Looper.getMainLooper()) {
		
		private void payFinished(Message msg) {
			Bundle data = msg.getData();
			if(data == null) data = new Bundle();
			boolean isSuccess = data.getBoolean(TAG_IS_SUCCESS, false);
			String info = data.getString(TAG_INFO);
			listener.payFinished(isSuccess, info);
		}
		
		public void handleMessage(android.os.Message msg) {
			if(listener == null)
				return;
			
			switch(msg.what) {
			case MESSAGE_START_PAY:
				listener.payStart(msg.arg1);
				break;
				
			case MESSAGE_PAY_FINISHED:
				payFinished(msg);
				break;
			case MESSAGE_PAY_FAIL_ORDERID_EXIST:
				Toast.makeText(mActivity, "订单重复，支付返回！", Toast.LENGTH_SHORT).show();
				payFinished(msg);
				if(mActivity != null) 
					mActivity.finish();
				break;
				
			}
		};
	};
	
	protected void payBeforeSendMessage(int payType) {
		Message msg = obtainMessage(MESSAGE_START_PAY, null);
		msg.arg1 = payType;
		sendMessage(msg);
	}
	
	protected void payFinishedSendMessage(Activity activity, boolean isSuccess, String result) {
		Bundle data = new Bundle();
		data.putBoolean(TAG_IS_SUCCESS, isSuccess);
		data.putString(TAG_INFO, result);
		sendMessage(obtainMessage(MESSAGE_PAY_FINISHED, data));
	}
	
	protected void payFailOrderIdExist(Activity activity, boolean isSuccess, String result) {
		TAGS.log("--payFailOrderIdExist--");
		Bundle data = new Bundle();
		data.putBoolean(TAG_IS_SUCCESS, false);
		data.putString(TAG_INFO, result);
		sendMessage(obtainMessage(MESSAGE_PAY_FAIL_ORDERID_EXIST, data));
	}
	
	public Pay setListener(IPayListener listener) {
		this.listener = listener;
		return this;
	}
	
	protected void sendMessage(Message msg) {
		if(msg != null) {
			msg.setTarget(mListenerHandler);
			msg.sendToTarget();
		}
	}
	
	private void startPayLoadingActivity(Activity activity) {
		PayLoadingActivity.setPay(this);
		activity.startActivity(new Intent(activity, PayLoadingActivity.class));
	}
	
	protected void startPayH5Activity(Activity activity, String url, int payPage, String requestMethod) {
		DYH5Activity.setPayListener(listener);
		Intent it = new Intent(activity, DYH5Activity.class);
		it.putExtra(DYH5Activity.WEBVIEW_URL, url);
		it.putExtra(DYH5Activity.PAGE_SEE, payPage);
		it.putExtra(DYH5Activity.WEBVIEW_METHOD, requestMethod);
		it.putExtra(ParamNames.OUT_TRADE_NO, mParamMap.get(ParamNames.OUT_TRADE_NO));
		activity.startActivity(it);
	}
	
	protected Message obtainMessage(int what, Bundle data) {
		Message tmp = Message.obtain();
		tmp.what = what;
		tmp.setData(data);
		return tmp;
	}
	
	public void pay(Activity activity) {
		assertUiThread();
		mActivity = activity;
		boolean isLaunchPay = isLaunchPay(activity);
		if(isLaunchPay && !isPaying) {
			TAGS.log("Pay->pay: " + this);
			isPaying = true;
			startPayLoadingActivity(activity);
		}
	}
	
	private void payBeforeDoThing(Activity activity) {
		payBefore(activity);
		putParamBeforePay();
	}
	
	
	
	public class PayTask extends AsyncTask<String, Void, Result> {
		
		private Activity activity;
		private IPayNotify payNotify;
		
		public PayTask(Activity activity, IPayNotify payNotify) {
			this.activity = activity;
			this.payNotify = payNotify;
		}
		
		@Override
		protected void onPreExecute() {
			payBeforeDoThing(activity);
			if(payNotify != null)
				payNotify.payBefore();
		}
		
		@Override
		protected Result doInBackground(String... params) {
			Result result = payDoing(activity);
			return result;
		}
		
		@Override
		protected void onPostExecute(Result result) {
			parseResultFinished(result);
			
			isPaying = false;
			if(payNotify != null) 
				payNotify.payAfter();
		}

		private void parseResultFinished(Result result) {
			if(isNeedSeedFinishedMessage(result)) {
				String code = parseResult(result.result);
				TAGS.log("PayTask->parseResultFinished->code: " + code);
				if("order_exist".equals(code)) 
					payFailOrderIdExist(activity, false, result.result);
				else
					payFinishedSendMessage(activity, result.code == Result.RESULT_SUCCESS, result.result);
			}
			
		}

		private String parseResult(String result) {
			try {
				JSONObject jsonObj = new JSONObject(result);
				String code = jsonObj.getString("code");
				return code;
			} catch (JSONException e) {
			}
			
			return null;
		}

		private boolean isNeedSeedFinishedMessage(Result result) {
			return result.code != Result.RESULT_PROTO_CASHIER_PAGE_SHOW;
		}
		
	}
	
	class Result {
		public static final int RESULT_REQUEST_NULL = 1001;
		public static final int RESULT_DEJSON_EXCEPTION = 1002;
		public static final int RESULT_REQUEST_FAIL = 1003;
		public static final int RESULT_PROTO_CASHIER_PAGE_SHOW = 1004;
		public static final int RESULT_SUCCESS = 200;
		
		
		public int code;
		public String result = "";
		
		public Result(int code, String result) {
			this.code = code;
			this.result = result;
		}
		
		public Result() {}
	}
	
	
	
	protected abstract Result payDoing(Activity activity);
	protected abstract void payBefore(Activity activity);
	protected abstract boolean isLaunchPay(Activity activity);
	protected abstract void putParamBeforePay();
	protected abstract String getPayChannel();
}
