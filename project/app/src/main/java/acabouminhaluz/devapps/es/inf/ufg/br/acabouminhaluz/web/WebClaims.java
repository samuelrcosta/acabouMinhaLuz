package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.web;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.MessageEvent;
import acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model.Reclamacao;
import okhttp3.Response;

public class WebClaims extends WebConnection {
    private static final String SERVICE = "minhasReclamacoes";
    private String token;

    public WebClaims(String token) {
        super(SERVICE);
        this.token = token;
    }

    @Override
    String getRequestContent() {
        Map<String,String> requestMap = new HashMap<>();
        requestMap.put("token", token);

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

                    JSONArray reclamacoesAsJSON = object.getJSONArray("reclamacoes");
                    ArrayList<Reclamacao> reclamacoes = new ArrayList<>();

                    for (int index = 0; index < reclamacoesAsJSON.length(); index++) {
                        JSONObject reclamacaoAsJSON = reclamacoesAsJSON.getJSONObject(index);
                        Reclamacao reclamacao = new Reclamacao();
                        reclamacao.setId(reclamacaoAsJSON.getInt("id"));
                        reclamacao.setData(reclamacaoAsJSON.getString("data"));
                        reclamacao.setHora(reclamacaoAsJSON.getString("hora"));
                        reclamacao.setObs(reclamacaoAsJSON.getString("obs"));
                        reclamacao.setLatitude(reclamacaoAsJSON.getString("latitude"));
                        reclamacao.setLongitude(reclamacaoAsJSON.getString("longitude"));
                        reclamacoes.add(reclamacao);
                    }
                    EventBus.getDefault().post(reclamacoes);
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
