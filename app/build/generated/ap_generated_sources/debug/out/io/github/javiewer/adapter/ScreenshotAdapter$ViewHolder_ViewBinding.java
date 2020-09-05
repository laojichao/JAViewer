// Generated code from Butter Knife. Do not modify!
package io.github.javiewer.adapter;

import android.view.View;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import io.github.javiewer.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ScreenshotAdapter$ViewHolder_ViewBinding implements Unbinder {
  private ScreenshotAdapter.ViewHolder target;

  @UiThread
  public ScreenshotAdapter$ViewHolder_ViewBinding(ScreenshotAdapter.ViewHolder target,
      View source) {
    this.target = target;

    target.mImage = Utils.findRequiredViewAsType(source, R.id.screenshot_image_view, "field 'mImage'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ScreenshotAdapter.ViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mImage = null;
  }
}
