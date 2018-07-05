package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web;

import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.Marker;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.User;
import okhttp3.Response;

public class WebMarkers extends WebConnection {
    private static final String SERVICE = "reclamacoesProximas";
    private String latitude_usuario;
    private String longitude_usuario;

    public WebMarkers(String latitude_usuario, String longitude_usuario) {
        super(SERVICE);
        this.latitude_usuario = latitude_usuario;
        this.longitude_usuario = longitude_usuario;
    }

    @Override
    String getRequestContent() {
        Map<String,String> requestMap = new HashMap<>();
        requestMap.put("latitude_usuario", latitude_usuario);
        requestMap.put("longitude_usuario", longitude_usuario);

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
            if(status.equals("ok")) {

                JSONArray reclamacoesAsJSON = object.getJSONArray("reclamacoesProximas");
                List<Marker> markers = new ArrayList<>();

                for (int index = 0; index < reclamacoesAsJSON.length(); index++) {
                    JSONObject reclamacaoAsJSON = reclamacoesAsJSON.getJSONObject(index);
                    Marker marker = new Marker();
                    marker.setId_reclamacao(reclamacaoAsJSON.getString("id_reclamacao"));
                    marker.setData_registro(reclamacaoAsJSON.getString("data_registro"));
                    marker.setHora_registro(reclamacaoAsJSON.getString("hora_registro"));
                    marker.setData_problema(reclamacaoAsJSON.getString("data_problema"));
                    marker.setHora_problema(reclamacaoAsJSON.getString("hora_problema"));
                    marker.setLatitude_problema(reclamacaoAsJSON.getString("latitude_problema"));
                    marker.setLongitude_problema(reclamacaoAsJSON.getString("longitude_problema"));

                    markers.add(marker);
                }
                EventBus.getDefault().post(markers);
            }else{
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
