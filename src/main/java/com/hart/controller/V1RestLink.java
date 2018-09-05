package com.hart.controller;

import com.hart.link.Link;
import com.hart.link.LinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

        Link newLink = new Link("Http://www.newyorkjets.com/offense/samdarnold", little, "REDDIT QB Debate", 0);
        links.save(newLink);

        return newLink;
    }

    //Find link by little
    @RequestMapping(value = "/links/{little}", method= RequestMethod.GET, produces = "application/json")
    public Link FindLink(@PathVariable("little") String little){

        Link foundLink = links.findByLittle(little);
        Integer incr = foundLink.getHit();
        incr++;
        foundLink.setHit(incr);
        links.save(foundLink);

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
