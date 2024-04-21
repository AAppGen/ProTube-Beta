package io.awesome.gagtube.util;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import io.awesome.gagtube.R;

public class GlideUtils {
	
	public static void loadAvatar(Context context, ImageView imageView, String imageUrl) {
		Glide.with(context).load(imageUrl)
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(R.drawable.placeholder_person)
				.error(R.drawable.placeholder_person)
				.fallback(R.drawable.placeholder_person)
				.into(imageView);
	}
	
	public static void loadThumbnail(Context context, ImageView imageView, String imageUrl) {
		Glide.with(context).load(imageUrl)
				.transition(withCrossFade(new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()))
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(R.drawable.no_image)
				.error(R.drawable.no_image)
				.fallback(R.drawable.no_image)
				.into(imageView);
	}
	
	public static void loadChannelBanner(Context context, ImageView imageView, String imageUrl) {
		Glide.with(context).load(imageUrl)
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(R.drawable.no_image)
				.error(R.drawable.no_image)
				.fallback(R.drawable.no_image)
				.into(imageView);
	}
}
