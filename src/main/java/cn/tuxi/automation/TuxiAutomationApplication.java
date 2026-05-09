package cn.tuxi.automation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class TuxiAutomationApplication {

    public static void main(String[] args) {
        SpringApplication.run(TuxiAutomationApplication.class, args);
    }
}
