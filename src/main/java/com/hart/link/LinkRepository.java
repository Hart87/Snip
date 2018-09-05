package com.hart.link;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by jameshart on 9/4/18.
 */
public interface LinkRepository extends PagingAndSortingRepository<Link, Long> {

        //@RestResource(rel = "title-contains", path = "containsTitle")
        //Page<Link>findByLittle(@Param("little") String little, Pageable pageable);

        Link findByLittle(String little);
}


