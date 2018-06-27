package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.model;


public class MessageEvent{
    private final String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}