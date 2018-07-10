package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model;

/**
 * Created by marceloquinta on 03/02/17.
 */

public class Task {

    private int id;
    private String name;
    private String description;
    private boolean done;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}