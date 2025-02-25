package org.happysanta.gdtralive.game.api.dto;

public class ThemeHeader {
    private String guid; // = "e46a37c0-69e1-4646-8f9c-b47247586635";
    private String name; // = "Gravity Defied Original";
    private String description; // = "Default game parameters. No changes.";
    private String author; // = "GD Alive";
    private String date; // = "2024-04-09";

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}