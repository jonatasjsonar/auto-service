package com.jsonar.secondservice.services;

import java.lang.Exception;
import java.lang.String;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

@Path("/uuid")
@Consumes("application/json")
@Produces("application/json")
public class UUIDService {
  @GET
  @Path("/random")
  public String randomUUID() {
    ResteasyClient client = (ResteasyClient) ClientBuilder.newBuilder().register(ResteasyJackson2Provider.class).build();
    ResteasyWebTarget target = client.target(UriBuilder.fromPath("http://localhost:8080/second"));
    UUIDServiceProxy proxy = target.proxy(UUIDServiceProxy.class);
    return proxy.randomUUID();
  }

  @GET
  @Path("/session")
  public String getSession() throws Exception {
    ResteasyClient client = (ResteasyClient) ClientBuilder.newBuilder().register(ResteasyJackson2Provider.class).build();
    ResteasyWebTarget target = client.target(UriBuilder.fromPath("http://localhost:8080/second"));
    UUIDServiceProxy proxy = target.proxy(UUIDServiceProxy.class);
    return proxy.getSession();
  }
}
