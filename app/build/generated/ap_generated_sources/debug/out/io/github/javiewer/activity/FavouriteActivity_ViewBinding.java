// Generated code from Butter Knife. Do not modify!
package io.github.javiewer.activity;

import android.content.Context;
import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.google.android.material.appbar.AppBarLayout;
import io.github.javiewer.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class FavouriteActivity_ViewBinding implements Unbinder {
  private FavouriteActivity target;

  @UiThread
  public FavouriteActivity_ViewBinding(FavouriteActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public FavouriteActivity_ViewBinding(FavouriteActivity target, View source) {
    this.target = target;

    target.mViewPager = Utils.findRequiredViewAsType(source, R.id.favourite_view_pager, "field 'mViewPager'", AHBottomNavigationViewPager.class);
    target.mAppBarLayout = Utils.findRequiredViewAsType(source, R.id.app_bar_fav, "field 'mAppBarLayout'", AppBarLayout.class);
    target.mBottomNav = Utils.findRequiredViewAsType(source, R.id.bottom_navigation, "field 'mBottomNav'", AHBottomNavigation.class);
    target.mToolbar = Utils.findRequiredViewAsType(source, R.id.toolbar_fav, "field 'mToolbar'", Toolbar.class);

    Context context = source.getContext();
    target.mColorPrimary = ContextCompat.getColor(context, R.color.colorPrimary);
  }

  @Override
  @CallSuper
  public void unbind() {
    FavouriteActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mViewPager = null;
    target.mAppBarLayout = null;
    target.mBottomNav = null;
    target.mToolbar = null;
  }
}
