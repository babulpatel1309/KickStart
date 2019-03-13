package com.kickstart.WebService;


import com.google.gson.JsonElement;
import com.kickstart.data.Bean.CommonCategoryBean;
import com.kickstart.data.Bean.CommonQuestionBean;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Harsh on 29/12/17.
 */
public interface ServiceInterface {

    @GET("category.php")
    Call<CommonCategoryBean> getCategories();

    @POST("questions.php")
    Call<CommonQuestionBean> getQuestions(@Body JsonElement requestBody);

   /*
    @GET("checkVersion.php")
    Call<VersionCheckBean> checkVersion();

    @Streaming
    @GET
    Call<ResponseBody> syncImages(@Url String fileUrl);

    @GET("getAdsSettings")
    Call<AdsSetting> getAdsSettingsNew(@QueryMap Map<String, String> requestBody);
*/
}