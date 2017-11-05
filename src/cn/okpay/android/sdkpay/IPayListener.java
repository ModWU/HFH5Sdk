package cn.okpay.android.sdkpay;

public interface IPayListener {
	void payStart(int payType);
	void payFinished(boolean isSuccess, String info);
	void payBack(String out_trade_no);
}
