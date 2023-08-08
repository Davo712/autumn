package org.autumn.test;


import lombok.Data;
import org.autumn.annotation.db.Column;
import org.autumn.annotation.db.Id;
import org.autumn.annotation.db.Model;
import org.autumn.annotation.db.Transient;

@Model
@Data
public class User2 {

    @Id
    @Column(columnName = "id", notNull = true, autoIncrement = true)
    private long id;

    @Column(columnName = "namee", length = "255", notNull = true)
    private String name;
    private String surname;
    private String username;
    private String password;
    private int age;




    @Transient
    private String test;



}