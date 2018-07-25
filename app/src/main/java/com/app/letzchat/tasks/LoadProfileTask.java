package com.app.letzchat.tasks;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import android.content.Context;
import android.graphics.Bitmap;

import com.app.letzchat.SmackInvocationException;
import com.app.letzchat.bitmapcache.BitmapUtils;
import com.app.letzchat.bitmapcache.ImageCache;
import com.app.letzchat.model.LoginUserProfile;
import com.app.letzchat.utils.PreferenceUtils;
import com.app.letzchat.xmpp.SmackHelper;


public class LoadProfileTask extends BaseAsyncTask<Void, Void, LoginUserProfile> {
	public LoadProfileTask(Response.Listener<LoginUserProfile> listener, Context context) {
		super(listener, context);
	}
	
	@Override
	protected Response<LoginUserProfile> doInBackground(Void... params) {
		Context context = getContext();
		if (context != null) {
			try {
				String user = PreferenceUtils.getUser(context);
				
				// first check cache file to find avatar, and if not existing, load vcard from server
				Bitmap avatar = ImageCache.getAvatarFromFile(context, user);
				if (avatar == null) {
					VCard vcard = SmackHelper.getInstance(context).loadVCard();
					if (vcard != null) {
						byte[] data = vcard.getAvatar();
						if (data != null) {
							avatar = BitmapUtils.decodeSampledBitmapFromByteArray(data, Integer.MAX_VALUE, Integer.MAX_VALUE, null);
						}
					}
					
					if (avatar != null) {
						ImageCache.addAvatarToFile(context, user, avatar);
					}
				}
				
				LoginUserProfile result = new LoginUserProfile();
				result.setAvatar(avatar);
				result.setNickname(PreferenceUtils.getNickname(context));
				
				return Response.success(result);
			} catch (SmackInvocationException e) {
				return Response.error(e);
			}
		}
		
		return null;
	}
}
