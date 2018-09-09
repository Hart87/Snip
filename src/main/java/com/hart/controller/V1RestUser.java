package com.hart.controller;


import com.hart.aws.DBOpsUsers;
import com.hart.link.LinkRepository;
import com.hart.user.User;
import com.hart.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by
 *
 *
 *  \\\\    \\\\         \\\\\\         \\\\\\\\\\     \\\\\\\\\\\\
 *  \\\\    \\\\       \\\\  \\\\       \\\\    \\     \\\\\\\\\\\\
 *  \\\\\\\\\\\\     \\\\      \\\\     \\\\\\\\\\         \\\\
 *  \\\\\\\\\\\\     \\\\\\\\\\\\\\     \\\\\\             \\\\
 *  \\\\    \\\\     \\\\      \\\\     \\\\  \\           \\\\
 *  \\\\    \\\\     \\\\      \\\\     \\\\    \\         \\\\
 *
 *
 */
@RestController
//@RequestMapping("/routes/api/v1/")
public class V1RestUser {


    private static final Logger logger = LoggerFactory.getLogger(V1RestLink.class);
    private final LinkRepository links;
    private final UserRepository users;

    public V1RestUser(LinkRepository links, UserRepository users) {
        this.links = links;
        this.users = users;
    }


    // HELPER METHODS      ------------------------------

    public String CreateUserWithEmailValidation(String email) {  //TEST THIS L8ER
        //Run through all users to make sure
        //the new one created is unique by email.
        for (User user : users.findAll()) {
            User user2Validate = user;
            if (email.equals(user2Validate.getEmail())) {
                return "bad";
            }
        }
        return "good";
    }


    ///   R  E  S  T       -------------------------------

    //Create a user
    @RequestMapping(value = "routes/api/v1/users/new", method= RequestMethod.POST, produces = "application/json")
    public User CreateUser(
            @RequestParam("email") String emailParam,
            @RequestParam("password") String passwordParam,
            @RequestParam("username") String usernameParam,
            @RequestParam("image") String imageParam) {

        String validateBeforeCreate = CreateUserWithEmailValidation(emailParam);

        if (validateBeforeCreate.equals("good")) {
            DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            Date dateobj = new Date();
            String createdAt = df.format(dateobj);

            //H2 DB
            User newUser = new User(emailParam, passwordParam, usernameParam, new String[]{"ROLE_USER"}, 5, createdAt, imageParam);
            users.save(newUser);


            //AWS
            try {
                DBOpsUsers.AddUser(emailParam, passwordParam, usernameParam, 5, createdAt, imageParam);
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("AWS : DYNAMO DB - USERS - FAIL TO UPLOAD");
            }

            return newUser;

        } else {

            User badUser = new User("User already created with that name", "", "", new String[] {"ROLE_USER"}, 5, "", "");
            return badUser;
        }
    }

    //Find user by username
    @RequestMapping(value = "routes/api/v1/users/{username}", method= RequestMethod.GET, produces = "application/json")
    public User FindUser(@PathVariable("username") String username){

        User foundUser = users.findByUsername(username);

        return foundUser;
    }

    //Get all users
    @RequestMapping(value = "routes/api/v1/users", method= RequestMethod.GET, produces = "application/json")
    public ArrayList<User> GetUser(){
        ArrayList<User> userArrayList = new ArrayList<User>();

        for (User user : users.findAll()) {
            User newUser = user;
            userArrayList.add(newUser);
        }

        return userArrayList;
    }

    //Update a User........
    @RequestMapping(value = "routes/api/v1/users/{username}", method= RequestMethod.PUT, produces = "application/json")
    public User EditUser(
            @PathVariable("username") String username,
            @RequestParam("image") String image)
            //@RequestParam("password") String password)
            {

        User editedUser = users.findByUsername(username);

        editedUser.setImage(image);
        //editedUser.setPassword(password);

        //H2 EDIT AND SAVE
        users.save(editedUser);

        //AWS EDIT AND SAVE
        try {
            DBOpsUsers.UpdateUser(editedUser.getEmail(), editedUser.getImage()); //editedUser.getPassword()
        } catch (Exception e) {
            e.printStackTrace();
        }

        return editedUser;
    }

    //Delete link by little
    @RequestMapping(value = "routes/api/v1/users/{username}", method= RequestMethod.DELETE, produces = "application/json")
    public String DeleteLink(@PathVariable("username") String username){

        //H2 DB
        User user2delete = users.findByUsername(username);
        users.delete(user2delete);

        //AWS DYNAMO
        try {
            DBOpsUsers.DeleteUser(user2delete.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "User Deleted...";
    }


}
