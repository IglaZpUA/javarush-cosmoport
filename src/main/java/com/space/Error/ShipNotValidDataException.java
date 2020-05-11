package com.space.Error;

import java.util.Map;

public class ShipNotValidDataException extends RuntimeException {
    private String errorParam;

    public ShipNotValidDataException(String errorParam) {
        this.errorParam = errorParam;
    }

    public String getErrorParam() {
        return errorParam;
    }
}
