package com.honeybadger.views;

import com.honeybadger.R;
import com.honeybadger.api.scripts.ReturnOutput;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

public class ViewRawRulesActivity extends Activity
{
	SharedPreferences settings;
	SharedPreferences.Editor editor;

	String ruleText;

	TextView rules;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.raw_rules);
		rules = (TextView) findViewById(R.id.ruleText);
		rules.setText("Loading");

		settings = getSharedPreferences("main", 0);
		editor = settings.edit();

		class GetRules extends AsyncTask<Integer, Integer, Integer>
		{
			protected Integer doInBackground(Integer... integers)
			{
				String scriptText = ViewRawRulesActivity.this.getDir("bin", 0)
						+ "/iptables -L -n -v";

				Intent script = new Intent(ViewRawRulesActivity.this, ReturnOutput.class);
				script.putExtra("script", scriptText);
				startService(script);

				ruleText = "";
				ruleText = settings.getString("tempText", "not ready");

				while (ruleText.contains("not ready"))
				{
					ruleText = settings.getString("tempText", "not ready");
				}

				editor.remove("tempText");
				editor.commit();

				ViewRawRulesActivity.this.runOnUiThread(new Runnable()
				{
					public void run()
					{
						new Handler().postDelayed(new Runnable()
						{
							public void run()
							{
								rules.setText(ruleText);
							}
						}, 100);
					}

				});

				return 0;
			}

			protected void onPreExecute()
			{
				setProgressBarIndeterminateVisibility(true);
			}

			protected void onPostExecute(Integer result)
			{
				setProgressBarIndeterminateVisibility(false);
			}

		}

		new GetRules().execute();

	}
}
