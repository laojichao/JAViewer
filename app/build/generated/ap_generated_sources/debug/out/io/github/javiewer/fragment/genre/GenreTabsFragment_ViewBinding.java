// Generated code from Butter Knife. Do not modify!
package io.github.javiewer.fragment.genre;

import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.viewpager.widget.ViewPager;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.google.android.material.tabs.TabLayout;
import io.github.javiewer.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class GenreTabsFragment_ViewBinding implements Unbinder {
  private GenreTabsFragment target;

  @UiThread
  public GenreTabsFragment_ViewBinding(GenreTabsFragment target, View source) {
    this.target = target;

    target.mTabLayout = Utils.findRequiredViewAsType(source, R.id.genre_tabs, "field 'mTabLayout'", TabLayout.class);
    target.mViewPager = Utils.findRequiredViewAsType(source, R.id.genre_view_pager, "field 'mViewPager'", ViewPager.class);
    target.mProgressBar = Utils.findRequiredViewAsType(source, R.id.genre_progress_bar, "field 'mProgressBar'", ProgressBar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    GenreTabsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTabLayout = null;
    target.mViewPager = null;
    target.mProgressBar = null;
  }
}
