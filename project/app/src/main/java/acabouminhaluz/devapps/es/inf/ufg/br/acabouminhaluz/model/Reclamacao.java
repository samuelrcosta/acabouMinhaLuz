package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model;

public class Reclamacao {

    private int Id;
    private String data;
    private String hora;
    private String obs;
    private String latitude;
    private String longitude;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}