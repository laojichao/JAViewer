// Generated code from Butter Knife. Do not modify!
package io.github.javiewer.activity;

import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import io.github.javiewer.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class WebViewActivity_ViewBinding implements Unbinder {
  private WebViewActivity target;

  private View view7f08007e;

  @UiThread
  public WebViewActivity_ViewBinding(WebViewActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public WebViewActivity_ViewBinding(final WebViewActivity target, View source) {
    this.target = target;

    View view;
    target.mWebView = Utils.findRequiredViewAsType(source, R.id.web_view, "field 'mWebView'", WebView.class);
    view = Utils.findRequiredView(source, R.id.button_unlock, "method 'onUnlock'");
    view7f08007e = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onUnlock(Utils.castParam(p0, "doClick", 0, "onUnlock", 0, Button.class));
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    WebViewActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mWebView = null;

    view7f08007e.setOnClickListener(null);
    view7f08007e = null;
  }
}
