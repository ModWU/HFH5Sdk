package com.example.hfh5sdk;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import cn.okpay.android.sdkhelper.TAGS;
import cn.okpay.android.sdkhelper.Util;
import cn.okpay.android.sdkpay.IPayListener;
import cn.okpay.android.sdkpay.Pay;

public class MainActivity extends Activity implements IPayListener {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void onWechatPay(View v) {
		
		String mch_id = "EEJ9TKS1V0VZD72";
		String key = "20561a19m721t52";
		String app_id = "201709012";
		String pay_channel = "wechat";
		String trade_type = "wap";
		String nonce_str = Util.getRandomString(13);
		
		String out_trade_no = Util.md5("fdfd" + System.currentTimeMillis())
				.substring(1, 11);
		
		Pay pay = new Pay.Builder().buildListener(this).buildParam(mch_id, key, app_id, trade_type, nonce_str, "aa", "bb", "cc", out_trade_no, "1", "43").build(Pay.PAY_TYPE_WEIXIN);
		pay.pay(this);
	}
	
	public void onAlipayPay(View v) {
		String mch_id = "EEJ9TKS1V0VZD72";
		String key = "20561a19m721t52";
		String app_id = "201709012";
		String pay_channel = "alipay";
		String trade_type = "wap";
		String nonce_str = Util.getRandomString(13);
		
		String out_trade_no = Util.md5("fdfd" + System.currentTimeMillis())
				.substring(1, 11);
		
		Pay pay = new Pay.Builder().buildListener(this).buildParam(mch_id, key, app_id, trade_type, nonce_str, "a43a", "bbfad", "c12c", out_trade_no, "1", "43f3").build(Pay.PAY_TYPE_ALIPAY);
		pay.pay(this);
	}

	
	//cashier的方式时，pay_channel都传cashier
	public void onWapCashierPay(View v) {
		String mch_id = "EEJ9TKS1V0VZD72";
		String key = "20561a19m721t52";
		String app_id = "201709012";
		String pay_channel = "cashier";
		String trade_type = "wap";
		String nonce_str = Util.getRandomString(13);
		
		String out_trade_no = Util.md5("fdfd" + System.currentTimeMillis())
				.substring(1, 11);
		
		Pay pay = new Pay.Builder().buildListener(this).buildParam(mch_id, key, app_id, trade_type, nonce_str, "a43a", "bbfad", "c12c", out_trade_no, "1", "43f3").build(Pay.PAY_TYPE_WAP_CASHIER);
		pay.pay(this);
	}
	
	public void onProtoCashierPay(View v) {
		String mch_id = "EEJ9TKS1V0VZD72";
		String key = "20561a19m721t52";
		String app_id = "201709012";
		String pay_channel = "cashier";
		String trade_type = "wap";
		String nonce_str = Util.getRandomString(13);
		
		String out_trade_no = Util.md5("fdfd" + System.currentTimeMillis())
				.substring(1, 11);
		
		Pay pay = new Pay.Builder().buildListener(this).buildParam(mch_id, key, app_id, trade_type, nonce_str, "a43a", "bbfad", "c12c", out_trade_no, "1", "43f3").build(Pay.PAY_TYPE_PROTO_CASHIER);
		pay.pay(this);
	}

	@Override
	public void payStart(int payType) {
		
	}

	@Override
	public void payFinished(boolean isSuccess, String info) {
		Toast.makeText(this, "isSuccess: " + isSuccess + ", info: " + info, Toast.LENGTH_LONG).show();
		TAGS.log("payFinished...");
	}

	@Override
	public void payBack(String out_trade_no) {
		TAGS.log("payBack->out_trade_no: " + out_trade_no);
	}

}
