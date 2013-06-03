package net.bajawa.batterypro;

import android.app.Activity;
import android.os.Bundle;

public class InfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.faq_title);
		setContentView(R.layout.main);
	}
}
