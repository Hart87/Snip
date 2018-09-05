package com.hart.link;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by jameshart on 9/4/18.
 */
public interface LinkRepository extends PagingAndSortingRepository<Link, Long> {

        //@RestResource(rel = "title-contains", path = "containsTitle")
        //Page<Course>findByTitleContaining(@Param("title") String title, Pageable page);
        Page<Link>findByLittle(@Param("little") String little, Pageable pageable);
}


