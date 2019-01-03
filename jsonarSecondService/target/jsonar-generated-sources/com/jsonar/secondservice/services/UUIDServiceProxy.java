package com.jsonar.secondservice.services;

import java.lang.Exception;
import java.lang.String;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/uuid")
@Consumes("application/json")
@Produces("application/json")
public interface UUIDServiceProxy {
  @GET
  @Path("/random")
  String randomUUID();

  @GET
  @Path("/session")
  String getSession() throws Exception;
}
