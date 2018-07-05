package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model;

public class Marker {

    private String id_reclamacao;
    private String data_registro;
    private String hora_registro;
    private String data_problema;
    private String hora_problema;
    private String latitude_problema;
    private String longitude_problema;
    private String obs;

    public String getId_reclamacao() {
        return id_reclamacao;
    }

    public void setId_reclamacao(String id_reclamacao) {
        this.id_reclamacao = id_reclamacao;
    }

    public String getData_registro() {
        return data_registro;
    }

    public void setData_registro(String data_registro) {
        this.data_registro = data_registro;
    }

    public String getHora_registro() {
        return hora_registro;
    }

    public void setHora_registro(String hora_registro) {
        this.hora_registro = hora_registro;
    }

    public String getData_problema() {
        return data_problema;
    }

    public void setData_problema(String data_problema) {
        this.data_problema = data_problema;
    }

    public String getHora_problema() {
        return hora_problema;
    }

    public void setHora_problema(String hora_problema) {
        this.hora_problema = hora_problema;
    }

    public String getLatitude_problema() {
        return latitude_problema;
    }

    public void setLatitude_problema(String latitude_problema) {
        this.latitude_problema = latitude_problema;
    }

    public String getLongitude_problema() {
        return longitude_problema;
    }

    public void setLongitude_problema(String longitude_problema) {
        this.longitude_problema = longitude_problema;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }
}
