package com.hart.controller;

import com.hart.aws.DBOpsLinks;
import com.hart.link.Link;
import com.hart.link.LinkRepository;
import com.hart.user.User;
import com.hart.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

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
public class V1RestLink {


    private static final Logger logger = LoggerFactory.getLogger(V1RestLink.class);
    private final LinkRepository links;
    private final UserRepository users;

    public V1RestLink(LinkRepository links, UserRepository users) {
        this.links = links;
        this.users = users;
    }


    //Generate Hex
    public String GetHex() {
        Random random = new Random();
        Integer num = random.nextInt(9000) + 1000;
        String result = Integer.toHexString(num);
        return result;
    }

    public String CreateLittleURLWithValidation() {  //TEST THIS L8ER
        //Generate hex
        String little = GetHex();

        //Run through all links to make sure
        //the new one created is unique.
        for (Link link : links.findAll()) {
            Link newLink = link;
            if (little.equals(newLink.getLittle())) {
                CreateLittleURLWithValidation(); //Recursion
            }
        }
        return little;
    }


    ///   R  E  S  T       -------------------------------

    //Create a Link
    @RequestMapping(value = "routes/api/v1/links", method= RequestMethod.POST, produces = "application/json")
    public Link CreateLinks(
            @RequestParam("big") String big,
            @RequestParam("description") String description ) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        User authedUser = users.findByUsername(username);

        String little = CreateLittleURLWithValidation();

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        String createdAt = df.format(dateobj);

        //H2 DB
        Link newLink = new Link(big, little, description, 0, createdAt, authedUser.getUsername());
        links.save(newLink);

        //AWS
        try {
            DBOpsLinks.AddLink(little, big, description, 0, createdAt, authedUser.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AWS : DYNAMO DB - LINKS - FAIL TO UPLOAD");
        }

        return newLink;
    }

    //Find link by little
    @RequestMapping(value = "/{little}", method= RequestMethod.GET, produces = "application/json")
    public Link FindLink(@PathVariable("little") String little){

        Link foundLink = links.findByLittle(little);
        Integer incr = foundLink.getHit();
        incr++;

        //H2 DB
        foundLink.setHit(incr);
        links.save(foundLink);

        //AWS DYNAMO
        try {
            //DBOps.IncrementCounter(little);
            DBOpsLinks.UpdateLinkHit(little, incr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return foundLink;
    }

    //Get all links
    @RequestMapping(value = "routes/api/v1/links", method= RequestMethod.GET, produces = "application/json")
    public ArrayList<Link> GetLink(){
        ArrayList<Link> linkArrayList = new ArrayList<Link>();

        for (Link link : links.findAll()) {
            Link newLink = link;
            linkArrayList.add(newLink);
        }

        return linkArrayList;
    }

    //Delete link by little
    @RequestMapping(value = "routes/api/v1/links/{little}", method= RequestMethod.DELETE, produces = "application/json")
    public String DeleteLink(@PathVariable("little") String little){

        //H2 DB
        Link foundLink = links.findByLittle(little);
        links.delete(foundLink);

        //AWS DYNAMO
        try {
            DBOpsLinks.DeleteLink(little);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Link Deleted...";
    }


    //Get all links by user
    @RequestMapping(value = "routes/api/v1/links/all/{username}", method= RequestMethod.GET, produces = "application/json")
    public ArrayList<Link> GetLinksByUsername(@PathVariable("username") String username) {
        ArrayList<Link> userLinksArrayList = new ArrayList<Link>();

        User authedUser = users.findByUsername(username);

        for (Link link : links.findAll()) {
            Link newLink = link;
            if (link.getUsername().equals(authedUser.getUsername())){
                userLinksArrayList.add(newLink);
            }
        }
        return userLinksArrayList;
    }


}
