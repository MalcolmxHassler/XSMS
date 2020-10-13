package com.example.xsms.beans;

public class Message {
    String id;
    String nom;
    String numero;
    String message;
    String dateM;

    public Message(String nom, String numero, String message, String dateM) {
        this.nom = nom;
        this.numero = numero;
        this.message = message;
        this.dateM = dateM;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateM() {
        return dateM;
    }

    public void setDateM(String dateM) {
        this.dateM = dateM;
    }

    public Message(String id, String nom, String numero, String message, String dateM) {
        this.id = id;
        this.nom = nom;
        this.numero = numero;
        this.message = message;
        this.dateM = dateM;
    }
}
