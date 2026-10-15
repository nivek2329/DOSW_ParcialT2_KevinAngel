package edu.dosw.parcial.controller.dtos.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    @NotBlank(message = "El correo no puede estar vacio")
    private String correo;

    @NotBlank(message = "La contrasena no puede estar vacia")
    private String password;

    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String correo, String password) {
        this.correo = correo;
        this.password = password;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
