package by.bsuir.pandadb.web.helloworld;

import by.bsuir.pandadb.core.helloworld.HelloWorldService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping("/")
public class HelloWorldController {

    @Resource
    private HelloWorldService helloWorldService;

    @GetMapping
    private String getHelloWorld(Model model) {
        model.addAttribute("helloWorldAttribute", helloWorldService.getData());
        return "helloWorld";
    }

}

