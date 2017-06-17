package com.backbase.weatherapp.model.weather;

/**
 * Created by Souvik on 17/06/17.
 */

public class Weather {

    private String id;
    private String main;
    private String description;

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getMain() {
        return main;
    }
}
