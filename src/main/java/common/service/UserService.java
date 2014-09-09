package common.service;

import java.util.List;

import javax.jws.WebService;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import common.model.User;

/**
 * ユーザ処理のWEBサービス.
 */
@WebService
@Path("/users")
public interface UserService {

    /**
     * 全ユーザを取得する.
     *
     * @return ユーザのリスト
     */
    @GET
    List<User> getUsers();

    /**
     * 指定されたIDのユーザを取得する.
     *
     * @param userId
     *            ID
     * @return ユーザ
     */
    @GET
    @Path("{id}")
    User getUser(@PathParam("id") String userId);

    /**
     * 指定されたユーザ名のユーザを取得する.
     *
     * @param username
     *            ユーザ名
     * @return ユーザ
     */
    User getUserByUsername(@PathParam("username") String username);

    /**
     * 指定されたユーザを永続化する.
     *
     * @param user
     *            ユーザ
     * @return 永続化されたユーザ
     */
    @POST
    User saveUser(User user);

    /**
     * 指定されたIDのユーザを削除する.
     *
     * @param userId
     *            ID
     */
    @DELETE
    void removeUser(String userId);
}
