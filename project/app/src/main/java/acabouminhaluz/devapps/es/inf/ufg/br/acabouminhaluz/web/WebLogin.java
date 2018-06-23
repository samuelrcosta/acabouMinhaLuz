package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.User;
import okhttp3.Response;

/**
 * Created by marceloquinta on 10/02/17.
 */

public class WebLogin extends WebConnection {

    private static final String SERVICE = "login";
    private String email;
    private String password;

    public WebLogin(String email, String password) {
        super(SERVICE);
        this.email = email;
        this.password = password;
    }

    @Override
    String getRequestContent() {
        Map<String,String> requestMap = new HashMap<>();
        requestMap.put("login", email);
        requestMap.put("password", password);

        JSONObject json = new JSONObject(requestMap);
        String jsonString = json.toString();

        return  jsonString;
    }

    @Override
    void handleResponse(Response response) {
        String responseBody = null;
        try {
            responseBody = response.body().string();
            User user = new User();
            JSONObject object = new JSONObject(responseBody);
            user.setEmail(email);
            user.setName(object.getString("username"));
            user.setToken(object.getString("token"));
            EventBus.getDefault().post(user);
        } catch (IOException e) {
            EventBus.getDefault().post(e);
        } catch (JSONException e) {
            EventBus.getDefault().post(new Exception("A resposta do servidor não é válida"));
        }

    }
}
