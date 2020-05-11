package com.space.Error;

public class ShipNotValidIdException extends RuntimeException {
    private String shipId;

    public ShipNotValidIdException(String shipId) {
        this.shipId = shipId;
    }

    public String getShipId() {
        return shipId;
    }
}
