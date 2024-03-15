package ru.practicum.shareit;

public class ResponseBody {
    private final String error;

    public ResponseBody(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
