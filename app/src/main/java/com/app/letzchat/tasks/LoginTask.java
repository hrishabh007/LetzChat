package com.app.letzchat.tasks;

import android.app.ProgressDialog;
import android.content.Context;

import com.app.letzchat.R;
import com.app.letzchat.SmackInvocationException;
import com.app.letzchat.utils.AppLog;
import com.app.letzchat.utils.PreferenceUtils;
import com.app.letzchat.xmpp.SmackHelper;


public class LoginTask extends BaseAsyncTask<Void, Void, Boolean> {
	private String username;
	private String password;

	private ProgressDialog dialog;
	
	public LoginTask(Response.Listener<Boolean> listener, Context context, String username, String password) {
		super(listener, context);
		
		this.username = username;
		this.password = password;

		dialog = ProgressDialog.show(context, null, context.getResources().getString(R.string.login));
	}
	
	@Override
	public Response<Boolean> doInBackground(Void... params) {
		Context context = getContext();
		if (context != null) {
			try {
				SmackHelper smackHelper = SmackHelper.getInstance(context);
				
				smackHelper.login(username, password);

				PreferenceUtils.setLoginUser(context, username, password, smackHelper.getLoginUserNickname());
				//PreferenceUtils.AWS_SERVER_IP="192.168.2.15";
				return Response.success(true);
			} catch(SmackInvocationException e) {
				AppLog.e(String.format("login error %s", username), e);
				
				return Response.error(e);
			}
		} else {
			return null;
		}
	}

	@Override
	protected void onPostExecute(Response<Boolean> response) {
		dialog.dismiss();

		super.onPostExecute(response);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

		dismissDialog();
	}

	public void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public void dismissDialogAndCancel() {
		dismissDialog();
		cancel(false);
	}
}