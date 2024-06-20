package eu.ill.visa.vdi.domain.models;

public class AccessCancellation {
    private String token;
    private String userFullName;

    public AccessCancellation() {
    }

    public AccessCancellation(String userFullName, String token) {
        this.userFullName = userFullName;
        this.token = token;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}