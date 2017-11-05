package cn.okpay.android.sdkpay;

import android.app.Activity;
import android.widget.Toast;
import cn.okpay.android.sdkhelper.TAGS;

public class NullPay extends Pay {
	
	NullPay(int payType, IPayListener listener) {
		super(payType, listener);
	}

	@Override
	protected Result payDoing(Activity activity) {
		TAGS.log("NullPay-->payDoing");
		return new Result();
	}

	@Override
	protected void payBefore(Activity activity) {
		TAGS.log("NullPay-->payBefore");
		Toast.makeText(activity, "请选择正确的支付方式！", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void putParamBeforePay() {
		TAGS.log("NullPay-->putParamBeforePay");
	}

	@Override
	protected boolean isLaunchPay(Activity activity) {
		return false;
	}

	@Override
	protected String getPayChannel() {
		return "";
	}

}
