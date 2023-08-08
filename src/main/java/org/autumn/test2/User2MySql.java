package org.autumn.test2;


import lombok.Data;
import org.autumn.annotation.db.Column;
import org.autumn.annotation.db.Id;
import org.autumn.annotation.db.Model;

@Model
@Data
public class User2MySql {

    @Id
    @Column(columnName = "id", notNull = true, autoIncrement = true)
    private long id;
    private String name;




}