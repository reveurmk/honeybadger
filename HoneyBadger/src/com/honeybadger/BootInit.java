package com.honeybadger;

/*--------------------------------------------------------------------------------------------------------------------------------
 * Version: 4.5
 * Date of last modification: 11SEP13
 *
 * Edit 2.1: Loads app rules
 * Edit 4.0: Clean up
 * Edit 4.5: Revamp of database interaction
 *--------------------------------------------------------------------------------------------------------------------------------
 */

import com.honeybadger.api.AppBlocker;
import com.honeybadger.api.Blocker;
import com.honeybadger.api.SharedMethods;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootInit extends BroadcastReceiver
{
	SharedPreferences settings;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String startScript = "";

		settings = context.getSharedPreferences("main", 1);

		startScript = SharedMethods.initialString(startScript, context);

		startScript = SharedMethods.setLogging(startScript, settings, context);

		startScript = SharedMethods.setBlock(startScript, settings, context);

		// Launch Script
		SharedMethods.execScript(startScript);

		// reload rules
		Intent reload = new Intent(context, Blocker.class);
		reload.putExtra("reload", "true");
		context.startService(reload);

		// reload app rules
		Intent reloadApps = new Intent(context, AppBlocker.class);
		context.startService(reloadApps);

		// reload auto-generated rules if specified
		if (settings.getBoolean("generate", false) | settings.getBoolean("autoUpdate", false))
		{
			SharedMethods.fetch(context);
		}

		SharedMethods.loadApps(context, settings);
	}

}
