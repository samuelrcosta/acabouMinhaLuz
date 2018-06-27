package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web;


import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.MessageEvent;
import okhttp3.Response;

public class WebRegister extends WebConnection {

    private static final String SERVICE = "cadastro";
    private String name;
    private String CPF;
    private String email;
    private String password;

    public WebRegister(String name, String CPF, String email, String password) {
        super(SERVICE);
        this.name = name;
        this.CPF = CPF;
        this.email = email;
        this.password = password;
    }

    @Override
    String getRequestContent() {
        Map<String,String> requestMap = new HashMap<>();
        requestMap.put("nome", name);
        requestMap.put("CPF", CPF);
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
            if(status.equals("error")){
                String error = object.getString("message");
                EventBus.getDefault().post(new Exception(error));
            }else{
                EventBus.getDefault().post(new MessageEvent("Ok"));
            }
        } catch (IOException e) {
            EventBus.getDefault().post(e);
        } catch (JSONException e) {
            EventBus.getDefault().post(new Exception("A resposta do servidor não é válida"));
        }

    }
}