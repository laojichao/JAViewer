// Generated code from Butter Knife. Do not modify!
package io.github.javiewer.activity;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.google.android.material.tabs.TabLayout;
import io.github.javiewer.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class DownloadActivity_ViewBinding implements Unbinder {
  private DownloadActivity target;

  @UiThread
  public DownloadActivity_ViewBinding(DownloadActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public DownloadActivity_ViewBinding(DownloadActivity target, View source) {
    this.target = target;

    target.mToolbar = Utils.findRequiredViewAsType(source, R.id.download_toolbar, "field 'mToolbar'", Toolbar.class);
    target.mTabLayout = Utils.findRequiredViewAsType(source, R.id.download_tabs, "field 'mTabLayout'", TabLayout.class);
    target.mViewPager = Utils.findRequiredViewAsType(source, R.id.download_view_pager, "field 'mViewPager'", ViewPager.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    DownloadActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mToolbar = null;
    target.mTabLayout = null;
    target.mViewPager = null;
  }
}
