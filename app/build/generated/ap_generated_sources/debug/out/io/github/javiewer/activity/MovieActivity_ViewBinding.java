// Generated code from Butter Knife. Do not modify!
package io.github.javiewer.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wefika.flowlayout.FlowLayout;
import io.github.javiewer.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MovieActivity_ViewBinding implements Unbinder {
  private MovieActivity target;

  private View view7f080262;

  private View view7f080261;

  @UiThread
  public MovieActivity_ViewBinding(MovieActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MovieActivity_ViewBinding(final MovieActivity target, View source) {
    this.target = target;

    View view;
    target.mToolbarLayout = Utils.findRequiredViewAsType(source, R.id.toolbar_layout, "field 'mToolbarLayout'", CollapsingToolbarLayout.class);
    target.mToolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'mToolbar'", Toolbar.class);
    target.mToolbarLayoutBackground = Utils.findRequiredViewAsType(source, R.id.toolbar_layout_background, "field 'mToolbarLayoutBackground'", ImageView.class);
    target.mContent = Utils.findRequiredViewAsType(source, R.id.movie_content, "field 'mContent'", NestedScrollView.class);
    target.mProgressBar = Utils.findRequiredViewAsType(source, R.id.movie_progress_bar, "field 'mProgressBar'", ProgressBar.class);
    target.mFab = Utils.findRequiredViewAsType(source, R.id.fab, "field 'mFab'", FloatingActionButton.class);
    target.mFlowLayout = Utils.findRequiredViewAsType(source, R.id.genre_flow_layout, "field 'mFlowLayout'", FlowLayout.class);
    view = Utils.findRequiredView(source, R.id.view_preview, "method 'onClickPreview'");
    view7f080262 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClickPreview();
      }
    });
    view = Utils.findRequiredView(source, R.id.view_play, "method 'onPlay'");
    view7f080261 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onPlay();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    MovieActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mToolbarLayout = null;
    target.mToolbar = null;
    target.mToolbarLayoutBackground = null;
    target.mContent = null;
    target.mProgressBar = null;
    target.mFab = null;
    target.mFlowLayout = null;

    view7f080262.setOnClickListener(null);
    view7f080262 = null;
    view7f080261.setOnClickListener(null);
    view7f080261 = null;
  }
}
