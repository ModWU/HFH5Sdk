package cn.okpay.android.sdkpay;

import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import cn.okpay.android.sdkhelper.DYH5Activity;
import cn.okpay.android.sdkhelper.HttpUtils;
import cn.okpay.android.sdkhelper.ParamNames;
import cn.okpay.android.sdkhelper.TAGS;
import cn.okpay.android.sdkhelper.Util;

public class AlipayPay extends Pay {
	
	private static final String PACKAGE_NAME = "com.eg.android.AlipayGphone";
	
	AlipayPay(int payType, IPayListener listener) {
		super(payType, listener);
	}
	
	
	@Override
	protected void payBefore(Activity activity) {
		TAGS.log("--------------------------支付宝支付------------------------------");
		payBeforeSendMessage(PAY_TYPE_ALIPAY);
	}
	
	


	@Override
	protected Result payDoing(Activity activity) {
		
		final Result result = new Result();
	
		String ip = getIp(result);
		if(Util.isEmpty(ip)) return result;
		
		mParamMap.put(ParamNames.IP, ip);
		
		adjustParam();
		
		String tmpResult = HttpUtils.sendPostUTF8(getHost() + "/pay/unifiedorder", mParamMap, false);
		
		try {
			JSONObject jsonObj = new JSONObject(tmpResult);
			String code = jsonObj.getString("code");
			if("success".equalsIgnoreCase(code)) {
				JSONObject dataObj = new JSONObject(jsonObj.getString("data"));
				String mweb_url = dataObj.getString("mweb_url");
				
				startPayH5Activity(activity, mweb_url, DYH5Activity.PAGE_ALIPAY, DYH5Activity.GET);
				
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
	protected void putParamBeforePay() {
		
	}


	@Override
	protected boolean isLaunchPay(Activity activity) {
		boolean isLaunchPay = Util.isPackageAvilible(activity, PACKAGE_NAME);
		if(!isLaunchPay)
			Toast.makeText(activity, "支付前请先安装支付宝客户端", Toast.LENGTH_SHORT).show();
		return isLaunchPay;
	}


	@Override
	protected String getPayChannel() {
		return "alipay";
	}


}
