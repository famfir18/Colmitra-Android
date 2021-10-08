package com.softnesia.colmitra.service.api

import com.softnesia.colmitra.config.Session
import com.softnesia.colmitra.model.UnauthorizedEvent
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.greenrobot.eventbus.EventBus

/**
 * An interceptor to refresh token when the current token has been expired
 */
class TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val mainRequest = response.request()

        if (Session.loggedIn) {
            // if response code is 401 or 403, 'mainRequest' has encountered authentication error
            if (response.code() == 401 || response.code() == 403) {
                EventBus.getDefault().post(UnauthorizedEvent.instance())

                return refreshToken(mainRequest)
            }
        }
        return null
    }

    private fun refreshToken(mainRequest: Request): Request? {
//        String refreshToken = Session.getRefreshToken();
//        int clientId = BuildConfig.CLIENT_ID;
//        String clientSecret = BuildConfig.CLIENT_SECRET;
//        String grantType = "refresh_token";
//
//        try {
//            Call<Auth> request = ApiService.getInstance().refreshToken(
//                    refreshToken,
//                    clientId,
//                    clientSecret,
//                    grantType,
//                    "");
//
//            retrofit2.Response<Auth> authResponse = request.execute();
//
//            if (authResponse.isSuccessful()) {
//                Auth auth = authResponse.body();
//                Session.saveAuth(auth);
//
//                // Retry the 'mainRequest' which encountered an authentication error
//                // Add new token into 'mainRequest' header and request again
//                Request.Builder builder = mainRequest.newBuilder().header("Authorization", Session.getToken()).
//                        method(mainRequest.method(), mainRequest.body());
//                return builder.build();
//            } else {
//                // Send an event to trigger BaseActivity method to redirect to LoginActivity.
//                EventBus.getDefault().post(UnauthorizedEvent.INSTANCE);
//                return null;
//            }
//        } catch (IOException e) {
//            // Send an event to trigger BaseActivity method to redirect to LoginActivity.
//            EventBus.getDefault().post(UnauthorizedEvent.INSTANCE);
//            return null;
//        }
        return null
    }
}