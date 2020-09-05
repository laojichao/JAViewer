package io.github.javiewer.network;

import io.github.javiewer.JAViewer;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Url;


/**
 * TorrentKitty
 *
 * @author lao
 * @date 2020/9/4
 * Profile: TorrentKitty
 */

public interface TorrentKitty {
    //地址发布页：
    String POST_URL = "http://torrentkittyurl.com/tk/";
    String BASE_URL = "https://www.torrentkitty.tv";
    String BASE_URL1 = "https://www.torrentkitty.app";
    String BASE_URL2 = "https://www.torrentkitty.se";
    TorrentKitty INSTANCE = new Retrofit.Builder()
            .baseUrl(TorrentKitty.BASE_URL)
            .client(JAViewer.HTTP_CLIENT)
            .build()
            .create(TorrentKitty.class);

    @GET("/search/{keyword}")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Call<ResponseBody> search(@Path(value = "keyword") String keyword);

    @GET
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Call<ResponseBody> get(@Url String url);
}
