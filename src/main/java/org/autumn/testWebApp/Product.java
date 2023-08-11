package org.autumn.testWebApp;

import lombok.Data;
import org.autumn.annotation.db.Column;
import org.autumn.annotation.db.Id;
import org.autumn.annotation.db.Model;

@Data
@Model
public class Product {

    @Id
    @Column(columnName = "id", autoIncrement = true)
    private long id;
    private String code;

}
