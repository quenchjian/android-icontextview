package me.quenchjian.android.icontextview.utils;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Size;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.graphics.drawable.DrawableCompat;

public final class Drawables {

  private static final Rect ZERO_BOUNDS = new Rect();

  @Nullable
  public static Drawable wrap(@Nullable Drawable drawable) {
    if (drawable == null) return null;
    if (drawable.getBounds() == ZERO_BOUNDS) {
      drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }
    return DrawableCompat.wrap(drawable);
  }

  @Nullable
  public static Drawable scale(@Nullable Drawable drawable, int targetWidth, int targetHeight) {
    if (drawable == null) return null;
    if (targetWidth < 0 && targetHeight < 0) return drawable;

    Rect bounds = drawable.getBounds();
    int w = bounds.width();
    int h = bounds.height();
    float scale = h * 1f / w;
    if (w != targetWidth) {
      w = targetWidth;
      h = (int) (w * scale);
    }
    if (h != targetHeight) {
      h = targetHeight;
      w = (int) (h / scale);
    }
    bounds.right = (bounds.left + w);
    bounds.bottom = (bounds.top + h);
    drawable.setBounds(bounds);
    return drawable;
  }

  public static int getWidth(Drawable drawable) {
    return drawable == null
        ? 0 : drawable.getBounds() != ZERO_BOUNDS
        ? drawable.getBounds().width() : drawable.getIntrinsicWidth();
  }

  public static int getHeight(Drawable drawable) {
    return drawable == null
        ? 0 : drawable.getBounds() != ZERO_BOUNDS
        ? drawable.getBounds().height() : drawable.getIntrinsicHeight();
  }

  private Drawables() {
    throw new RuntimeException("prevent reflection");
  }
}
