// Generated code from Butter Knife. Do not modify!
package io.github.javiewer.adapter;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.cardview.widget.CardView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import io.github.javiewer.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class GenreAdapter$ViewHolder_ViewBinding implements Unbinder {
  private GenreAdapter.ViewHolder target;

  @UiThread
  public GenreAdapter$ViewHolder_ViewBinding(GenreAdapter.ViewHolder target, View source) {
    this.target = target;

    target.mTextName = Utils.findRequiredViewAsType(source, R.id.genre_name, "field 'mTextName'", TextView.class);
    target.mCard = Utils.findRequiredViewAsType(source, R.id.card_genre, "field 'mCard'", CardView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    GenreAdapter.ViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTextName = null;
    target.mCard = null;
  }
}
