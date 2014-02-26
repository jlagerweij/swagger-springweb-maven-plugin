package org.example.controllers;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;

import org.example.model.User;
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
@RequestMapping(value = "/v1?0/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsersEndpoint {

    @ApiOperation(value = "Creates a User")
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public User create(@ApiParam(required = true, name = "user", value = "The user object that needs to be created") @RequestBody User user) {
        return new User(user.getId(), user.getName(), user.getEmailAddress());
    }

    @ApiOperation(value = "Method to update a user")
    @RequestMapping(method = RequestMethod.PUT, value = "/{userId}")
    @ResponseBody
    public User update(
            @ApiParam(required = true, value = "The id of the user that should be updated", name = "userId") @PathVariable("userId") String userId,
            @ApiParam(required = true, name = "user", value = "The user object that needs to be updated") @RequestBody User user) {

        return new User(userId, user.getName(), user.getEmailAddress());
    }

    @ApiOperation(value = "Gets all Users.")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<User> getUsers() {
        return new ArrayList<User>();
    }

    @ApiOperation(value = "Retrieves a user based on their id")
    @ApiResponse(code = 404, message = "No user corresponding to the id was found")
    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
    @ResponseBody
    public User view(@ApiParam(name = "userId", required = true, value = "The id of the user that needs to be retrieved") @PathVariable("userId") String userId) {
        return new User(userId, "name", "emailaddress@test.com");
    }

    @ApiOperation(value = "Deletes a user based on their id")
    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}")
    @ResponseBody
    public User delete(@ApiParam(name = "userId", value = "The id of the user to be deleted", required = true) @PathVariable("userId") String userId) {
        return new User(userId, null, null);
    }
}
