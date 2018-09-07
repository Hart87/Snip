package com.hart.core;

import com.hart.aws.DBOpsLinks;
import com.hart.link.Link;
import com.hart.link.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

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
@Component
public class DatabaseLoader implements ApplicationRunner {

    private final LinkRepository links;

    @Autowired
    public DatabaseLoader(LinkRepository links) {
        this.links = links;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        //LINKS   ---- POPULATE FROM AWS DYNAMO DB ON START
        ArrayList<Link> h2Links = DBOpsLinks.ScanDB();
        links.save(h2Links);

//        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
//        Date dateobj = new Date();
//        String createdAt = df.format(dateobj);
//        links.save(new Link("http://www.newyorkjets.com", "HX3", "Twitter link for NY Jets", 20, createdAt));
    }
}
