package com.warmice.android.videomessaging.ui.widget;

import com.warmice.android.videomessaging.R;
import com.warmice.android.videomessaging.file.image.Image.ImageLoadedListener;
import com.warmice.android.videomessaging.file.image.ResourceImage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class CropImageView extends ViewGroup implements ImageLoadedListener {

	final int photoHeight = 256;
	final int photoWidth = 256;

	Selector mSelector;
	Bitmap mBitmap;

	public CropImageView(Context context) {
		super(context);
	}

	public CropImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CropImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupSelector(context);
	}

	private void setupSelector(Context context) {
		mSelector = new Selector(context);
		mSelector.setBackgroundResource(R.drawable.selector);
		this.addView(mSelector);
	}

	/**
	 * Returns a set of layout parameters with a width of
	 * {@link android.view.ViewGroup.LayoutParams#FILL_PARENT}, and a height of
	 * {@link android.view.ViewGroup.LayoutParams#FILL_PARENT}.
	 */
	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int count = getChildCount();

		Drawable background = getBackground();
		int backgroundHeight = 0;
		int backgroundWidth = 0;
		int width = 0;
		int height = 0;
		
		if (background != null){
			backgroundHeight = background.getIntrinsicHeight();
			backgroundWidth = background.getIntrinsicWidth();
			width = MeasureSpec.getSize(widthMeasureSpec);
			height = MeasureSpec.getSize(heightMeasureSpec);
			
		}

		if (backgroundHeight > 0 && backgroundWidth > 0 && height > 0
				&& width > 0) {

			int heightRatio = backgroundHeight / height;
			int widthRatio = backgroundWidth / width;

			if (heightRatio < widthRatio) {
				height = width * backgroundHeight / backgroundWidth;
			} else if (heightRatio > widthRatio) {
				width = height * backgroundWidth / backgroundHeight;
			} else {
				if (width < height) {
					height = width * backgroundHeight / backgroundWidth;
				} else {
					width = height * backgroundWidth / backgroundHeight;
				}
			}

			widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,
					MeasureSpec.getMode(widthMeasureSpec));
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
					MeasureSpec.getMode(heightMeasureSpec));

			for (int i = 0; i < count; i++) {
				final View child = getChildAt(i);
				if (child.getVisibility() != GONE) {
					measureChildWithMargins(child, widthMeasureSpec, 0,
							heightMeasureSpec, 0);
				}
			}

			setMeasuredDimension(width, height);
		} else {
			setMeasuredDimension(0, 0);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		final int paddingLeft = getPaddingLeft();
		final int paddingTop = getPaddingTop();
		final int parentLeft = paddingLeft;
		final int parentTop = paddingTop;

		final int width = mSelector.getMeasuredWidth();
		final int height = mSelector.getMeasuredHeight();

		mSelector.calculateOffsets(parentLeft, parentTop, parentLeft + width, parentTop + height);
		final int horizontalOffset = mSelector.getHorizontalOffset();
		final int verticalOffset = mSelector.getVerticalOffset();

		int childLeft = parentLeft + horizontalOffset;
		int childTop = parentTop + verticalOffset;

		mSelector.layout(childLeft, childTop, childLeft + width, childTop
				+ height);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			mSelector.setCenter(event.getX(), event.getY());
			requestLayout();
			return true;
		case MotionEvent.ACTION_MOVE:
			mSelector.setCenter(event.getX(), event.getY());
			requestLayout();
			return true;
		case MotionEvent.ACTION_UP:
			return true;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new CropImageView.LayoutParams(getContext(), attrs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof LayoutParams;
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(
			ViewGroup.LayoutParams p) {
		return new LayoutParams(p);
	}

	public void setBitmap(Context context, Uri imageUri) {
		ResourceImage image = new ResourceImage(context);
		image.setDimens(photoWidth, photoHeight);
		image.load(this, 0, imageUri);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onImageLoaded(int imageId, Boolean succeeded, Bitmap bitmap) {
		if (succeeded) {
			mBitmap = bitmap;
			Drawable image = new BitmapDrawable(getResources(), mBitmap);
			if (Build.VERSION.SDK_INT < 16) {
				setBackgroundDrawable(image);
			} else {
				setBackground(image);
			}
		}
	}

	public Bitmap getCroppedBitmap() {
		int bitmapWidth = mBitmap.getWidth();
		int bitmapHeight = mBitmap.getHeight();
		int selectorWidth = mSelector.mHorizontalBound;
		int selectorHeight = mSelector.mVerticalBound;
		int x = mSelector.x * bitmapWidth / selectorWidth;
		int y = mSelector.y * bitmapHeight / selectorHeight;
		int length = Math.min(bitmapWidth, bitmapHeight);
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		Bitmap bitmap = Bitmap.createBitmap(mBitmap, x, y, length, length);
		bitmap = Bitmap.createScaledBitmap(bitmap, photoWidth, photoHeight,
				true);
		return bitmap;
	}

	public static class LayoutParams extends MarginLayoutParams {

		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
		}

		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(int width, int height) {
			super(width, height);
		}

		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}

		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(ViewGroup.MarginLayoutParams source) {
			super(source);
		}
	}

	protected class Selector extends View {

		int x = 0;
		int y = 0;

		int mLength;
		int mHorizontalBound;
		int mVerticalBound;

		public Selector(Context context) {
			super(context);
		}

		public void setCenter(Float x, Float y) {
			if (mLength != 0) {
				int radius = mLength / 2;
				this.x = x.intValue() - radius;
				this.y = y.intValue() - radius;
			} else {
				this.x = x.intValue();
				this.y = y.intValue();
			}
		}

		public void calculateOffsets(int left, int top, int right, int bottom) {
			setHorizontalAdjustment(left, right);
			setVerticalAdjustment(top, bottom);
		}

		public int getHorizontalOffset() {
			return x;
		}

		public int getVerticalOffset() {
			return y;
		}

		private void setHorizontalAdjustment(int l, int r) {
			int right = (r + x);
			int left = (l + x);

			if (left < 0) {
				x -= left;
			} else {
				if (right > mHorizontalBound) {
					int difference = right - mHorizontalBound;
					x -= difference;
				}
			}
		}

		public void setVerticalAdjustment(int t, int b) {
			int bottom = (t + y);
			int top = (b + y);

			if (bottom < 0) {
				y -= bottom;
			} else {
				if (top > mVerticalBound) {
					int difference = top - mVerticalBound;
					y -= difference;
				}
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			mHorizontalBound = MeasureSpec.getSize(widthMeasureSpec);
			mVerticalBound = MeasureSpec.getSize(heightMeasureSpec);
			int length;
			if (Math.min(mHorizontalBound, mVerticalBound) == mHorizontalBound) {
				length = mHorizontalBound;
			} else {
				length = mVerticalBound;
			}

			mLength = MeasureSpec.getSize(length);
			setMeasuredDimension(length, length);
		}

	}
}