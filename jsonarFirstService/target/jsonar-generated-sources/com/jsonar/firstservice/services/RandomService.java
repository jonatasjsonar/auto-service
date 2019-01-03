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
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

@Path("/random")
@Produces("application/json")
@Consumes("application/json")
public class RandomService {
  @GET
  @Path("/int")
  public int randomInt() throws Exception {
    ResteasyClient client = (ResteasyClient) ClientBuilder.newBuilder().register(ResteasyJackson2Provider.class).build();
    ResteasyWebTarget target = client.target(UriBuilder.fromPath("http://localhost:8080/first"));
    RandomServiceProxy proxy = target.proxy(RandomServiceProxy.class);
    return proxy.randomInt();
  }

  @GET
  @Path("/int-interval")
  public int randomInt(@QueryParam("min") int min, @QueryParam("max") int max) throws Exception {
    ResteasyClient client = (ResteasyClient) ClientBuilder.newBuilder().register(ResteasyJackson2Provider.class).build();
    ResteasyWebTarget target = client.target(UriBuilder.fromPath("http://localhost:8080/first"));
    RandomServiceProxy proxy = target.proxy(RandomServiceProxy.class);
    return proxy.randomInt(min,max);
  }

  @GET
  @Path("/city")
  public String randomCity() {
    ResteasyClient client = (ResteasyClient) ClientBuilder.newBuilder().register(ResteasyJackson2Provider.class).build();
    ResteasyWebTarget target = client.target(UriBuilder.fromPath("http://localhost:8080/first"));
    RandomServiceProxy proxy = target.proxy(RandomServiceProxy.class);
    return proxy.randomCity();
  }

  @GET
  @Path("/user")
  public User randomUser() {
    ResteasyClient client = (ResteasyClient) ClientBuilder.newBuilder().register(ResteasyJackson2Provider.class).build();
    ResteasyWebTarget target = client.target(UriBuilder.fromPath("http://localhost:8080/first"));
    RandomServiceProxy proxy = target.proxy(RandomServiceProxy.class);
    return proxy.randomUser();
  }

  @POST
  @Path("/user")
  public User addUser(User user) {
    ResteasyClient client = (ResteasyClient) ClientBuilder.newBuilder().register(ResteasyJackson2Provider.class).build();
    ResteasyWebTarget target = client.target(UriBuilder.fromPath("http://localhost:8080/first"));
    RandomServiceProxy proxy = target.proxy(RandomServiceProxy.class);
    return proxy.addUser(user);
  }

  @GET
  @Path("/session")
  public String getSession() throws Exception {
    ResteasyClient client = (ResteasyClient) ClientBuilder.newBuilder().register(ResteasyJackson2Provider.class).build();
    ResteasyWebTarget target = client.target(UriBuilder.fromPath("http://localhost:8080/first"));
    RandomServiceProxy proxy = target.proxy(RandomServiceProxy.class);
    return proxy.getSession();
  }
}
