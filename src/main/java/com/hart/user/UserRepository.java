package com.hart.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by jameshart on 9/8/18.
 */
@RepositoryRestResource()  // <-- if you want no routes here then put this in the args  ->   exported = false
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}

