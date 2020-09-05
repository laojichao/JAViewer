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

public class ActressPaletteAdapter$ViewHolder_ViewBinding implements Unbinder {
  private ActressPaletteAdapter.ViewHolder target;

  @UiThread
  public ActressPaletteAdapter$ViewHolder_ViewBinding(ActressPaletteAdapter.ViewHolder target,
      View source) {
    this.target = target;

    target.mImage = Utils.findRequiredViewAsType(source, R.id.actress_palette_img, "field 'mImage'", ImageView.class);
    target.mName = Utils.findRequiredViewAsType(source, R.id.actress_palette_name, "field 'mName'", TextView.class);
    target.mCard = Utils.findRequiredViewAsType(source, R.id.card_actress_palette, "field 'mCard'", CardView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ActressPaletteAdapter.ViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mImage = null;
    target.mName = null;
    target.mCard = null;
  }
}
