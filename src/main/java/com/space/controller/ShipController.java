package com.space.controller;

import com.space.model.Ship;
import com.space.Error.ShipNotFoundException;
import com.space.Error.ShipNotValidDataException;
import com.space.Error.ShipNotValidIdException;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rest/ships")
public class ShipController {

    @Autowired
    ShipService shipService;

    @GetMapping("")
    public List<Ship> getAllShips(
            @RequestParam Map<String, String> requestParams
    ) {
        return shipService.getAllShips(requestParams);
    }

    @GetMapping("/count")
    public Long getCountShips(
            @RequestParam Map<String, String> requestParams
    ) {
        return shipService.countShips(requestParams);
    }

    @PostMapping("/")
    public Ship createShip (
            @RequestBody Map<String, String> dataParams
    ) {

        Ship ship = new Ship();

        if (!shipService.isValidShipDataThanSet(dataParams, ship, true)) {
            throw new ShipNotValidDataException(dataParams.get("Error"));
        }

        return shipService.findById(shipService.saveShip(ship).getId()).get();
    }

    @GetMapping("/{id}")
    public Ship getShips(@PathVariable String id){

        if (!shipService.isValidShipId(id)) {
            throw new ShipNotValidIdException(id);
        }

        return shipService.findById(Long.valueOf(id))
                .orElseThrow(() -> new ShipNotFoundException(Long.valueOf(id)));
    }

    @PostMapping("/{id}")
    public Ship updateShip (
            @PathVariable String id,
            @RequestBody Map<String, String> dataParams
    ) {

        if (!shipService.isValidShipId(id)) {
            throw new ShipNotValidIdException(id);
        }

        Ship ship = shipService.findById(Long.valueOf(id))
                .orElseThrow(() -> new ShipNotFoundException(Long.valueOf(id)));

        if (!shipService.isValidShipDataThanSet(dataParams, ship, false)) {
            throw new ShipNotValidDataException(dataParams.get("Error"));
        }

        return shipService.findById(shipService.saveShip(ship).getId()).get();
    }

    @DeleteMapping("/{id}")
    public void deleteShip(@PathVariable String id) {

        if (!shipService.isValidShipId(id)) {
            throw new ShipNotValidIdException(id);
        }

        if (!shipService.isPresentbyId(Long.valueOf(id))) {
            throw new ShipNotFoundException(Long.valueOf(id));
        }

        shipService.deleteById(Long.valueOf(id));

    }

    @ExceptionHandler(ShipNotValidIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void shipNotValidId(ShipNotValidIdException e) {
        System.out.println("Error: shipId is not valid for [id=" + e.getShipId() + "]");
    }

    @ExceptionHandler(ShipNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void shipNotFound(ShipNotFoundException e) {
        System.out.println("Error: ship not found by [id=" + e.getShipId() + "]");
    }

    @ExceptionHandler(ShipNotValidDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void shipNotValidData(ShipNotValidDataException e) {
        System.out.println("Error: ship's data is not valid for [" + e.getErrorParam() + "]");
    }

}

