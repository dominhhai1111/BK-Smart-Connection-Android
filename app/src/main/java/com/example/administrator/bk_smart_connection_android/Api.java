package doanbk.hust.demoretrofit;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by DOANBK on 10/26/2017.
 */

public interface Api {
    @GET("/user_id=1/")
    Observable<User> getUser();

}
