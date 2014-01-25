package org.example.controllers;

import com.wordnik.swagger.annotations.ApiOperation;

import org.example.model.Vehicle;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/vehicle", produces = MediaType.APPLICATION_JSON_VALUE)
public class VehicleEndpoint {

    @ApiOperation(value = "Gets all Vehicles.")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Vehicle> getVehicles() {
        return new ArrayList<Vehicle>();
    }

}
