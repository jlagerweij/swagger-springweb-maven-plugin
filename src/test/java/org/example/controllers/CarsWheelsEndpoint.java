package org.example.controllers;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;

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
@RequestMapping(value = "/cars/{carId}/wheels", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "", description = "Gives access to the wheels of the car")
public class CarsWheelsEndpoint {

    @ApiOperation(value = "Creates a Wheel")
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Wheel create(@ApiParam(required = true, name = "wheel", value = "The wheel object that needs to be created") @RequestBody Wheel wheel) {
        return new Wheel(wheel.getId(), wheel.getPosition());
    }

    @ApiOperation(value = "Method to update a wheel")
    @RequestMapping(method = RequestMethod.PUT, value = "/{wheelId}")
    @ResponseBody
    public Wheel update(
            @ApiParam(required = true, value = "The id of the wheel that should be updated", name = "wheelId") @PathVariable("wheelId") String wheelId,
            @ApiParam(required = true, name = "wheel", value = "The wheel object that needs to be updated") @RequestBody Wheel wheel) {

        return new Wheel(wheelId, wheel.getPosition());
    }

    @ApiOperation(value = "Gets all Wheels.")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Wheel> getWheels() {
        return new ArrayList<Wheel>();
    }

    @ApiOperation(value = "Retrieves a wheel based on their id")
    @ApiResponse(code = 404, message = "No wheel corresponding to the id was found")
    @RequestMapping(method = RequestMethod.GET, value = "/{wheelId}")
    @ResponseBody
    public Wheel view(@ApiParam(name = "wheelId", required = true, value = "The id of the wheel that needs to be retrieved") @PathVariable("wheelId") String wheelId) {
        return new Wheel(wheelId, "right-front");
    }

    @ApiOperation(value = "Deletes a wheel based on their id")
    @RequestMapping(method = RequestMethod.DELETE, value = "/{wheelId}")
    @ResponseBody
    public Wheel delete(@ApiParam(name = "wheelId", value = "The id of the wheel to be deleted", required = true) @PathVariable("wheelId") String wheelId) {
        return new Wheel(wheelId, null);
    }
}
