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
 * BTSO
 *
 * @author lao
 * @date 2020/9/4
 * Profile:
 */

public interface BTSO {


    String BASE_URL = "https://btsow.com";
    String BASE_URL1 = "https://btsow.monster";
    String BASE_URL2 = "https://btsow.case";
    BTSO INSTANCE = new Retrofit.Builder()
            .baseUrl(BTSO.BASE_URL)
            .client(JAViewer.HTTP_CLIENT)
            .build()
            .create(BTSO.class);

    @GET("/search/{keyword}/page/{page}")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Call<ResponseBody> search(@Path(value = "keyword") String keyword, @Path("page") int page);

    @GET
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Call<ResponseBody> get(@Url String url);
}
