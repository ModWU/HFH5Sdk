package cn.okpay.android.sdkpay;

import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import cn.okpay.android.sdkhelper.DYH5Activity;
import cn.okpay.android.sdkhelper.HttpUtils;
import cn.okpay.android.sdkhelper.TAGS;
import cn.okpay.android.sdkhelper.Util;

public class WapCashierPay extends Pay {
	
	WapCashierPay(int payType, IPayListener listener) {
		super(payType, listener);
	}

	@Override
	protected void payBefore(Activity activity) {
		TAGS.log("--------------------------WAP收银台支付------------------------------");
		payBeforeSendMessage(PAY_TYPE_WAP_CASHIER);
		putParamBeforePay();
	}


	protected void putParamBeforePay() {
		
	}
	

	@Override
	protected Result payDoing(Activity activity) {
		
		final Result result = new Result();
	
		String ip = getIp(result);
		if(Util.isEmpty(ip)) return result;
		
		mParamMap.put("spbill_create_ip", ip);
		
		adjustParam();
		
		String tmpResult = HttpUtils.sendPostUTF8(getHost() + "/pay/wap/unifiedorder", mParamMap);
		
		try {
			JSONObject jsonObj = new JSONObject(tmpResult);
			String code = jsonObj.getString("code");
			if("success".equalsIgnoreCase(code)) {
				JSONObject dataObj = new JSONObject(jsonObj.getString("data"));
				String mweb_url = dataObj.getString("wap_url");
				
				startPayH5Activity(activity, mweb_url, DYH5Activity.PAGE_CASHIER, DYH5Activity.GET);
				
				result.code = Result.RESULT_SUCCESS;
				result.result = tmpResult;
				
				return result;
			}
			
		} catch (Exception e) {
			result.code = Result.RESULT_DEJSON_EXCEPTION;
			result.result = e.toString();
			Log.i("INFO", Log.getStackTraceString(e));
			return result;
		}
		
		result.code = Result.RESULT_REQUEST_FAIL;
		result.result = tmpResult;
		return result;
			
	}

	@Override
	protected boolean isLaunchPay(Activity activity) {
		return true;
	}

	@Override
	protected String getPayChannel() {
		return "cashier";
	}

}
