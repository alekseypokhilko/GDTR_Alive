package org.happysanta.gdtralive.game.http;

import org.happysanta.gdtralive.game.api.Constants;
import org.happysanta.gdtralive.game.api.util.Function;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class APIClient {
    public static <T> T fetchConfig(Function<ConfigApi, Call<T>> apiCall) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        try {
            ConfigApi api = new Retrofit.Builder()
                    .baseUrl(Constants.GITHUB_BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                    .create(ConfigApi.class);
            return apiCall.apply(api)
                    .execute()
                    .body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            //HACK https://github.com/square/retrofit/issues/3144
            //OkHttp, the HTTP client which sits behind Retrofit by default, uses non-daemon threads.
            //This will prevent the JVM from exiting until they time out.
            //The general pattern for avoiding this scenario is:
            client.dispatcher().executorService().shutdown();
            client.connectionPool().evictAll();
        }
    }

    public static <T> T serverCall(String host, Function<ServerApi, Call<T>> apiCall) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        try {
            ServerApi api = new Retrofit.Builder()
                    .baseUrl(host)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                    .create(ServerApi.class);
            return apiCall.apply(api)
                    .execute()
                    .body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            //HACK https://github.com/square/retrofit/issues/3144
            //OkHttp, the HTTP client which sits behind Retrofit by default, uses non-daemon threads.
            //This will prevent the JVM from exiting until they time out.
            //The general pattern for avoiding this scenario is:
            client.dispatcher().executorService().shutdown();
            client.connectionPool().evictAll();
        }
    }
}
