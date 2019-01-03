package com.jsonar.secondservice.services;

import com.jsonar.annotation.JSonarService;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@JSonarService
@Path("/uuid")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class UUIDService {

    @GET
    @Path("/random")
    public String randomUUID() {
        return UUID.randomUUID().toString();
    }

    @GET
    @Path("/session")
    public String getSession() throws Exception {
        HttpServletRequest context = ResteasyProviderFactory.getContextData(HttpServletRequest.class);
        HttpSession session = context.getSession();
        ServletContext appContext = session.getServletContext().getContext("/webapp");

        String test = (String) appContext.getAttribute("testSession");

        if (StringUtils.isBlank(test)) {
            test = "second" + randomUUID();
            appContext.setAttribute("testSession", test);
        }

        return test + " | SessionID: " + session.getId();
    }

}
