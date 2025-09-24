package org.example.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PasswordEntity {

    private String site;
    private String username;
    private String password;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor
    public PasswordEntity(String site, String username, String password, String notes) {
        this.site = site;
        this.username = username;
        this.password = password;
        this.notes = notes != null ? notes : "";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor para deserialización
    public PasswordEntity(String site, String username, String password, String notes,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.site = site;
        this.username = username;
        this.password = password;
        this.notes = notes != null ? notes : "";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
        updateTimestamp();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        updateTimestamp();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        updateTimestamp();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes != null ? notes : "";
        updateTimestamp();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String toCsvString() {
        return String.join("|",
                escapeCsvField(site),
                escapeCsvField(username),
                escapeCsvField(password),
                escapeCsvField(notes),
                createdAt.toString(),
                updatedAt.toString()
        );
    }

    private String escapeCsvField(String field) {
        if (field == null) return "";
        return field.replace("|", "\\|").replace("\n", "\\n").replace("\r", "\\r");
    }

    public static PasswordEntity fromCsvString(String csvLine) {
        String[] fields = csvLine.split("\\|");

        if (fields.length < 6) {
            throw new IllegalArgumentException("Formato CSV inválido");
        }

        String site = unescapeCsvField(fields[0]);
        String username = unescapeCsvField(fields[1]);
        String password = unescapeCsvField(fields[2]);
        String notes = unescapeCsvField(fields[3]);
        LocalDateTime createdAt = LocalDateTime.parse(fields[4]);
        LocalDateTime updatedAt = LocalDateTime.parse(fields[5]);

        return new PasswordEntity(site, username, password, notes, createdAt, updatedAt);
    }

    private static String unescapeCsvField(String field) {
        if (field == null) return "";
        return field.replace("\\|", "|").replace("\\n", "\n").replace("\\r", "\r");
    }

    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return createdAt.format(formatter);
    }

    public String getFormattedUpdatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return updatedAt.format(formatter);
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}
