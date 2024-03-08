package com.lan.module.infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author Kuen
 * @Date 2024/3/2 16:37
 */
@SpringBootApplication
@EnableDiscoveryClient
public class InfraServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(InfraServerApplication.class, args);
    }
}
