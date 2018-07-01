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
        requestMap.put("email", email);
        requestMap.put("senha", password);

        JSONObject json = new JSONObject(requestMap);
        String jsonString = json.toString();

        return  jsonString;
    }

    @Override
    void handleResponse(Response response) {
        String responseBody = null;
        try {
            responseBody = response.body().string();
            JSONObject object = new JSONObject(responseBody);
            String status = object.getString("status");
            if(status.equals("ok")){
                User user = new User();
                user.setEmail(email);
                user.setId(object.getString("id"));
                user.setCPF(object.getString("CPF"));
                user.setName(object.getString("nome"));
                user.setToken(object.getString("token"));
                user.setImage(object.getString("image"));
                EventBus.getDefault().post(user);
            }else {
                String error = object.getString("message");
                EventBus.getDefault().post(new Exception(error));
            }
        } catch (IOException e) {
            EventBus.getDefault().post(e);
        } catch (JSONException e) {
            EventBus.getDefault().post(new Exception("A resposta do servidor não é válida"));
        }

    }
}
