package Shared.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "Categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryId")
    private Integer categoryId;

    @Column(name = "Name", unique = true, nullable = false, length = 50)
    private String name;

    public Integer getCategoryId() {
        return categoryId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category()
    {

    }
    public Category(String name)
    {
        this.name = name;
    }

}
