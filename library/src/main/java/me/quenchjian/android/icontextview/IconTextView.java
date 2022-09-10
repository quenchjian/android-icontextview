package me.quenchjian.android.icontextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
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
  private boolean isRelative;

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
    int size = a.getDimensionPixelSize(R.styleable.IconTextView_iconSize, 0);
    if (size == 0) {
      iconWidth = a.getDimensionPixelSize(R.styleable.IconTextView_iconWidth, 0);
      iconHeight = a.getDimensionPixelSize(R.styleable.IconTextView_iconHeight, 0);
    } else {
      iconWidth = size;
      iconHeight = size;
    }
    a.recycle();
    if (iconWidth > 0 || iconHeight > 0) {
      if (isRelative) {
        setCompoundDrawablesRelative(getStartIcon(), getTopIcon(), getEndIcon(), getBottomIcon());
      } else {
        setCompoundDrawables(getLeftIcon(), getTopIcon(), getRightIcon(), getBottomIcon());
      }
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
  public Drawable getStartIcon() {
    return getCompoundDrawablesRelative()[0];
  }

  /**
   * Set start icon
   *
   * @param drawable icon to set
   */
  public void setStartIcon(@Nullable Drawable drawable) {
    setCompoundDrawablesRelative(drawable, getTopIcon(), getEndIcon(), getBottomIcon());
  }

  /**
   * Set start icon
   *
   * @param res icon resource id to set
   */
  public void setStartIcon(@DrawableRes int res) {
    setStartIcon(ContextCompat.getDrawable(getContext(), res));
  }

  @Nullable
  public Drawable getTopIcon() {
    return getCompoundDrawablesRelative()[1];
  }

  /**
   * Set top icon
   *
   * @param drawable icon to set
   */
  public void setTopIcon(@Nullable Drawable drawable) {
    setCompoundDrawablesRelative(getStartIcon(), drawable, getEndIcon(), getBottomIcon());
  }

  /**
   * Set top icon
   *
   * @param res icon resource id to set
   */
  public void setTopIcon(@DrawableRes int res) {
    setTopIcon(ContextCompat.getDrawable(getContext(), res));
  }

  @Nullable
  public Drawable getEndIcon() {
    return getCompoundDrawablesRelative()[2];
  }

  /**
   * Set end icon
   *
   * @param drawable icon to set
   */
  public void setEndIcon(@Nullable Drawable drawable) {
    setCompoundDrawablesRelative(getStartIcon(), getTopIcon(), drawable, getBottomIcon());
  }

  /**
   * Set end icon
   *
   * @param res icon resource id to set
   */
  public void setEndIcon(@DrawableRes int res) {
    setEndIcon(ContextCompat.getDrawable(getContext(), res));
  }

  @Nullable
  public Drawable getBottomIcon() {
    return getCompoundDrawablesRelative()[3];
  }

  /**
   * Set bottom icon
   *
   * @param drawable icon to set
   */
  public void setBottomIcon(@Nullable Drawable drawable) {
    setCompoundDrawablesRelative(getStartIcon(), getTopIcon(), getEndIcon(), drawable);
  }

  /**
   * Set bottom icon
   *
   * @param res icon resource id to set
   */
  public void setBottomIcon(@DrawableRes int res) {
    setBottomIcon(ContextCompat.getDrawable(getContext(), res));
  }

  @Override
  public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top,
      @Nullable Drawable right, @Nullable Drawable bottom) {
    isRelative = false;
    super.setCompoundDrawables(
        Drawables.scale(Drawables.wrap(left), iconWidth, iconHeight),
        Drawables.scale(Drawables.wrap(top), iconWidth, iconHeight),
        Drawables.scale(Drawables.wrap(right), iconWidth, iconHeight),
        Drawables.scale(Drawables.wrap(bottom), iconWidth, iconHeight));
  }

  @Override
  public void setCompoundDrawablesRelative(@Nullable Drawable start, @Nullable Drawable top,
      @Nullable Drawable end, @Nullable Drawable bottom) {
    isRelative = start != null || top != null || end != null || bottom != null;
    super.setCompoundDrawablesRelative(
        Drawables.scale(Drawables.wrap(start), iconWidth, iconHeight),
        Drawables.scale(Drawables.wrap(top), iconWidth, iconHeight),
        Drawables.scale(Drawables.wrap(end), iconWidth, iconHeight),
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
        - getPaddingStart()
        - drawableWidth
        - getCompoundDrawablePadding()) / 2;
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

  @Nullable
  private Drawable getLeftIcon() {
    return getCompoundDrawables()[0];
  }

  @Nullable
  private Drawable getRightIcon() {
    return getCompoundDrawables()[2];
  }
}
