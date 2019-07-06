package com.bestpractices.learning.atul.pinkupndrop;

import com.bestpractices.learning.atul.pinkupndrop.models.Users;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {
    @GET("users/serviceability")
    Call<Users> getServiceability();
}
