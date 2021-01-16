package yakom.mercury.model;

public class AuthenticationRequest {

    public String username;
    public String password;

    public AuthenticationRequest() {
    }

    public AuthenticationRequest(
            String username, String password) {
        this.username = username;
        this.password = password;
    }
}
