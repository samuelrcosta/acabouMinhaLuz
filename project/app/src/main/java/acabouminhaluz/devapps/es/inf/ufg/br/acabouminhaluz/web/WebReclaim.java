package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.MapMarker;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.MessageEvent;
import okhttp3.Response;

public class WebReclaim extends WebConnection {
    private static final String SERVICE = "enviarReclamacao";
    private String token;
    private String data;
    private String hora;
    private String latitude;
    private String longitude;
    private String obs;

    public WebReclaim(String token, String data, String hora, String latitude, String longitude, String obs) {
        super(SERVICE);
        this.token = token;
        this.data = data;
        this.hora = hora;
        this.latitude = latitude;
        this.longitude = longitude;
        this.obs = obs;
    }

    @Override
    String getRequestContent() {
        Map<String,String> requestMap = new HashMap<>();
        requestMap.put("token", token);
        requestMap.put("data", data);
        requestMap.put("hora", hora);
        requestMap.put("latitude", latitude);
        requestMap.put("longitude", longitude);
        requestMap.put("obs", obs);

        JSONObject json = new JSONObject(requestMap);
        String jsonString = json.toString();

        return  jsonString;
    }

    @Override
    void handleResponse(Response response) {
        int responseCode = response.code();
        if(responseCode == 200){
            String responseBody = null;
            try {
                responseBody = response.body().string();
                JSONObject object = new JSONObject(responseBody);
                String status = object.getString("status");
                if(status.equals("ok")) {
                    EventBus.getDefault().post(new MessageEvent("Ok"));
                }else{
                    String error = object.getString("message");
                    EventBus.getDefault().post(new Exception(error));
                }
            } catch (IOException e) {
                EventBus.getDefault().post(e);
            } catch (JSONException e) {
                EventBus.getDefault().post(new Exception("A resposta do servidor não é válida"));
            }
        }else if(responseCode == 403){
            // is not logged
            EventBus.getDefault().post(new MessageEvent("403"));
        }
    }
}
