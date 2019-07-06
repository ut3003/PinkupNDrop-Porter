package com.bestpractices.learning.atul.pinkupndrop;

import com.bestpractices.learning.atul.pinkupndrop.models.Users;
import com.bestpractices.learning.atul.pinkupndrop.view.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetUsersData {
    MainActivity activity = new MainActivity();
    private boolean isAvailable = false;

    public void getUserData(GetDataService service) {
        Call<Users> serviceability = service.getServiceability();
        serviceability.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                setStopService(response.body().isServiceability());
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                setStopService(false);
            }
        });
    }

    public void setStopService(boolean isAvailable) {
        this.isAvailable =  isAvailable;
    }

    public boolean getStopService(){
        return isAvailable;
    }


}
