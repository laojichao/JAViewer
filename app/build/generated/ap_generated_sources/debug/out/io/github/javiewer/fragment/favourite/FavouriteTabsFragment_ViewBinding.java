// Generated code from Butter Knife. Do not modify!
package io.github.javiewer.fragment.favourite;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.viewpager.widget.ViewPager;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.google.android.material.tabs.TabLayout;
import io.github.javiewer.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class FavouriteTabsFragment_ViewBinding implements Unbinder {
  private FavouriteTabsFragment target;

  @UiThread
  public FavouriteTabsFragment_ViewBinding(FavouriteTabsFragment target, View source) {
    this.target = target;

    target.mTabLayout = Utils.findRequiredViewAsType(source, R.id.favourite_tabs, "field 'mTabLayout'", TabLayout.class);
    target.mViewPager = Utils.findRequiredViewAsType(source, R.id.favourite_view_pager, "field 'mViewPager'", ViewPager.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    FavouriteTabsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTabLayout = null;
    target.mViewPager = null;
  }
}
