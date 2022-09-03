package me.quenchjian.android.icontextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Size;
import android.view.Gravity;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import java.util.Objects;

import me.quenchjian.android.icontextview.utils.Drawables;

/**
 * Enhanced TextView that can set compound drawable size.
 */
public class IconTextView extends AppCompatTextView {

  private int cachedPosition = 0;
  private int iconWidth;
  private int iconHeight;

  public IconTextView(@NonNull Context context) {
    this(context, null);
  }

  public IconTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, android.R.attr.textViewStyle);
  }

  public IconTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    if (attrs == null) return;
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconTextView);
    int size = a.getDimensionPixelSize(R.styleable.IconTextView_iconSize, -1);
    if (size == -1) {
      iconWidth = a.getDimensionPixelSize(R.styleable.IconTextView_iconWidth, -1);
      iconHeight = a.getDimensionPixelSize(R.styleable.IconTextView_iconHeight, -1);
    } else {
      iconWidth = size;
      iconHeight = size;
    }
    a.recycle();
    if (iconWidth > 0 || iconHeight > 0) {
      setCompoundDrawables(getLeftIcon(), getTopIcon(), getRightIcon(), getBottomIcon());
    }
  }

  public int getIconWidth() {
    return iconWidth;
  }

  public int getIconHeight() {
    return iconHeight;
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public Size getIconSize() {
    return new Size(iconWidth, iconHeight);
  }

  public void setIconSize(int size) {
    setIconSize(size, size);
  }

  public void setIconSize(int width, int height) {
    if (width < 0 || height < 0) {
      throw new IllegalArgumentException(String.format("invalid width(%s) or height(%s).", width, height));
    }
    if (iconWidth == width && iconHeight == height) {
      return;
    }
    iconWidth = width;
    iconHeight = height;
    requestLayout();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public void setIconSize(Size size) {
    Objects.requireNonNull(size);
    setIconSize(size.getWidth(), size.getHeight());
  }

  @Nullable
  public Drawable getLeftIcon() {
    Drawable left = getCompoundDrawables()[0];
    return left != null ? left : getCompoundDrawablesRelative()[0];
  }

  public void setLeftIcon(@Nullable Drawable drawable) {
    setCompoundDrawables(drawable, null, null, null);
  }

  public void setLeftIcon(@DrawableRes int res) {
    setCompoundDrawables(res, -1, -1, -1);
  }

  @Nullable
  public Drawable getTopIcon() {
    Drawable top = getCompoundDrawables()[1];
    return top != null ? top : getCompoundDrawablesRelative()[1];
  }

  public void setTopIcon(@Nullable Drawable drawable) {
    setCompoundDrawables(null, drawable, null, null);
  }

  public void setTopIcon(@DrawableRes int res) {
    setCompoundDrawables(-1, res, -1, -1);
  }

  @Nullable
  public Drawable getRightIcon() {
    Drawable right = getCompoundDrawables()[2];
    return right != null ? right : getCompoundDrawablesRelative()[2];
  }

  public void setRightIcon(@Nullable Drawable drawable) {
    setCompoundDrawables(null, null, drawable, null);
  }

  public void setRightIcon(@DrawableRes int res) {
    setCompoundDrawables(-1, -1, res, -1);
  }

  @Nullable
  public Drawable getBottomIcon() {
    Drawable bottom = getCompoundDrawables()[3];
    return bottom != null ? bottom : getCompoundDrawablesRelative()[3];
  }

  public void setBottomIcon(@Nullable Drawable drawable) {
    setCompoundDrawables(null, null, null, drawable);
  }

  public void setBottomIcon(@DrawableRes int res) {
    setCompoundDrawables(-1, -1, -1, res);
  }

  public void setCompoundDrawables(@DrawableRes int left, @DrawableRes int top,
      @DrawableRes int right, @DrawableRes int bottom) {
    setCompoundDrawables(
        left == -1 ? null : ContextCompat.getDrawable(getContext(), left),
        top == -1 ? null : ContextCompat.getDrawable(getContext(), top),
        right == -1 ? null : ContextCompat.getDrawable(getContext(), right),
        bottom == -1 ? null : ContextCompat.getDrawable(getContext(), bottom));
  }

  @Override
  public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top,
      @Nullable Drawable right, @Nullable Drawable bottom) {
    super.setCompoundDrawables(
        Drawables.scale(Drawables.wrap(left), iconWidth, iconHeight),
        Drawables.scale(Drawables.wrap(top), iconWidth, iconHeight),
        Drawables.scale(Drawables.wrap(right), iconWidth, iconHeight),
        Drawables.scale(Drawables.wrap(bottom), iconWidth, iconHeight));
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    if ((getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK) != Gravity.CENTER_HORIZONTAL) {
      return;
    }
    Paint textPaint = getPaint();
    String text = getText().toString();
    if (getTransformationMethod() != null) {
      // if text is transformed, add that transformation to to ensure correct calculation of drawable padding.
      text = getTransformationMethod().getTransformation(text, this).toString();
    }
    int textWidth = Math.min((int) textPaint.measureText(text), getLayout().getWidth());
    int drawableWidth = Drawables.getWidth(getLeftIcon()) + Drawables.getWidth(getRightIcon());
    int position = (getMeasuredWidth()
        - textWidth
        - getPaddingEnd()
        - drawableWidth
        - getCompoundDrawablePadding()
        - getPaddingStart()) / 2;
    if (cachedPosition != position) {
      cachedPosition = position;
      Drawable drawable;
      if ((drawable = getLeftIcon()) != null) {
        drawable.setBounds(position, 0, position + Drawables.getWidth(drawable), Drawables.getHeight(drawable));
      }
      if ((drawable = getRightIcon()) != null) {
        drawable.setBounds(-position, 0, -position + Drawables.getWidth(drawable), Drawables.getHeight(drawable));
      }
    }
  }
}
