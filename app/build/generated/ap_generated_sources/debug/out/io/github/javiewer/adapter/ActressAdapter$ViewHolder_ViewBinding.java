// Generated code from Butter Knife. Do not modify!
package io.github.javiewer.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import io.github.javiewer.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ActressAdapter$ViewHolder_ViewBinding implements Unbinder {
  private ActressAdapter.ViewHolder target;

  @UiThread
  public ActressAdapter$ViewHolder_ViewBinding(ActressAdapter.ViewHolder target, View source) {
    this.target = target;

    target.mTextName = Utils.findRequiredViewAsType(source, R.id.actress_name, "field 'mTextName'", TextView.class);
    target.mImage = Utils.findRequiredViewAsType(source, R.id.actress_img, "field 'mImage'", ImageView.class);
    target.mLayout = Utils.findRequiredView(source, R.id.layout_actress, "field 'mLayout'");
  }

  @Override
  @CallSuper
  public void unbind() {
    ActressAdapter.ViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTextName = null;
    target.mImage = null;
    target.mLayout = null;
  }
}
