package edu.bluejack19_1.moment.util;

import edu.bluejack19_1.moment.notification.MyResponse;
import edu.bluejack19_1.moment.notification.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                "Content-Type:application/json",
                "Authorization:key=AAAA9GGlEZ4:APA91bHS753TrN1i6-VglDoNS2AOAUidFrXDOaGHqBeeCe3wqTcYZGkV7nQuHtQU6-FUs5OfNQRr37muztwgCgPpzlNT6q-j_1pzC6NlCvPL_9QBAaaKWVPpGtypLoAVMq4O7UqBqVzy"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
