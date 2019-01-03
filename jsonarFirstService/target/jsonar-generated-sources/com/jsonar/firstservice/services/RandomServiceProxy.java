package com.jsonar.firstservice.services;

import com.jsonar.firstservice.models.User;
import java.lang.Exception;
import java.lang.String;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/random")
@Produces("application/json")
@Consumes("application/json")
public interface RandomServiceProxy {
  @GET
  @Path("/int")
  int randomInt() throws Exception;

  @GET
  @Path("/int-interval")
  int randomInt(@QueryParam("min") int min, @QueryParam("max") int max) throws Exception;

  @GET
  @Path("/city")
  String randomCity();

  @GET
  @Path("/user")
  User randomUser();

  @POST
  @Path("/user")
  User addUser(User user);

  @GET
  @Path("/session")
  String getSession() throws Exception;
}
