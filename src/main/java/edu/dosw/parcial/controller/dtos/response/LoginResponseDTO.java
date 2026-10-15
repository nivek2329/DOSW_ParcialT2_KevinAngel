package edu.dosw.parcial.controller.dtos.response;

public class LoginResponseDTO {

    private String token;
    private String tipo;
    private String rol;
    private long expiracionMilis;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, String tipo, String rol, long expiracionMilis) {
        this.token = token;
        this.tipo = tipo;
        this.rol = rol;
        this.expiracionMilis = expiracionMilis;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public long getExpiracionMilis() {
        return expiracionMilis;
    }

    public void setExpiracionMilis(long expiracionMilis) {
        this.expiracionMilis = expiracionMilis;
    }
}
