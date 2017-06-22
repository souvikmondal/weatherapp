package com.backbase.weatherapp.main.provider;

/**
 * Created by Souvik on 22/06/17.
 */

public class ProviderException extends Exception {

    private String userMessage;

    public ProviderException(Exception e, String userMessage) {
        super(e);
        this.userMessage = userMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }
}
