package com.hart.controller;

import com.hart.aws.DBOps;
import com.hart.link.Link;
import com.hart.link.LinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/routes/api/v1/")
public class V1RestLink {


    private static final Logger logger = LoggerFactory.getLogger(V1RestLink.class);
    private final LinkRepository links;

    public V1RestLink(LinkRepository links) {
        this.links = links;
    }


    //Generate Hex
    public String GetHex() {
        Random random = new Random();
        Integer num = random.nextInt(9000) + 1000;
        String result = Integer.toHexString(num);
        return result;
    }


    ///   R  E  S  T       -------------------------------

    //Create a Link
    @RequestMapping(value = "/links", method= RequestMethod.POST, produces = "application/json")
    public Link CreateLinks(
            @RequestParam("big") String big,
            @RequestParam("description") String description ) {

        String little = GetHex();

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        String createdAt = df.format(dateobj);

        //H2 DB
        Link newLink = new Link(big, little, description, 0, createdAt);
        links.save(newLink);

        //AWS
        try {
            DBOps.AddLink(little, big, description, 0, createdAt);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AWS : DYNAMO DB - LINKS - FAIL TO UPLOAD");
        }

        return newLink;
    }

    //Find link by little
    @RequestMapping(value = "/links/{little}", method= RequestMethod.GET, produces = "application/json")
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
            DBOps.UpdateLinkHit(little, incr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return foundLink;
    }

    //Get all links
    @RequestMapping(value = "/links", method= RequestMethod.GET, produces = "application/json")
    public ArrayList<Link> GetLink(){
        ArrayList<Link> linkArrayList = new ArrayList<Link>();

        for (Link link : links.findAll()) {
            Link newLink = link;
            linkArrayList.add(newLink);
        }

        return linkArrayList;
    }

    //Delete link by little
    @RequestMapping(value = "/links/{little}", method= RequestMethod.DELETE, produces = "application/json")
    public String DeleteLink(@PathVariable("little") String little){

        Link foundLink = links.findByLittle(little);
        links.delete(foundLink);

        return "Link Deleted...";
    }


    //Get Link by User .........


}
