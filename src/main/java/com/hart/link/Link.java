package com.hart.link;



import com.hart.core.BaseEntity;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by
 *
 *
 *  \\\\    \\\\         \\\\\\         \\\\\\\\\\     \\\\\\\\\\\\
 *  \\\\    \\\\       \\\\  \\\\       \\\\    \\     \\\\\\\\\\\\
 *  \\\\\\\\\\\\     \\\\      \\\\     \\\\\\\\\\         \\\\
 *  \\\\\\\\\\\\     \\\\\\\\\\\\\\     \\\\\\             \\\\
 *  \\\\    \\\\     \\\\      \\\\     \\\\  \\           \\\\
 *  \\\\    \\\\     \\\\      \\\\     \\\\    \\         \\\\
 *
 *
 */
@Entity
public class Link extends BaseEntity {

    @NotNull
    @Size(min = 10, max = 1000)
    private String big;
    private String little;
    private String description;
    private Integer hit;
    //ADD USER LATER OVER HERE

    protected Link() {
        super();
        //reviews = new ArrayList<>();    Think about this line come user time
    }


    public Link(String big, String little, String description, Integer hit) {
        this.big = big;
        this.little = little;
        this.description = description;
        this.hit = hit;
    }

    public String getBig() {
        return big;
    }

    public void setBig(String big) {
        this.big = big;
    }

    public String getLittle() {
        return little;
    }

    public void setLittle(String little) {
        this.little = little;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getHit() {
        return hit;
    }

    public void setHit(Integer hit) {
        this.hit = hit;
    }
}
