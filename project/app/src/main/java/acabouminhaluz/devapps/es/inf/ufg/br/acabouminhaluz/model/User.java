package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model;

/**
 * Created by marceloquinta on 03/02/17.
 */

public class User {

    private String name;

    private String email;

    private String token;

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
}
