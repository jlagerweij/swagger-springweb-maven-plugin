package org.example.controllers;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;

import org.example.model.Car;
import org.example.model.Wheel;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/cars", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "", description = "Cars accesss resoure.")
public class CarsEndpoint {

    @ApiOperation(value = "Creates a Car")
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Car create(@ApiParam(required = true, name = "car", value = "The car object that needs to be created") @RequestBody Car car) {
        return new Car(car.getId(), car.getName(), car.getColor(), car.getWheels());
    }

    @ApiOperation(value = "Method to update a car")
    @RequestMapping(method = RequestMethod.PUT, value = "/{carId}")
    @ResponseBody
    public Car update(
            @ApiParam(required = true, value = "The id of the car that should be updated", name = "carId") @PathVariable("carId") String carId,
            @ApiParam(required = true, name = "car", value = "The car object that needs to be updated") @RequestBody Car car) {

        return new Car(carId, car.getName(), car.getColor(), car.getWheels());
    }

    @ApiOperation(value = "Gets all Cars.")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Car> getCars() {
        return new ArrayList<Car>();
    }

    @ApiOperation(value = "Retrieves a car based on their id")
    @ApiResponse(code = 404, message = "No car corresponding to the id was found")
    @RequestMapping(method = RequestMethod.GET, value = "/{carId}")
    @ResponseBody
    public Car view(@ApiParam(name = "carId", required = true, value = "The id of the car that needs to be retrieved") @PathVariable("carId") String carId) {
        return new Car(carId, "name", "color", new ArrayList<Wheel>());
    }

    @ApiOperation(value = "Deletes a car based on their id")
    @RequestMapping(method = RequestMethod.DELETE, value = "/{carId}")
    @ResponseBody
    public Car delete(@ApiParam(name = "carId", value = "The id of the car to be deleted", required = true) @PathVariable("carId") String carId) {
        return new Car(carId, null, null, new ArrayList<Wheel>());
    }
}
