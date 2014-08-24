package common.service;

import java.util.List;

import javax.jws.WebService;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import common.model.User;

@WebService
@Path("/users")
public interface UserService {

    @GET
    @Path("{id}")
    User getUser(@PathParam("id") String userId);

    User getUserByUsername(@PathParam("username") String username);

    @GET
    List<User> getUsers();

    @POST
    User saveUser(User user);

    @DELETE
    void removeUser(String userId);
}
