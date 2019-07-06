package com.bestpractices.learning.atul.pinkupndrop;

import com.bestpractices.learning.atul.pinkupndrop.models.Cost;
import com.bestpractices.learning.atul.pinkupndrop.models.ETA;
import com.bestpractices.learning.atul.pinkupndrop.models.Users;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetVehiclesData {
    @GET("vehicles/eta")
    Call<ETA> getVehiclesETA(@Query(value="lat") Double lat, @Query(value="lng" ) Double lng);

    @GET("vehicles/cost")
    Call<Cost> getVehiclesCost(@Query(value="lat") Double lat, @Query(value="lng") Double lng);
//
//    @GET("users/serviceability")
//    Call<Users> getServiceability();
}
