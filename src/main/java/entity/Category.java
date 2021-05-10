package entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

    @OneToMany(mappedBy = "category")
    private List<Option> options;

    private String name;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Product> getProducts() {
        return this.products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Option> getOptions() {
        return this.options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
