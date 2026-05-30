package com.example.msseguridad.infrastructure.rest.dto;

import java.util.List;

public class AuthResponse {
    private String token;
    private String tipo;
    private String username;
    private String email;
    private List<String> roles;

    public AuthResponse() {}

    public AuthResponse(String token, String tipo, String username, String email, List<String> roles) {
        this.token = token;
        this.tipo = tipo;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String token;
        private String tipo;
        private String username;
        private String email;
        private List<String> roles;

        public Builder token(String token) { this.token = token; return this; }
        public Builder tipo(String tipo) { this.tipo = tipo; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder roles(List<String> roles) { this.roles = roles; return this; }
        public AuthResponse build() { return new AuthResponse(token, tipo, username, email, roles); }
    }
}