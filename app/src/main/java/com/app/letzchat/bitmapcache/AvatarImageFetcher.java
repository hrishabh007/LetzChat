package com.app.letzchat.bitmapcache;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;


import com.app.letzchat.R;
import com.app.letzchat.SmackInvocationException;
import com.app.letzchat.model.UserProfile;
import com.app.letzchat.utils.AppLog;
import com.app.letzchat.xmpp.SmackHelper;

import java.lang.ref.WeakReference;

public class AvatarImageFetcher {
	private Context context;
	private Resources resources;
	
	private ImageCache imageCache;
	private ImageCache.ImageCacheParams imageCacheParams;
	
	private Bitmap loadingBitmap;
	
	private int imageWidth;
	private int imageHeight;
	
	private static final int MESSAGE_CLEAR = 0;
    private static final int MESSAGE_INIT_DISK_CACHE = 1;
    private static final int MESSAGE_FLUSH = 2;
    private static final int MESSAGE_CLOSE = 3;
	
	public AvatarImageFetcher(Context context, int imageWidth, int imageHeight) {
		this.context = context;
		resources = context.getResources();
		setImageSize(imageWidth, imageHeight);
	}
	
	public AvatarImageFetcher(Context context, int imageSize) {
		this.context = context;
		resources = context.getResources();
		setImageSize(imageSize, imageSize);
	}
	
	public static AvatarImageFetcher getAvatarImageFetcher(Activity activity) {
		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(activity, ImageCache.AVATAR_DIR);
		AvatarImageFetcher imageFetcher = new AvatarImageFetcher(activity, activity.getResources().getDimensionPixelSize(R.dimen.default_avatar_size));
		imageFetcher.setLoadingImage(R.drawable.ic_default_avatar);
		imageFetcher.addImageCache(activity.getFragmentManager(), cacheParams);
		
		return imageFetcher;
	}
	
	private void setImageSize(int imageWidth, int imageHeight) {
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}
	
	public void setLoadingImage(int resId) {
		loadingBitmap = BitmapFactory.decodeResource(resources, resId);
    }
	
	public void addImageCache(FragmentManager fragmentManager, ImageCache.ImageCacheParams cacheParams) {
        imageCacheParams = cacheParams;
        imageCache = ImageCache.getInstance(fragmentManager, imageCacheParams);
        new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
    }
	
	public void loadImage(String jid, ImageView imageView) {
		if (jid == null) {
			return;
		}

		RoundedBitmapDrawable value = null;
		if (imageCache != null) {
			value = imageCache.getBitmapFromMemCache(jid);
		}
		
		if (value != null) {
			imageView.setImageDrawable(value);
		} else if (cancelPotentialWork(jid, imageView)) {
			BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(jid, imageView);
			AsyncDrawable drawable = new AsyncDrawable(resources, loadingBitmap, bitmapWorkerTask);
			imageView.setImageDrawable(drawable);
			bitmapWorkerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jid);
		}
	}
	
	private static boolean cancelPotentialWork(String jid, ImageView imageView) {
		BitmapWorkerTask task = getBitmapWorkerTask(imageView);
		if (task != null) {
			String taskJid = task.jid;
			if (taskJid == null || !taskJid.equals(jid)) {
				task.cancel(true);
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				return ((AsyncDrawable)drawable).getBitmapWorkerTask();
			}
		
		}
		return null;
	}
	
	public class BitmapWorkerTask extends AsyncTask<String, Void, Drawable> {
		WeakReference<ImageView> imageViewReference;
		String jid;
		
		public BitmapWorkerTask(String jid, ImageView imageView) {
			this.jid = jid;
			imageViewReference = new WeakReference<ImageView>(imageView);
		}
		
		@Override
		protected Drawable doInBackground(String... params) {
			Bitmap bitmap = null;
			RoundedBitmapDrawable drawable = null;
			
			if (imageCache != null && !isCancelled() && getAttachedImageView() != null) {
				bitmap = imageCache.getBitmapFromDiskCache(jid);
			}
			
			if (bitmap == null && !isCancelled() && getAttachedImageView() != null) {
				UserProfile user = null;
				try {
					user = SmackHelper.getInstance(context).search(jid);
				} catch (SmackInvocationException e) {
					AppLog.e(String.format("get user avatar error %s", jid), e);
				}
				
				if (user != null) {
					byte[] avatar = user.getAvatar();
					if (avatar != null) {
						bitmap = BitmapUtils.decodeSampledBitmapFromByteArray(avatar, imageWidth, imageHeight, imageCache);
					}
				}
			}
				
			if (bitmap != null) {
				if (imageCache != null) {
					drawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
					drawable.setCircular(true);
					imageCache.addBitmapToCache(jid, drawable);
				}
			}

			return drawable;
		}
		
		@Override
		protected void onPostExecute(Drawable drawable) {
			if (isCancelled()) {
				drawable = null;
			}
			
			ImageView imageView = getAttachedImageView();
			if (imageView != null && drawable != null) {
				imageView.setImageDrawable(drawable);
			}
		}
		
		/**
         * Returns the ImageView associated with this task as long as the ImageView's task still
         * points to this task as well. Returns null otherwise.
         */
        private ImageView getAttachedImageView() {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (this == bitmapWorkerTask) {
                return imageView;
            }

            return null;
        }
	}
	
	private static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;
		
		public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
	    }
	
	    public BitmapWorkerTask getBitmapWorkerTask() {
	    	return bitmapWorkerTaskReference.get();
	    }
	}
	
	protected class CacheAsyncTask extends AsyncTask<Object, Void, Void> {
		@Override
		protected Void doInBackground(Object... params) {
			switch ((Integer)params[0]) {
				case MESSAGE_CLEAR:
					clearCacheInternal();
		            break;
				
				case MESSAGE_INIT_DISK_CACHE:
		            initDiskCacheInternal();
		            break;
				
				case MESSAGE_FLUSH:
		            flushCacheInternal();
		            break;
				
				case MESSAGE_CLOSE:
		            closeCacheInternal();
		            break;
		    }
		    return null;
		}
	}

    protected void initDiskCacheInternal() {
    	if (imageCache != null) {
    		imageCache.initDiskCache();
        }
    }

    protected void clearCacheInternal() {}

    protected void flushCacheInternal() {
    	if (imageCache != null) {
    		imageCache.flush();
    	}
    }

    protected void closeCacheInternal() {
    	if (imageCache != null) {
    		imageCache.close();
    	}
    }

    public void clearCache() {
        new CacheAsyncTask().execute(MESSAGE_CLEAR);
    }

    public void flushCache() {
        new CacheAsyncTask().execute(MESSAGE_FLUSH);
    }

    public void closeCache() {
        new CacheAsyncTask().execute(MESSAGE_CLOSE);
    }
}