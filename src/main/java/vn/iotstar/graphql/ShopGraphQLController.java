package vn.iotstar.graphql;

import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import vn.iotstar.entity.*;
import vn.iotstar.repository.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
@Transactional
public class ShopGraphQLController {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final UserRepository userRepo;

    public ShopGraphQLController(ProductRepository productRepo,
                                 CategoryRepository categoryRepo,
                                 UserRepository userRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.userRepo = userRepo;
    }

    // ==========================
    // QUERIES
    // ==========================
    @QueryMapping
    public List<Product> productsSortedByPriceAsc() {
        return productRepo.findAllByOrderByPriceAsc();
    }

    @QueryMapping
    public List<Product> productsByCategory(@Argument Long categoryId) {
        return productRepo.findByCategory_Id(categoryId);
    }

    @QueryMapping
    public List<Product> products() {
        return productRepo.findAll();
    }

    @QueryMapping
    public List<Category> categories() {
        return categoryRepo.findAll();
    }

    @QueryMapping
    public List<User> users() {
        return userRepo.findAll();
    }

    @QueryMapping
    public Optional<User> user(@Argument Long id) {
        return userRepo.findById(id);
    }

    @QueryMapping
    public Optional<Category> category(@Argument Long id) {
        return categoryRepo.findById(id);
    }

    @QueryMapping
    public Optional<Product> product(@Argument Long id) {
        return productRepo.findById(id);
    }

    // ==========================
    // FIELD RESOLVERS (relations)
    // ==========================
    @SchemaMapping(typeName = "Product", field = "user")
    public User user(Product product) {
        return product.getUser();
    }

    @SchemaMapping(typeName = "Product", field = "category")
    public Category category(Product product) {
        return product.getCategory();
    }

    @SchemaMapping(typeName = "User", field = "products")
    public List<Product> products(User user) {
        return user.getProducts();
    }

    @SchemaMapping(typeName = "User", field = "categories")
    public Set<Category> categories(User user) {
        return user.getCategories();
    }

    @SchemaMapping(typeName = "Category", field = "products")
    public List<Product> products(Category c) {
        return c.getProducts();
    }

    @SchemaMapping(typeName = "Category", field = "users")
    public Set<User> users(Category c) {
        return c.getUsers();
    }

    // ==========================
    // MUTATIONS - USER
    // ==========================
    @MutationMapping
    public User createUser(@Argument String fullname,
                           @Argument String email,
                           @Argument String password,
                           @Argument String phone,
                           @Argument List<Long> categoryIds) {
        if (userRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        User u = new User();
        u.setFullname(fullname);
        u.setEmail(email);
        u.setPassword(password);
        u.setPhone(phone);

        if (categoryIds != null && !categoryIds.isEmpty()) {
            Set<Category> cats = new LinkedHashSet<>(categoryRepo.findAllById(categoryIds));
            u.setCategories(cats);
        }
        return userRepo.save(u);
    }

    @MutationMapping
    public User updateUser(@Argument Long id,
                           @Argument String fullname,
                           @Argument String email,
                           @Argument String password,
                           @Argument String phone,
                           @Argument List<Long> categoryIds) {
        User u = userRepo.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
        if (fullname != null) u.setFullname(fullname);
        if (email != null) {
            if (!email.equals(u.getEmail()) && userRepo.existsByEmail(email)) {
                throw new IllegalArgumentException("Email already exists");
            }
            u.setEmail(email);
        }
        if (password != null) u.setPassword(password);
        if (phone != null) u.setPhone(phone);

        if (categoryIds != null) {
            Set<Category> cats = new LinkedHashSet<>(categoryRepo.findAllById(categoryIds));
            u.setCategories(cats);
        }
        return userRepo.save(u);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        if (!userRepo.existsById(id)) return false;
        userRepo.deleteById(id);
        return true;
    }

    // ==========================
    // MUTATIONS - CATEGORY
    // ==========================
    @MutationMapping
    public Category createCategory(@Argument String name,
                                   @Argument String images) {
        Category c = new Category();
        c.setName(name);
        c.setImages(images);
        return categoryRepo.save(c);
    }

    @MutationMapping
    public Category updateCategory(@Argument Long id,
                                   @Argument String name,
                                   @Argument String images) {
        Category c = categoryRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Category not found"));
        if (name != null) c.setName(name);
        if (images != null) c.setImages(images);
        return categoryRepo.save(c);
    }

    @MutationMapping
    public Boolean deleteCategory(@Argument Long id) {
        if (!categoryRepo.existsById(id)) return false;
        categoryRepo.deleteById(id);
        return true;
    }

    // ==========================
    // MUTATIONS - PRODUCT
    // ==========================
    @MutationMapping
    public Product createProduct(@Argument String title,
                                 @Argument Integer quantity,
                                 @Argument String desc,
                                 @Argument BigDecimal price,
                                 @Argument Long userId,
                                 @Argument Long categoryId) {
        User u = (userId != null) ? userRepo.findById(userId).orElse(null) : null;
        Category c = (categoryId != null) ? categoryRepo.findById(categoryId).orElse(null) : null;

        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("price must be >= 0");
        }
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("quantity must be >= 0");
        }

        Product p = new Product();
        p.setTitle(title);
        p.setQuantity(quantity);
        p.setDesc(desc);
        p.setPrice(price);
        p.setUser(u);
        p.setCategory(c);
        return productRepo.save(p);
    }

    @MutationMapping
    public Product updateProduct(@Argument Long id,
                                 @Argument String title,
                                 @Argument Integer quantity,
                                 @Argument String desc,
                                 @Argument BigDecimal price,
                                 @Argument Long userId,
                                 @Argument Long categoryId) {
        Product p = productRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Product not found"));

        if (title != null) p.setTitle(title);
        if (quantity != null) {
            if (quantity < 0) throw new IllegalArgumentException("quantity must be >= 0");
            p.setQuantity(quantity);
        }
        if (desc != null) p.setDesc(desc);
        if (price != null) {
            if (price.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("price must be >= 0");
            p.setPrice(price);
        }
        if (userId != null) {
            User u = userRepo.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
            p.setUser(u);
        }
        if (categoryId != null) {
            Category c = categoryRepo.findById(categoryId).orElseThrow(() -> new NoSuchElementException("Category not found"));
            p.setCategory(c);
        }
        return productRepo.save(p);
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        if (!productRepo.existsById(id)) return false;
        productRepo.deleteById(id);
        return true;
    }
}
