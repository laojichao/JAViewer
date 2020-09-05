package io.github.javiewer.network.provider;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.github.javiewer.adapter.item.DownloadLink;
import io.github.javiewer.adapter.item.MagnetLink;
import io.github.javiewer.network.BTMOVI;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * BTMOVILinkProvider
 *
 * @author lao
 * @date 2020/9/4
 * Profile:
 */
class BTMOVILinkProvider extends DownloadLinkProvider {
    private static final String TAG = "BTMOVILinkProvider";
    @Override
    public Call<ResponseBody> search(String keyword, int page) {
        if (page == 1) {
            return BTMOVI.INSTANCE.search(keyword);
        } else {
            return null;
        }
    }

    @Override
    public List<DownloadLink> parseDownloadLinks(String htmlContent) {
        ArrayList<DownloadLink> links = new ArrayList<>();
        Elements rows = Jsoup.parse(htmlContent).getElementsByClass("search-item");
        for (Element row : rows) {
            try {
                Element a = row.getElementsByTag("a").first();
                String url = "http://www.btmovi.space" + a.attr("href");
                links.add(
                        DownloadLink.create(
                                row.getElementsByClass("item-title").first().text(),
                                row.getElementsByClass("cpill yellow-pill").first().text(),
                                row.getElementsByClass("item-bar").first().getElementsByTag("b").first().text(),
                                url,
                                null)
                );
            } catch (Exception ignored) {

            }
        }
        return links;
    }

    @Override
    public Call<ResponseBody> get(String url) {
        return BTMOVI.INSTANCE.get(url);
    }

    @Override
    public MagnetLink parseMagnetLink(String htmlContent) {
        return MagnetLink.create(Jsoup.parse(htmlContent).getElementById("down-url").attr("href"));
    }
}
