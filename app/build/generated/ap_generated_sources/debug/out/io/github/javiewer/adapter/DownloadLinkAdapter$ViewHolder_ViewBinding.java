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

public class DownloadLinkAdapter$ViewHolder_ViewBinding implements Unbinder {
  private DownloadLinkAdapter.ViewHolder target;

  @UiThread
  public DownloadLinkAdapter$ViewHolder_ViewBinding(DownloadLinkAdapter.ViewHolder target,
      View source) {
    this.target = target;

    target.mTextTitle = Utils.findRequiredViewAsType(source, R.id.download_title, "field 'mTextTitle'", TextView.class);
    target.mTextSize = Utils.findRequiredViewAsType(source, R.id.download_size, "field 'mTextSize'", TextView.class);
    target.mTextDate = Utils.findRequiredViewAsType(source, R.id.download_date, "field 'mTextDate'", TextView.class);
    target.mView = Utils.findRequiredView(source, R.id.layout_download, "field 'mView'");
  }

  @Override
  @CallSuper
  public void unbind() {
    DownloadLinkAdapter.ViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTextTitle = null;
    target.mTextSize = null;
    target.mTextDate = null;
    target.mView = null;
  }
}
