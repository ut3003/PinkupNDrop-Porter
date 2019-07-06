package com.bestpractices.learning.atul.pinkupndrop;

import android.util.Log;

import com.bestpractices.learning.atul.pinkupndrop.models.Cost;
import com.bestpractices.learning.atul.pinkupndrop.models.ETA;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetVehicles {
    public static final String TAG = "GetVehicles";
    private int cost;
    private int eta;

    public void getVehiclesData(GetVehiclesData vehiclesData) {
        Call<Cost> callCost = vehiclesData.getVehiclesCost(0.0, 1.0);
        callCost.enqueue(new Callback<Cost>() {
            @Override
            public void onResponse(Call<Cost> call, Response<Cost> response) {
                setCost(response.body());
            }

            @Override
            public void onFailure(Call<Cost> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });

        Call<ETA> callETA = vehiclesData.getVehiclesETA(0.0, 1.0);
        callETA.enqueue(new Callback<ETA>() {
            @Override
            public void onResponse(Call<ETA> call, Response<ETA> response) {
                ETA eta = response.body();
                setETA(response.body());
            }

            @Override
            public void onFailure(Call<ETA> call, Throwable t) {

            }
        });

    }

    private void setCost(Cost body){
        cost = body.getCost();
    }

    public int getCost(){
        return cost;
    }

    private void setETA(ETA body){
        eta = body.getEta();
    }

    public int getETA(){
        return eta;
    }

}
