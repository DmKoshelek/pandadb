package by.bsuir.pandadb.core.helloworld;

import org.springframework.stereotype.Service;

@Service
public class HelloWorldService {

    public String getData() {
        return "Hello World! (HelloWorldService)";
    }

}
