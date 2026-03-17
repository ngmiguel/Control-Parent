package org.example.backend.dto;

public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private String telephone;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(String token, String telephone, String message) {
        this.token = token;
        this.telephone = telephone;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
