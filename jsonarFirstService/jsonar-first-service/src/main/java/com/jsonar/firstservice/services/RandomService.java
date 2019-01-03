package com.jsonar.firstservice.services;

import com.jsonar.annotation.JSonarService;
import com.jsonar.firstservice.models.User;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@JSonarService
@Path("/random")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RandomService {

    private static final List<String> cityList = new ArrayList<>();
    {
        cityList.add("Vancouver");
        cityList.add("Toronto");
        cityList.add("Montreal");
        cityList.add("Ottawa");
        cityList.add("Calgary");
        cityList.add("Winnipeg");
    }

    @GET
    @Path("/int")
    public int randomInt() throws Exception {
        System.out.println(">>> Random Int");
        return randomInt(1, 100);
    }

    @GET
    @Path("/int-interval")
    public int randomInt(@QueryParam("min") int min, @QueryParam("max") int max) throws Exception {
        System.out.println(">>> Random Int min(" + min + "), max(" + max + ")");
        if (min > max) {
            throw new Exception("Max must be bigger than Min");
        }

        Random random = new Random(System.currentTimeMillis());
        return random.nextInt((max - min) + 1) + min;
    }

    @GET
    @Path("/city")
    public String randomCity() {
        System.out.println(">>> Random City");
        Random random = new Random(System.currentTimeMillis());
        return cityList.get(random.nextInt(cityList.size()));
    }

    @GET
    @Path("/user")
    public User randomUser() {
        System.out.println(">>> Random User");
        Random random = new Random(System.currentTimeMillis());
        return new User(UUID.randomUUID().toString(), 18 + random.nextInt(43), randomCity());
    }

    @POST
    @Path("/user")
    public User addUser(User user) {
        System.out.println(">>> Add User id(" + user.getId() + "), number(" + user.getNumber() + "), name(" + user.getCity() + ")");
        return user;
    }

    public List<User> randomUser(int amount) throws Exception {
        if (amount <= 0 || amount > 100) {
            throw new Exception("Invalid amount");
        }
        List<User> users = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            users.add(randomUser());
        }

        return users;
    }

    @GET
    @Path("/session")
    public String getSession() throws Exception {
        HttpServletRequest context = ResteasyProviderFactory.getContextData(HttpServletRequest.class);
        HttpSession session = context.getSession();
        ServletContext appContext = session.getServletContext().getContext("/webapp");

        String test = (String) appContext.getAttribute("testSession");

        if (StringUtils.isBlank(test)) {
            test = "first" + randomInt();
            appContext.setAttribute("testSession", test);
        }

        return test + " | SessionID: " + session.getId();
    }
}
