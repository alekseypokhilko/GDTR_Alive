package org.happysanta.gdtralive.game.http;

import org.happysanta.gdtralive.game.api.Constants;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ConfigApi {
    @GET(Constants.SERVER_CONFIG_URL)
    Call<ServerConfig> serverConfig();
}
