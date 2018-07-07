package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model;

/**
 * Created by Samuel on 27/06/2018.
 */
public class User {

    private String name;

    private String email;

    private String CPF;

    private String password;

    private String token;

    private String image;

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }
}
