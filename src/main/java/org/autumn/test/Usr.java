package org.autumn.test;

import lombok.Data;
import org.autumn.annotation.db.Model;

@Data
@Model  // auto create
public class Usr {

    private String username;
    private int age;
    private String name;
    private String surname;
    private String password;
    private boolean active;

}
