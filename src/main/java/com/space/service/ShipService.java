package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class ShipService {
    private static final String PAGE_NUMBER_NAME = "pageNumber";
    private static final String PAGE_NUMBER_VALUE = "0";
    private static final String PAGE_SIZE_NAME = "pageSize";
    private static final String PAGE_SIZE_VALUE = "3";
    private static final String ORDER_NAME = "order";
    private static final String ORDER_VALUE = "ID";

    private static final int NAME_LEN_MAX = 50;
    private static final int PLANET_LEN_MAX = 50;

    private static final int PRODDATE_YEAR_MIN = 2800;
    private static final int PRODDATE_YEAR_MAX = 3019;

    private static final double SPEED_VALUE_MIN = 0.01;
    private static final double SPEED_VALUE_MAX = 0.99;

    private static final int CREWSIZE_VALUE_MIN = 1;
    private static final int CREWSIZE_VALUE_MAX = 9999;

    private static final int CURRENT_YEAR = 3019;
    private static final int RATING_KOEFF = 80;
    private static final double USED_KOEFF = .5;
    private static final double NOT_USED_KOEFF = 1.;

    @Autowired
    ShipRepository shipRepository;

    private Pageable getPageable(Map<String, String> restParams) {

        int pageNumber = Integer.parseInt(Optional
                .ofNullable(restParams.get(PAGE_NUMBER_NAME))
                .orElse(PAGE_NUMBER_VALUE));

        int pageSize = Integer.parseInt(Optional
                .ofNullable(restParams.get(PAGE_SIZE_NAME))
                .orElse(PAGE_SIZE_VALUE));

        String order = Optional
                .ofNullable(restParams.get(ORDER_NAME))
                .orElse(ORDER_VALUE);

        Sort sort = Sort.by(Sort.DEFAULT_DIRECTION, ShipOrder.valueOf(order).getFieldName());

        return PageRequest.of(pageNumber, pageSize, sort);
    }

    private Specification<Ship> getShipSpecification(Map<String, String> requestParams) {
        Specification<Ship> shipSpecification = Specification
                .where(ShipSpecifications.nameLike(requestParams.get("name")))
                .and(ShipSpecifications.planetLike(requestParams.get("planet")))
                .and(ShipSpecifications.shipTypeEqual(requestParams.get("shipType")))
                .and(ShipSpecifications.prodDateAfter(requestParams.get("after")))
                .and(ShipSpecifications.prodDateBefore(requestParams.get("before")))
                .and(ShipSpecifications.isUsed(requestParams.get("isUsed")))
                .and(ShipSpecifications.speedGreaterOrEqual(requestParams.get("minSpeed")))
                .and(ShipSpecifications.speedLessOrEqual(requestParams.get("maxSpeed")))
                .and(ShipSpecifications.crewSizeGreaterOrEqual(requestParams.get("minCrewSize")))
                .and(ShipSpecifications.crewSizeLessOrEqual(requestParams.get("maxCrewSize")))
                .and(ShipSpecifications.raitingGreaterOrEqual(requestParams.get("minRating")))
                .and(ShipSpecifications.raitingLessOrEqual(requestParams.get("maxRating")))
                ;

        return shipSpecification;
    }

    public List<Ship> getAllShips (Map<String, String> restParams) {

        Pageable pageable = getPageable(restParams);

        Specification<Ship> shipSpecification = getShipSpecification(restParams);
//        System.out.println("specification: " + shipSpecification);

        Page<Ship> shipPage = shipRepository.findAll(shipSpecification, pageable);

        return shipPage.getContent();
    }

    public long countShips(Map<String, String> requestParams){
        return shipRepository.count(getShipSpecification(requestParams));
    }

    public Ship saveShip(Ship ship) {
        ship.setRating(calcRatingShip(ship));
        return shipRepository.save(ship);
    }

    public Optional<Ship> findById (Long id) {
        return shipRepository.findById(id);
    }

    public boolean isPresentbyId (Long id) {
        return shipRepository.findById(id).isPresent();
    }

    public void deleteById (Long id) {
        shipRepository.deleteById(id);
    }

    public boolean isValidShipId(String Id) {

        try {
            Float vFloat = Float.valueOf(Id);
            if (vFloat > 0 && (vFloat - vFloat.longValue()) == 0 )
                return true;

        } catch (NumberFormatException e) {

        }

        return false;
    }

   public boolean isValidShipDataThanSet (Map<String, String> dataParams, Ship ship, boolean isNewShip) {

       if (isNewShip) {
           for (Field field : ship.getClass().getDeclaredFields()) {
               String name = field.getName();
               switch (name) {
                   case "id":
                   case "rating":
                       break;
                   case "isUsed":
                       if (dataParams.get(name) == null)
                           isValidShipFieldThanSet(name, "false", ship);
                       else
                           isValidShipFieldThanSet(name, dataParams.get(name), ship);
                       break;
                   default:
                       if (dataParams.get(name) == null || !isValidShipFieldThanSet(name, dataParams.get(name), ship)) {
                           dataParams.put("Error",name + "=" + dataParams.get(name));
                           return false;
                       }
               }
           }
       } else {
           for(Map.Entry<String, String> entry: dataParams.entrySet()) {
               if (!isValidShipFieldThanSet(entry.getKey(),entry.getValue(),ship)) {
                   dataParams.put("Error", entry.getKey() + "=" + entry.getValue());
                   return false;
               }
           }
       }

        return true;
    }

    public boolean isValidShipFieldThanSet(String fieldName, String fieldValue, Ship ship) {

        switch (fieldName) {
            case "id":
            case "rating":
                break;
            case "name" :
                String name = fieldValue;
                if (name.isEmpty() || name.toString().length() > NAME_LEN_MAX)
                    return false;
                else
                    ship.setName(name);
                    break;
            case "planet" :
                String planet = fieldValue;
                if (planet.isEmpty() || planet.toString().length() > PLANET_LEN_MAX)
                    return false;
                else
                    ship.setPlanet(planet);
                    break;
            case "shipType":
                ShipType shipType = ShipType.valueOf(fieldValue);
                ship.setShipType(shipType);
                break;
            case "prodDate":
                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(Long.valueOf(fieldValue));

                if (
                        calendar.getTimeInMillis() < 0 ||
                        calendar.get(Calendar.YEAR) < PRODDATE_YEAR_MIN ||
                        calendar.get(Calendar.YEAR) > PRODDATE_YEAR_MAX
                )
                    return false;
                else
                    ship.setProdDate(calendar.getTime());
                    break;
            case "isUsed":
                ship.setUsed(Boolean.valueOf(fieldValue));
                break;
            case "speed":
                double speed = Double.valueOf(fieldValue);
                if (speed < SPEED_VALUE_MIN || speed > SPEED_VALUE_MAX)
                    return false;
                else
                    ship.setSpeed(Math.round(speed * 100.)/100.);
                    break;
            case "crewSize":
                int crewSize = Integer.valueOf(fieldValue);
                if (crewSize < CREWSIZE_VALUE_MIN || crewSize > CREWSIZE_VALUE_MAX)
                    return false;
                else
                    ship.setCrewSize(crewSize);
                    break;
            default:
        }
        return true;
    }

    public double calcRatingShip(Ship ship) {

        Calendar calendar = Calendar.getInstance();
//                calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(ship.getProdDate().getTime());
        int prodYear = calendar.get(Calendar.YEAR);

        double rating = Math.round(
            (RATING_KOEFF * ship.getSpeed() * (ship.isUsed() ? USED_KOEFF : NOT_USED_KOEFF)) * 100.
                /
            (CURRENT_YEAR - prodYear + 1.)
                                    )/100.;

        return rating;
    }

}
