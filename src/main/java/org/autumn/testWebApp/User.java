package org.autumn.testWebApp;

import lombok.Data;
import org.autumn.annotation.db.Column;
import org.autumn.annotation.db.Id;
import org.autumn.annotation.db.Model;

@Model
@Data
public class User {


    @Id
    @Column(columnName = "id", autoIncrement = true)
    private long id;
    private String name;
    private String surname;
    private String username;
    private String password;
    private int age;
    private boolean isActive;


}
