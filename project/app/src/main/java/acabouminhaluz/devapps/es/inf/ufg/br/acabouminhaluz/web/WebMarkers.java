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
        int responseCode = response.code();
        if(responseCode == 200){
            String responseBody = null;
            try {
                responseBody = response.body().string();
                JSONObject object = new JSONObject(responseBody);
                String status = object.getString("status");
                if(status.equals("ok")) {

                    JSONArray reclamacoesAsJSON = object.getJSONArray("reclamacoesProximas");
                    ArrayList<MapMarker> mapMarkers = new ArrayList<>();

                    for (int index = 0; index < reclamacoesAsJSON.length(); index++) {
                        JSONObject reclamacaoAsJSON = reclamacoesAsJSON.getJSONObject(index);
                        MapMarker mapMarker = new MapMarker();
                        mapMarker.setUsuario(reclamacaoAsJSON.getString("usuario"));
                        mapMarker.setData_problema(reclamacaoAsJSON.getString("data_problema"));
                        mapMarker.setHora_problema(reclamacaoAsJSON.getString("hora_problema"));
                        mapMarker.setLatitude_problema(reclamacaoAsJSON.getString("latitude_problema"));
                        mapMarker.setLongitude_problema(reclamacaoAsJSON.getString("longitude_problema"));
                        mapMarker.setObs(reclamacaoAsJSON.getString("obs"));
                        mapMarkers.add(mapMarker);
                    }
                    EventBus.getDefault().post(mapMarkers);
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
