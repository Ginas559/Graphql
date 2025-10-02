package vn.iotstar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductsViewController {

    // Trang mặc định → list.html (trong admin/categories)
    @GetMapping({"/", "/products", "/list", "/list.html"})
    public String products() {
        return "admin/categories/list"; 
        // tương ứng src/main/resources/templates/admin/categories/list.html
    }

    // Trang AJAX demo
    @GetMapping({"/ajax", "/ajax.html"})
    public String ajax() {
        return "admin/fragments/ajax"; 
        // tương ứng src/main/resources/templates/admin/fragments/ajax.html
    }

    // Trang playground GraphQL
    @GetMapping({"/playground", "/playground.html"})
    public String playground() {
        return "playground"; 
        // tương ứng src/main/resources/templates/playground.html
    }
}
