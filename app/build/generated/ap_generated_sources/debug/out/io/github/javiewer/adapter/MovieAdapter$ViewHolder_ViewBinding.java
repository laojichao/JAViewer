// Generated code from Butter Knife. Do not modify!
package io.github.javiewer.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.cardview.widget.CardView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import io.github.javiewer.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MovieAdapter$ViewHolder_ViewBinding implements Unbinder {
  private MovieAdapter.ViewHolder target;

  @UiThread
  public MovieAdapter$ViewHolder_ViewBinding(MovieAdapter.ViewHolder target, View source) {
    this.target = target;

    target.mTextTitle = Utils.findRequiredViewAsType(source, R.id.movie_title, "field 'mTextTitle'", TextView.class);
    target.mTextCode = Utils.findRequiredViewAsType(source, R.id.movie_size, "field 'mTextCode'", TextView.class);
    target.mTextDate = Utils.findRequiredViewAsType(source, R.id.movie_date, "field 'mTextDate'", TextView.class);
    target.mImageCover = Utils.findRequiredViewAsType(source, R.id.movie_cover, "field 'mImageCover'", ImageView.class);
    target.mImageHot = Utils.findRequiredViewAsType(source, R.id.movie_hot, "field 'mImageHot'", ImageView.class);
    target.mCard = Utils.findRequiredViewAsType(source, R.id.card_movie, "field 'mCard'", CardView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MovieAdapter.ViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTextTitle = null;
    target.mTextCode = null;
    target.mTextDate = null;
    target.mImageCover = null;
    target.mImageHot = null;
    target.mCard = null;
  }
}
