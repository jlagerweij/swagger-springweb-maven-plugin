package org.example.controllers;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import org.example.model.Car;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/cars", produces = MediaType.APPLICATION_JSON_VALUE)
public class CarsEndpoint {

    @ApiOperation(value = "Gets all Cars.")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Car> getCars() {
        return new ArrayList<Car>();
    }
}
