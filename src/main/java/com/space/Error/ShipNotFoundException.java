package com.space.Error;

public class ShipNotFoundException extends RuntimeException {
    private Long shipId;

    public ShipNotFoundException(Long shipId) {
        this.shipId = shipId;
    }

    public Long getShipId() {
        return shipId;
    }
}
