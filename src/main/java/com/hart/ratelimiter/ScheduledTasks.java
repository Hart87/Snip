package com.hart.ratelimiter;

import com.hart.user.User;
import com.hart.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
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
@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private final UserRepository users;

    @Autowired
    public ScheduledTasks(UserRepository users) {
        this.users = users;
    }

    @Scheduled(fixedRate = 20000)  //20 seconds
    public void ResetRequestsCount() {

        log.info("Rate Limit Reset : {}", dateFormat.format(new Date()));

        //Find every user and re-increment their req rate
        for(User user : users.findAll()) {
            User userRefresh = user;
            userRefresh.setRequests(5);
            users.save(userRefresh);
            log.info(userRefresh.getUsername() + " requests have been reset");
        }

    }
}