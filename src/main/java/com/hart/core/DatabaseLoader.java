package com.hart.core;

import com.hart.link.Link;
import com.hart.link.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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

        links.save(new Link("http://www.newyorkjets.com", "HX3", "Twitter link for NY Jets", 20));
    }
}
