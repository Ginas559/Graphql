package vn.iotstar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PlaygroundController {

    // ĐỔI đường dẫn, KHÔNG dùng /playground nữa để tránh trùng
    @GetMapping({"/altair", "/graphiql", "/graphql-ui"})
    public String altair() {
        return "playground"; // templates/playground.html
    }
}
