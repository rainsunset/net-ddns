package com.rainsunset.netddns.config;

import com.rainsunset.netddns.service.request.TodoDdnsBO;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "todo-ddns")
@Data
public class TodoDdnsListConfig {
    private List<TodoDdnsBO> list;
}