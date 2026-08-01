package com.acskii.client;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = ClientConfig.class)
public class ClientConfig {}