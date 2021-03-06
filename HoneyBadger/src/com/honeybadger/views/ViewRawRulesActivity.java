package com.honeybadger.views;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.honeybadger.R;
import com.honeybadger.api.SharedMethods;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ViewRawRulesActivity extends SherlockFragmentActivity
{
	SharedPreferences settings;
	SharedPreferences.Editor editor;

	String ruleText;

	TextView rules;
	Button showMoreButton;
	Button goBackButton;

	int lineNum = 0;

	ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		settings = getSharedPreferences("main", 0);

		new GetRules().execute();
	}

	class GetRules extends AsyncTask<Integer, Integer, Integer>
	{
		protected Integer doInBackground(Integer... integers)
		{
			String scriptText = ViewRawRulesActivity.this.getDir("bin", 0)
					+ "/iptables -L -n -v | busybox cut -d \"\n\" -f" + lineNum + "-" + (lineNum + 12);

			ruleText = SharedMethods.execScript(scriptText);

			ViewRawRulesActivity.this.runOnUiThread(new Runnable()
			{
				public void run()
				{
					new Handler().postDelayed(new Runnable()
					{
						public void run()
						{
							setContentView(R.layout.view_raw_rules);

							goBackButton = (Button) findViewById(R.id.go_back);
							if (lineNum == 0)
							{
								goBackButton.setVisibility(View.GONE);
							}
							goBackButton.setOnClickListener(new OnClickListener()
							{
								public void onClick(View v)
								{
									lineNum -= 13;
									new GetRules().execute();
								}
							});

							showMoreButton = (Button) findViewById(R.id.show_more);
							showMoreButton.setOnClickListener(new OnClickListener()
							{
								public void onClick(View v)
								{
									lineNum += 13;
									goBackButton.setVisibility(View.VISIBLE);
									new GetRules().execute();
								}
							});

							rules = (TextView) findViewById(R.id.ruleText);
							rules.setText(ruleText);
						}
					}, 1);
				}
			});

			return 0;
		}

		protected void onPreExecute()
		{
			dialog = ProgressDialog.show(ViewRawRulesActivity.this, "", "Loading");
		}

		protected void onPostExecute(Integer result)
		{
			dialog.dismiss();
		}

	}
}
