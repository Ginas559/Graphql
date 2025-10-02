package vn.iotstar.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(name = "UK_users_email", columnNames = "email")
       })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String fullname;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 30)
    private String phone;

    // N-N với Category (yêu cầu đề)
    @ManyToMany
    @JoinTable(
        name = "user_categories",
        joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_uc_user")),
        inverseJoinColumns = @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "FK_uc_category")),
        uniqueConstraints = @UniqueConstraint(name = "UK_user_categories", columnNames = {"user_id","category_id"})
    )
    private Set<Category> categories = new LinkedHashSet<>();

    // 1-N: User sở hữu nhiều Product
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Product> products = new ArrayList<>();

    public User() {}

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Set<Category> getCategories() { return categories; }
    public void setCategories(Set<Category> categories) { this.categories = categories; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}
