package cn.okpay.android.sdkpay;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import cn.okpay.android.sdkhelper.ParamNames;
import cn.okpay.android.sdkhelper.ProtoCashierActivity;
import cn.okpay.android.sdkhelper.TAGS;

public class ProtoCashierPay extends Pay {

	ProtoCashierPay(int payType, IPayListener listener) {
		super(payType, listener);
	}

	@Override
	protected Result payDoing(Activity activity) {
		ProtoCashierActivity.setPayListener(listener);
		Intent protoCashierIntent = new Intent(activity, ProtoCashierActivity.class);
		protoCashierIntent.putExtra(ParamNames.APP_NAME, mParamMap.get(ParamNames.APP_NAME));
		protoCashierIntent.putExtra(ParamNames.BUNDLE, mParamMap.get(ParamNames.BUNDLE));
		protoCashierIntent.putExtra(ParamNames.DETAIL, mParamMap.get(ParamNames.DETAIL));
		protoCashierIntent.putExtra(ParamNames.MCH_ID, mParamMap.get(ParamNames.MCH_ID));
		protoCashierIntent.putExtra(ParamNames.KEY, mParamMap.get(ParamNames.KEY));
		protoCashierIntent.putExtra(ParamNames.APP_ID, mParamMap.get(ParamNames.APP_ID));
		protoCashierIntent.putExtra(ParamNames.NONCE_STR, mParamMap.get(ParamNames.NONCE_STR));
		protoCashierIntent.putExtra(ParamNames.NOTIFY_URL, mParamMap.get(ParamNames.NOTIFY_URL));
		protoCashierIntent.putExtra(ParamNames.OUT_TRADE_NO, mParamMap.get(ParamNames.OUT_TRADE_NO));
		protoCashierIntent.putExtra(ParamNames.PAY_CHANNEL, mParamMap.get(ParamNames.PAY_CHANNEL));
		protoCashierIntent.putExtra(ParamNames.TOTAL_FEE, mParamMap.get(ParamNames.TOTAL_FEE));
		protoCashierIntent.putExtra(ParamNames.TRADE_TYPE, mParamMap.get(ParamNames.TRADE_TYPE));
		activity.startActivity(protoCashierIntent);
		return new Result(Result.RESULT_PROTO_CASHIER_PAGE_SHOW, "");
	}

	@Override
	protected void payBefore(Activity activity) {
		TAGS.log("--------------------------原生收银台支付------------------------------");
		payBeforeSendMessage(PAY_TYPE_PROTO_CASHIER);
	}

	@Override
	protected void putParamBeforePay() {
	}
	
	public void onWechatPay(View v) {
		
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
