package common.dao;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import common.model.User;

public interface UserDao extends GenericDao<User, Long> {

    @Transactional
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    List<User> getUsers();

    User saveUser(User user);

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getUserPassword(Long userId);
}
