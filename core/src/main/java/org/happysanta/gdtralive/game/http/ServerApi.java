package org.happysanta.gdtralive.game.http;

import org.happysanta.gdtralive.game.api.dto.ScoreDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServerApi {
    @GET("/score/track/{trackId}/{league}")
    Call<List<ScoreDto>> trackScores(@Path("trackId") String trackId, @Path("league") Integer league);

    @Headers({"Content-Type: application/json; charset=UTF-8"})
    @POST("/score")
    Call<String> sendScore(@Body ScoreDto body);
}
