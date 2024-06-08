package Shared.Models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryId")
    private Long categoryId;

    @Column(name = "Name", unique = true, nullable = false, length = 50)
    private Long name;

    public Long getCategoryId() {
        return categoryId;
    }
    public Long getName() {
        return name;
    }

    public void setName(Long name) {
        this.name = name;
    }
}
