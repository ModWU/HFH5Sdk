package cn.okpay.android.sdkhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.okpay.android.sdkpay.IPayListener;
import cn.okpay.android.sdkpay.Pay;

public class ProtoCashierActivity extends Activity implements IPayListener {
	//String mch_id, String pay_channel, String trade_type, String nonce_str, String app_name, String bundle, String detail, String out_trade_no, String total_fee, String notify_url
	private String mch_id;
	private String key;
	private String app_id;
	private String pay_channel;
	private String trade_type;
	private String nonce_str;
	private String app_name;
	private String bundle;
	private String detail;
	private String out_trade_no;
	private String total_fee;
	private String notify_url;
	
	private static IPayListener mListener;
	
	private TextView tv_price;
	
	public static void setPayListener(IPayListener listener) {
		mListener = listener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(ResourceUtils.getLayoutId(this, "dian_proto_cashier_activity"));
		
		initView();
		obtainData();
		setData();
	}

	private void setData() {
		String money = String.valueOf(Integer.valueOf(total_fee) / 100.0f);
		tv_price.setText(money);
	}

	private void initView() {
		tv_price = (TextView) findViewById(ResourceUtils.getId(this, "price"));
		
	}

	private void obtainData() {
		Intent it = getIntent();
		mch_id = it.getStringExtra(ParamNames.MCH_ID);
		key = it.getStringExtra(ParamNames.KEY);
		app_id = it.getStringExtra(ParamNames.APP_ID);
		pay_channel = it.getStringExtra(ParamNames.PAY_CHANNEL);
		trade_type = it.getStringExtra(ParamNames.TRADE_TYPE);
		nonce_str = it.getStringExtra(ParamNames.NONCE_STR);
		app_name = it.getStringExtra(ParamNames.APP_NAME);
		bundle = it.getStringExtra(ParamNames.BUNDLE);
		detail = it.getStringExtra(ParamNames.DETAIL);
		out_trade_no = it.getStringExtra(ParamNames.OUT_TRADE_NO);
		total_fee = it.getStringExtra(ParamNames.TOTAL_FEE);
		notify_url = it.getStringExtra(ParamNames.NOTIFY_URL);
		
		
		TAGS.log("ProtoCashierActivity->mch_id: " + mch_id);
		TAGS.log("ProtoCashierActivity->pay_channel: " + pay_channel);
		TAGS.log("ProtoCashierActivity->trade_type: " + trade_type);
		TAGS.log("ProtoCashierActivity->nonce_str: " + nonce_str);
	}
	
	public void onAlipayPay(View v) {
		pay_channel = "alipay";
		pay(Pay.PAY_TYPE_ALIPAY);
	}
	
	public void onWechatPay(View v) {
		pay_channel = "wechat";
		pay(Pay.PAY_TYPE_WEIXIN);
	}
	
	private void pay(int payType) {
		TAGS.log("ProtoCashierActivity->pay: trade_type: " + trade_type);
		DYH5Activity.addCloseActivity(this);
		Pay pay = new Pay.Builder().buildListener(this).buildParam(mch_id, key, app_id, trade_type, nonce_str, app_name, bundle, detail, out_trade_no, total_fee, notify_url).build(payType);
		pay.pay(this);
	}

	@Override
	public void payStart(int payType) {
	}

	@Override
	public void payFinished(boolean isSuccess, String info) {
		if(mListener != null)
			mListener.payFinished(isSuccess, info);
	}

	@Override
	public void payBack(String out_trade_no) {
	}
}
