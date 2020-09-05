package io.github.javiewer.network.provider;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.github.javiewer.adapter.item.DownloadLink;
import io.github.javiewer.adapter.item.MagnetLink;
import io.github.javiewer.network.BH;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * BHLinkProvider
 *
 * @author lao
 * @date 2020/9/5
 * Profile:
 */

public class BHLinkProvider extends DownloadLinkProvider {

    @Override
    public Call<ResponseBody> search(String keyword, int page) {
        if (page == 1) {
            return BH.INSTANCE.search(keyword);
        } else {
            return null;
        }
    }

    @Override
    public List<DownloadLink> parseDownloadLinks(String htmlContent) {
        ArrayList<DownloadLink> links = new ArrayList<>();
        Elements rows = Jsoup.parse(htmlContent).getElementsByClass("layui-colla-item fly-box");
        for (Element row : rows) {
            try {
                Element a = row.getElementsByClass("layui-colla-item").first().getElementsByTag("a").first();
                String magnet = a.attr("href");
                DownloadLink downloadLink =  DownloadLink.create(
                        getTitle(row),
                        row.getElementsByClass("layui-colla-content layui-show").first().getElementsByTag("p").get(3).text(),
                        row.getElementsByClass("layui-colla-content layui-show").first().getElementsByTag("p").get(2).text(),
                        null,
                        magnet);
                links.add(downloadLink);

            } catch (Exception ignored) {

            }
        }
        return links;
    }

    @Override
    public Call<ResponseBody> get(String url) {
        return null;
    }

    @Override
    public MagnetLink parseMagnetLink(String htmlContent) {
        return null;
    }

    private String getTitle(Element e) {
        String title = e.getElementsByClass("layui-colla-title search-colla-title").first().getElementsByTag("font").text();
        if (title.isEmpty()) {
            title = e.getElementsByTag("h2").first().text();
            title = title.substring(title.indexOf(" "), title.length());
        }
        return title;
    }
}
