package vn.iotstar.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products",
       indexes = {
           @Index(name = "IX_products_price", columnList = "price")
       })
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length = 200)
    private String title;

    @Column(nullable=false)
    private Integer quantity;

    @Column(name="description", columnDefinition = "nvarchar(max)")
    private String desc;

    @Column(nullable=false, precision = 18, scale = 2)
    private BigDecimal price;

    // N-1: Product thuộc 1 User (userid)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
                foreignKey = @ForeignKey(name = "FK_product_user"))
    private User user;

    // N-1: Product thuộc 1 Category (để lấy product theo category)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",
                foreignKey = @ForeignKey(name = "FK_product_category"))
    private Category category;

    public Product() {}

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
