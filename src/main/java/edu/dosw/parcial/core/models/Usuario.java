package edu.dosw.parcial.core.models;

public class Usuario {

    private String id;
    private String nombre;
    private String correo;
    private String telefono;
    private String passwordHash;
    private Rol rol;

    public Usuario() {
    }

    public Usuario(String id, String nombre, String correo, String telefono, String passwordHash, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public boolean esCliente() {
        return Rol.CLIENTE.equals(this.rol);
    }

    public boolean esAdmin() {
        return Rol.ADMIN.equals(this.rol);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
