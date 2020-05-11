package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.Optional;

public class ShipSpecifications {
    public static Specification<Ship> nameLike(String name) {
        return (ship, cq, cb) -> Optional.ofNullable(name)
                .map(x->cb.like(ship.get("name"),"%"  + x + "%"))
                .orElse(null);
    }

    public static Specification<Ship> planetLike(String planet) {
        return (ship, cq, cb) -> Optional.ofNullable(planet)
                .map(x->cb.like(ship.get("planet"), "%" + x + "%"))
                .orElse(null);
    }

    public static Specification<Ship> shipTypeEqual(String shipType) {
        return (ship, cq, cb) -> Optional.ofNullable(shipType)
                .map(x->cb.equal(ship.get("shipType"), ShipType.valueOf(x)))
                .orElse(null);
    }

    public static Specification<Ship> isUsed(String isUsed) {
        return (ship, cq, cb) -> Optional.ofNullable(isUsed)
                .map(x->cb.equal(ship.get("isUsed"), Boolean.valueOf(x)))
                .orElse(null);
    }

    public static Specification<Ship> speedGreaterOrEqual(String speedMin) {
        return (ship, cq, cb) -> Optional.ofNullable(speedMin)
                .map(x->cb.greaterThanOrEqualTo(ship.get("speed"), Double.valueOf(x)))
                .orElse(null);
    }

    public static Specification<Ship> speedLessOrEqual(String speedMax) {
        return (ship, cq, cb) -> Optional.ofNullable(speedMax)
                .map(x->cb.lessThanOrEqualTo(ship.get("speed"), Double.valueOf(x)))
                .orElse(null);
    }

    public static Specification<Ship> crewSizeGreaterOrEqual(String crewSizeMin) {
        return (ship, cq, cb) -> Optional.ofNullable(crewSizeMin)
                .map(x -> cb.greaterThanOrEqualTo(ship.get("crewSize"), Integer.valueOf(x)))
                .orElse(null);
    }

    public static Specification<Ship> crewSizeLessOrEqual(String crewSizeMax) {
        return (ship, cq, cb) -> Optional.ofNullable(crewSizeMax)
                .map(x -> cb.lessThanOrEqualTo(ship.get("crewSize"), Integer.valueOf(x)))
                .orElse(null);
    }

    public static Specification<Ship> prodDateAfter(String prodDateAfter) {
        return (ship, cq, cb) -> Optional.ofNullable(prodDateAfter)
                .map(x -> cb.greaterThanOrEqualTo(ship.get("prodDate"), new Date(Long.valueOf(x))))
                .orElse(null);
    }

    public static Specification<Ship> prodDateBefore(String prodDateBefore) {
        return (ship, cq, cb) -> Optional.ofNullable(prodDateBefore)
                .map(x -> cb.lessThanOrEqualTo(ship.get("prodDate"), new Date(Long.valueOf(x))))
                .orElse(null);
    }

    public static Specification<Ship> raitingGreaterOrEqual(String ratingMin) {
        return (ship, cq, cb) -> Optional.ofNullable(ratingMin)
                .map(x->cb.greaterThanOrEqualTo(ship.get("rating"), Double.valueOf(x)))
                .orElse(null);
    }

    public static Specification<Ship> raitingLessOrEqual(String ratingMax) {
        return (ship, cq, cb) -> Optional.ofNullable(ratingMax)
                .map(x->cb.lessThanOrEqualTo(ship.get("rating"), Double.valueOf(x)))
                .orElse(null);
    }

}
