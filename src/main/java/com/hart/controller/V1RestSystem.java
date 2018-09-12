package com.hart.controller;

import com.hart.link.LinkRepository;
import com.hart.user.UserRepository;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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
public class V1RestSystem {


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(V1RestSystem.class);
    private final LinkRepository links;
    private final UserRepository users;

    public V1RestSystem(LinkRepository links, UserRepository users) {
        this.links = links;
        this.users = users;
    }

    ///   R  E  S  T       -------------------------------
//Get a users IP address on startup and determine their location
    @RequestMapping(value = "/ipstart", method = RequestMethod.GET, produces = "application/json")
    public String GetLink(HttpServletRequest httpServletRequest) {


        String userIP = httpServletRequest.getRemoteAddr();
        String accessKey = "caf1fb34bc490773d6d0c6f2265e6040";

        //http://api.ipstack.com/24.146.233.114?access_key=caf1fb34bc490773d6d0c6f2265e6040
        String baseUrl = "http://api.ipstack.com/" + userIP + "?access_key=" + accessKey;

        //GET REQUEST
        String url = HttpUrl.parse(baseUrl).newBuilder().build().toString();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Didn't work";
    }
}

