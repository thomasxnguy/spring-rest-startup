package com.spring.security.impl.propertybased;

import com.spring.security.Authority;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "auth.conf")
public class PropertyFileUsersConfigurationProperties {

    private HashMap<String, String> users = new HashMap<>();
    private HashMap<String, List<Authority>> roles = new HashMap<>();

}
