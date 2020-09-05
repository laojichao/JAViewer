// Generated code from Butter Knife. Do not modify!
package io.github.javiewer.adapter;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import io.github.javiewer.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MovieHeaderAdapter$ViewHolder_ViewBinding implements Unbinder {
  private MovieHeaderAdapter.ViewHolder target;

  @UiThread
  public MovieHeaderAdapter$ViewHolder_ViewBinding(MovieHeaderAdapter.ViewHolder target,
      View source) {
    this.target = target;

    target.mHeaderName = Utils.findRequiredViewAsType(source, R.id.header_name, "field 'mHeaderName'", TextView.class);
    target.mHeaderValue = Utils.findRequiredViewAsType(source, R.id.header_value, "field 'mHeaderValue'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MovieHeaderAdapter.ViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mHeaderName = null;
    target.mHeaderValue = null;
  }
}
