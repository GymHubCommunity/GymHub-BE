package com.example.temp.common.config;

import com.example.temp.GymHubBackApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan(basePackageClasses = GymHubBackApplication.class)
public class ScanningPropertiesConfiguration {

}
