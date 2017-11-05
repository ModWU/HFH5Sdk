package cn.okpay.android.sdkhelper;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import cn.okpay.android.sdkpay.IPayNotify;
import cn.okpay.android.sdkpay.Pay;

public class PayLoadingActivity extends Activity implements IPayNotify {
	
	
	private static Pay mPay;
	
	public static void setPay(Pay pay) {
		mPay = pay;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().getDecorView().setBackgroundColor(Color.parseColor("#80000000"));
		
		setContentView(ResourceUtils.getLayoutId(this, "dian_loading_layout"));
		
		startPayTask();
	}

	



	private void startPayTask() {
		if(mPay != null)
			mPay.new PayTask(this, this).execute();
		else 
		{
			Toast.makeText(this, "Ö§¸¶Òì³£!", Toast.LENGTH_SHORT).show();
			TAGS.log("PayLoadingActivity-->startPayTask->mPay cannot be null.");
			finish();
		}
			
	}
	
	@Override
	public void onBackPressed() {
	}
	

	@Override
	public void payBefore() {
		
	}

	
	
	@Override
	public void payAfter() {
		mPay = null;
		finish();
	}
	
	
}
